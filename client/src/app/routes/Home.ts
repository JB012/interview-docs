import { Component, DOCUMENT, effect, ElementRef, Inject, inject, signal, ViewChild } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MenuButton } from "../components/MenuButton";
import { FolderSearch } from "../components/FolderSearch";
import { AsyncPipe } from '@angular/common';
import { Question } from "../components/Question";
import { QuestionType } from "../types/QuestionType";
import { Observable } from "rxjs";
import { QuestionService } from "../services/QuestionService";

import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { AuthService } from "@auth0/auth0-angular";
@Component ({
    templateUrl: './home.html',
    imports: [
        MatPaginatorModule,
        MatButtonModule,
        MenuButton,
        FolderSearch,
        Question, 
        AsyncPipe,
        MatProgressSpinnerModule
    ]
})

export class Home {  
    @ViewChild('questionInput')
    questionInput! : ElementRef<HTMLInputElement>;
    questions$!: Observable<QuestionType[]>;
    addQuestion$!: Observable<QuestionType>;
    userId? : string;

    private questionService = inject(QuestionService);
    
    constructor(@Inject(DOCUMENT) private doc: Document, public auth: AuthService) {
        effect(() => {
            this.questions$ = this.questionService.getAllQuestions();
        });
    }

    addQuestion() {
        if (this.questionInput.nativeElement.value !== "") {
            if (this.auth.user$) {
                this.auth.user$.subscribe((user) => {
                    if (user) {
                        this.userId = user.sub;
                        if (this.userId) {
                            this.questionService.postQuestion({question: this.questionInput.nativeElement.value, user_id: this.userId}).subscribe();
                        }
                    }
                    else {
                        console.log('user not authorized');
                    }
                });
            }
            else {
                // unauthenticated; use local storage
                console.log('unauthorized');
            }
        }
        else {
            console.log('failed');
        }
    }
}