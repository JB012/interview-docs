import { Component, inject, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { ActivatedRoute, Router, RouterOutlet } from "@angular/router";
import { QuestionService } from "../services/QuestionService";
import { QuestionType } from "../types/QuestionType";
import { Observable } from "rxjs/internal/Observable";
import { AsyncPipe } from "@angular/common";
import { AuthService } from "../services/AuthService";
import moment from "moment-timezone";
import { FolderSelectedDialog } from "../components/FolderSelectedDialog";
import { MatDialog } from "@angular/material/dialog";
import { FolderService } from "../services/FolderService";
import { FolderType } from "../types/FolderType";
import { forkJoin } from "rxjs";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Location } from '@angular/common';

@Component({
    selector: "question-page",
    imports: [
    MatButtonModule,
    AsyncPipe,
    RouterOutlet
],
    templateUrl: "./question-page.html"
})

export class QuestionPage {
    private activatedRoute = inject(ActivatedRoute);
    
    id = signal(0);
    option = signal('text');
    clickedNewVideo = signal(true);
    questionInput = signal('');
    answerInput = signal('');
    saveState = signal('');

    question$!: Observable<QuestionType>;

    allFolders : FolderType[] = [];
    selectedFolders : FolderType[] = [];

    timeoutID!: number;

    router = inject(Router);
    route = inject(ActivatedRoute);

    questionService = inject(QuestionService);
    folderService = inject(FolderService);

    user_id!: string;
    
    private _snackBar = inject(MatSnackBar);
    readonly dialog = inject(MatDialog);

    navigateToVideos() {
        const segments = this.route.snapshot.url;
        const urlPath = segments.map(segment => segment.path).join('/');

        if (!urlPath.includes("/videos")) {
            this.updateOption('video');
            this.router.navigate(['videos'], {relativeTo: this.route});
        }
    }

    updateQuestion(event : Event) {
        const targetId = (event.target as HTMLElement).id;

        if (targetId === "questionInput") {
            const text = (event.target as HTMLSpanElement).textContent;
            this.questionInput.set(text);
        }
        else if (targetId === "answerInput") {
            const text = (event.target as HTMLTextAreaElement).value;
            this.answerInput.set(text);
        }
        
        clearTimeout(this.timeoutID);

        const editedAt = moment().tz(moment.tz.guess(true)).format();

        this.timeoutID = setTimeout(() => {
            if (targetId === "questionInput") {
                const text = (event.target as HTMLSpanElement).textContent;
                this.questionService.putQuestion({question: text, edited_at: editedAt, user_id: this.user_id}, this.id())
                .subscribe();
            } 
            else if (targetId === "answerInput") {
                const text = (event.target as HTMLTextAreaElement).value;
                this.questionService.putQuestion({answer: text, edited_at: editedAt, user_id: this.user_id}, this.id()).subscribe();
            }
            
            this.saveState.set("saved");

            setTimeout(() => {
                this.saveState.set("");
            }, 3000);
        }, 2000);
    }
    
    constructor(public auth : AuthService, private _location: Location ) {
        this.activatedRoute.params.subscribe((params) => {
            this.id.set(parseInt(params['questionId']));
            
            this.question$ = this.questionService.getQuestion(this.id());

            this.question$.subscribe((res) => {
                this.questionInput.set(res.question!);
            });

            this.folderService.getFolders().subscribe((folders) => {
                this.allFolders = folders.content;
            });

            this.questionService.getFolders(this.id()).subscribe((selectedFolders) => {
                this.selectedFolders = selectedFolders;

                this.folderService.getFolders().subscribe((allFolders) => {
                    this.allFolders = allFolders.content.map(folder => {
                        if (selectedFolders.some(f => f.folder_id === folder.folder_id)) {
                            return {...folder, checked: true};
                        }
                        return {...folder, checked: false};
                    })
                })
            })

            auth.getCurrentUser().subscribe((res) => {
                this.user_id = res!.user!.claims.sub;
            });
        });
    }
    
    updateClickedNewVideo(newValue : boolean) {
        this.clickedNewVideo.set(newValue);
    } 

    updateOption(newValue : string) {
        this.option.set(newValue);

        if (newValue === "text") {
            const url = this.router.createUrlTree(["./"], {relativeTo: this.activatedRoute}).toString();
            this._location.go(url);
        }
    }

    openSnackBar() {
    this._snackBar.open("Changes saved", "Close", {
        duration: 5000
    });
}

    openDialog(): void {
        const dialogRef = this.dialog.open(FolderSelectedDialog, {
            data: {allFolders: this.allFolders},
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result !== undefined) {
                
                const checkedFolders : FolderType[] = result;

                const addQuestionToFolderTasks = checkedFolders.map(folder => this.folderService.postQuestionInFolder(folder.folder_id!, this.id()));
                const removeQuestionFromFolderTasks = this.selectedFolders
                .filter(folder => checkedFolders.some(f => f.folder_id !== folder.folder_id))
                .map(removedFolder => this.folderService.deleteQuestionInFolder(removedFolder.folder_id!, this.id()));
                
                forkJoin([...addQuestionToFolderTasks, ...removeQuestionFromFolderTasks]).subscribe({
                    next: () => {
                        this.openSnackBar();
                    },
                    error: (err) => console.error('One of the requests failed', err)
                });
            }
        });
    }
}