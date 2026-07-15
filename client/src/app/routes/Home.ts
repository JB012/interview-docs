import { Component, ElementRef, inject, OnInit, signal, ViewChild } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MenuButton } from "../components/MenuButton";
import { AsyncPipe } from '@angular/common';
import { PagedQuestionType, QuestionType } from "../types/QuestionType";
import { Observable, shareReplay, Subject } from "rxjs";
import { QuestionService } from "../services/QuestionService";
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {Router} from '@angular/router';
import { AuthService } from "../services/AuthService";
import { sortFields, orderDirection } from "../../utils";
import moment from "moment-timezone";
import { Table } from "../components/Table";

@Component ({
    templateUrl: './home.html',
    imports: [
    MatPaginatorModule,
    MatButtonModule,
    MenuButton,
    AsyncPipe,
    MatProgressSpinnerModule,
    Table
]
})

export class Home implements OnInit {  
    @ViewChild('questionInput')
    questionInput! : ElementRef<HTMLInputElement>;
    addQuestion$!: Observable<QuestionType>;
    userId? : string;
    pageEvent: PageEvent | undefined;
    
    private questionService = inject(QuestionService);
    private questionSource = new Subject<PagedQuestionType>();
    questions$ = this.questionSource.asObservable();

    sortValue = signal("Last viewed");
    orderValue = signal("Newest first");

    addButtonDisable = signal(false);

    length = 0;
    pageSize = 0;
    pageIndex = 0;

    updateQuestions = () => {
        const field = this.sortValue() === "Alphabetical" ? "question" : sortFields[this.sortValue()];
        this.questionService.getQuestions(this.pageIndex, this.pageSize, 
            {field: field, direction: orderDirection[this.orderValue()]})
        .subscribe((questions) => {
            this.questionSource.next(questions);
            
            this.length = questions.totalSize;
            this.pageSize = questions.pageSize
            this.pageIndex = questions.pageNumber;
        })
    }

    private router = inject(Router);

    constructor(public auth: AuthService) {
        this.auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user;
        });
    }
    
    ngOnInit(): void {
        this.questionService.getQuestions().subscribe((questions) => {
            this.length = questions.totalSize;
            this.pageSize = questions.pageSize
            this.pageIndex = questions.pageNumber;

            this.questionSource.next(questions);
        });
    }

    handlePageEvent(e: PageEvent) {
        this.pageSize = e.pageSize;
        this.pageIndex = e.pageIndex;

        this.updateQuestions();
    }

    addQuestion() {
        const currentTime = moment().tz(moment.tz.guess(true)).format();
        this.questionService.postQuestion({user_id: this.userId, question: 'What is your question?', answer: "", 
            viewed_at: currentTime, edited_at: currentTime})
        .subscribe((question) => {
            this.router.navigate(['questions', question.question_id]);
        });
    }

    navigateToQuestionPage(id: number) {
        this.addButtonDisable.set(true);
        
        this.questionService.putQuestion({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
            user_id: this.userId }, id).subscribe(() => 
            {
                this.router.navigate(['questions', id]);
            }
        );
    }
    
}