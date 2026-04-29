import { Component, DOCUMENT, effect, ElementRef, Inject, inject, OnInit, signal, ViewChild } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MenuButton } from "../components/MenuButton";
import { FolderSearch } from "../components/FolderSearch";
import { AsyncPipe } from '@angular/common';
import { Question } from "../components/Question";
import { QuestionType } from "../types/QuestionType";
import { Observable, shareReplay } from "rxjs";
import { QuestionService } from "../services/QuestionService";

import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { AuthService } from "@auth0/auth0-angular";
import { PagedQuestionType } from "../types/PagedQuestionType";
import {Router} from '@angular/router';

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

export class Home implements OnInit {  
    @ViewChild('questionInput')
    questionInput! : ElementRef<HTMLInputElement>;
    addQuestion$!: Observable<QuestionType>;
    userId? : string;
    pageEvent: PageEvent | undefined;
    
    private questionService = inject(QuestionService);
    questions$ = this.questionService.getQuestions().pipe(shareReplay(1)); 
    
    length = 0;
    pageSize = 0;
    pageIndex = 0;

    
    private router = inject(Router);

    constructor(public auth: AuthService) {}
    
    ngOnInit(): void {
        this.questions$.subscribe((questions) => {
            this.length = questions.page.totalElements;
            this.pageSize = questions.page.size;
            this.pageIndex = questions.page.number;
        });
    }

    handlePageEvent(e: PageEvent) {
        const pageSize = e.pageSize;
        const pageIndex = e.pageIndex;

        this.questions$ = this.questionService.getQuestions(pageIndex, pageSize);
    }

    addQuestion() {
        if (this.questionInput.nativeElement.value !== "") {
            if (this.auth.user$) {
                this.auth.user$.subscribe((user) => {
                    if (user) {
                        this.userId = user.sub;
                        if (this.userId) {
                            this.questionService.postQuestion({question: this.questionInput.nativeElement.value, user_id: this.userId}).subscribe((question) => {
                                this.router.navigate(['questions', question.id]);
                            });
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