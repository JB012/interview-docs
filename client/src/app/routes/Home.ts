import { Component, DOCUMENT, effect, ElementRef, Inject, inject, OnInit, signal, ViewChild } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MenuButton } from "../components/MenuButton";
import { AsyncPipe } from '@angular/common';
import { Question } from "../components/Question";
import { QuestionType } from "../types/QuestionType";
import { Observable, shareReplay } from "rxjs";
import { QuestionService } from "../services/QuestionService";

import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { PagedQuestionType } from "../types/PagedQuestionType";
import {Router} from '@angular/router';
import { AuthService } from "../services/AuthService";
import { sortFields, orderDirection } from "../../utils";
import moment from "moment-timezone";

@Component ({
    templateUrl: './home.html',
    imports: [
        MatPaginatorModule,
        MatButtonModule,
        MenuButton,
        AsyncPipe,
        Question,
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
    
    sortValue = signal("Last viewed");
    orderValue = signal("Newest first");

    length = 0;
    pageSize = 0;
    pageIndex = 0;

    updateQuestions = () => {
        this.questions$ = this.questionService.getQuestions(this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }

    private router = inject(Router);

    constructor(public auth: AuthService) {
        this.auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
    }
    
    ngOnInit(): void {
        this.questions$.subscribe((questions) => {
            this.length = questions.page.totalElements;
            this.pageSize = questions.page.size;
            this.pageIndex = questions.page.number;
        });
    }

    handlePageEvent(e: PageEvent) {
        this.pageSize = e.pageSize;
        this.pageIndex = e.pageIndex;

        this.questions$ = this.questionService.getQuestions(this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }

    addQuestion() {
       this.questionService.postQuestion({user_id: this.userId})
        .subscribe((question) => {
            this.router.navigate(['questions', question.id]);
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
    
}