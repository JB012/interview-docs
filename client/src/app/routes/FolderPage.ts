import { Component, inject, Signal, signal } from "@angular/core";
import { AuthService } from "../services/AuthService";
import { BehaviorSubject, forkJoin, map, Observable, shareReplay, Subject, switchMap } from "rxjs";
import { PagedQuestionType, QuestionType } from "../types/QuestionType";
import { ActivatedRoute, Router } from "@angular/router";
import { FolderService } from "../services/FolderService";
import { orderDirection, sortFields } from "../../utils";
import { MenuButton } from "../components/MenuButton";
import { AsyncPipe } from "@angular/common";
import { FolderType } from "../types/FolderType";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { Table } from "../components/Table";
import { MatMenu, MatMenuModule } from "@angular/material/menu";
import { QuestionService } from "../services/QuestionService";
import { MatDialog } from "@angular/material/dialog";
import { QuestionListDialog } from "../components/QuestionListDialog";
import {toSignal} from '@angular/core/rxjs-interop';
import moment from "moment-timezone";

@Component({
    templateUrl: './folder-page.html',
    imports: [
    MenuButton,
    AsyncPipe,
    MatProgressSpinner,
    MatPaginator,
    Table,
    MatMenu,
    MatMenuModule
]
})

export class FolderPage {
    id = signal(0);
    sortValue = signal("Last viewed");
    orderValue = signal("Newest first");

    private activatedRoute = inject(ActivatedRoute);
    private folderService = inject(FolderService);
    private questionService = inject(QuestionService);
    private router = inject(Router);
    readonly dialog = inject(MatDialog);

    loading = signal(false);
    private questionSource = new Subject<PagedQuestionType>();
    questionsInFolder$ = this.questionSource.asObservable();
    allQuestions : QuestionType[] | undefined;

    folder$: Observable<FolderType> | undefined;

    pageSize = 10;
    pageIndex = 0;
    userId: string | undefined;

    ngOnInit() {
        this.questionsInFolder$.subscribe((questions) => {
            this.pageSize = questions.page.size;
            this.pageIndex = questions.page.number;
        })
    }

    constructor(public auth : AuthService ) {
        this.activatedRoute.params.subscribe((params) => {
            this.id.set(parseInt(params['id']));
            
            this.folderService.getQuestionsInFolder(parseInt(params['id'])).subscribe((questions) => {
                this.questionSource.next(questions);
            });

            this.folder$ = this.folderService.getFolder(parseInt(params['id']));
        });

       this.questionService.getAllQuestions().pipe(shareReplay(1)).subscribe(questions => {
            this.allQuestions = questions.map(question => ({...question, checked: false}));
        });

        auth.getCurrentUser().subscribe((res) => {
            this.userId = res?.user?.claims.sub;
        })
    } 
    
    handlePageEvent(e: PageEvent) {
        this.pageSize = e.pageSize;
        this.pageIndex = e.pageIndex;

        this.updateQuestions();
    }

    updateQuestions = () => {
        this.folderService.getQuestionsInFolder(this.id(), this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]})
            .subscribe((questions) => {
                this.questionSource.next(questions);
            })
    }

    createNewQuestion() {
        const currentTime = moment().tz(moment.tz.guess(true)).format();
        this.questionService.postQuestion({user_id: this.userId, question: "What is your question?", viewed_at: currentTime, edited_at: currentTime})
            .subscribe((question) => {
                this.folderService.postQuestionInFolder(this.id(), question.id!).subscribe(() => {
                    this.router.navigate(['questions', question.id]);
                });
        });
    }

    navigateToQuestionPage(id: number) {
        this.questionService.putQuestion({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
            user_id: this.userId }, id).subscribe(() => 
            {
                this.router.navigate(['questions', id]);
            }
        );
    }

    openDialog(): void {
        const dialogRef = this.dialog.open(QuestionListDialog, {
            data: {allQuestions: this.allQuestions ?? []},
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result !== undefined) {
                this.loading.set(true);
                
                const questions : QuestionType[] = result;

                const tasks = questions.map(question => this.folderService.postQuestionInFolder(this.id(), question.id!));

                forkJoin(tasks).subscribe({
                    next: () => {
                        this.updateQuestions();
                        this.loading.set(false);
                    },
                    error: (err) => console.error('One of the requests failed', err)
                });
            }
        });
    }
}