import { Component, inject, input } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { QuestionService } from "../services/QuestionService";
import { AuthService } from "../services/AuthService";
import moment from "moment-timezone";

@Component({
    selector: 'question',
    template:  `
    <li class="flex justify-between">
        <div (click)="navigateToQuestionPage()" class="truncate cursor-pointer p-2 w-full hover:backdrop-brightness-97"> 
            {{ this.question() }}
        </div>
        <span class="material-symbols-outlined">&#xe5cc;</span>
    </li>
        `
})

export class Question {
    id = input.required<number>();
    question = input.required<string>();
    router = inject(Router);
    route = inject(ActivatedRoute);
    questionService = inject(QuestionService);
    userId : string | undefined;

    constructor(auth: AuthService) {
        auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
    }

    navigateToQuestionPage() {
        this.questionService.putQuestion({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
            user_id: this.userId }, this.id()).subscribe(() => 
            {
                this.router.navigate(['questions', this.id()]);
            }
        );
    }
}