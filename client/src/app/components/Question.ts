import { Component, inject, input, signal } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { QuestionService } from "../services/QuestionService";
import { AuthService } from "../services/AuthService";
import { MatMenuModule, MatMenu } from '@angular/material/menu';
import moment from "moment-timezone";
import { QuestionType } from "../types/QuestionType";

@Component({
    selector: '[question]',
    template:  `
        <td class="p-4 xxs:w-25 xs:w-50 sm:w-100 md:w-125 lg:w-150 xl:w-175 2xl:w-200"> 
            <div class="xxs:w-25 xs:w-50 sm:w-100 md:w-125 lg:w-150 xl:w-175 2xl:w-200 truncate">
                {{ question().question }}
            </div>
        </td>
        <td>
           {{ getLastModified() }}
        </td>
        <td>
            {{ getCreatedAt() }}
        </td>
        <td>
            <span [matMenuTriggerFor]="menu" class="material-symbols-outlined xxs xs:material-symbols-outlined xs lg:material-symbols-outlined lg">&#xe5d4;</span>
            <mat-menu #menu="matMenu">
            <button mat-menu-item>Open in a new tab</button>
            <button mat-menu-item>Delete</button>
            </mat-menu>
        </td>
        `,
    imports: [MatMenu, MatMenuModule]
})

export class Question {
    question = input.required<QuestionType>();
    router = inject(Router);
    route = inject(ActivatedRoute);
    questionService = inject(QuestionService);
    userId : string | undefined;
    optionMenu = signal(false);
    timeZone = moment.tz.guess(true);

    constructor(auth: AuthService) {
        auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
    }

    getLastModified() {
        const editedAt = new Date(this.question().edited_at!);

        return editedAt.toLocaleDateString();
    }

    getCreatedAt() {
        const createdAt = new Date(this.question().created_at!);

        return createdAt.toLocaleDateString();
    }

    getLastViewed() {
        const lastViewed = new Date(this.question().viewed_at!);

        return lastViewed.toLocaleDateString();
    }

    updateOptions() {
        this.optionMenu.update(val => !val);        
    }
    
    clickOutside() {
        if (this.optionMenu()) {
            this.optionMenu.set(false);
        }
    }
}