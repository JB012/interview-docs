import { Component, computed, inject, input, signal, SimpleChanges } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { QuestionService } from "../services/QuestionService";
import { AuthService } from "../services/AuthService";
import { MatMenuModule, MatMenu } from '@angular/material/menu';
import moment from "moment-timezone";
import { QuestionType } from "../types/QuestionType";
import { FolderType } from "../types/FolderType";
import { FolderService } from "../services/FolderService";
import { VideoType } from "../types/VideoType";

@Component({
    selector: '[question], [folder], [video]',
    template:  `
        <td class="p-4 xxs:w-25 xs:w-50 sm:w-100 md:w-125 lg:w-150 xl:w-175 2xl:w-200"> 
            <div class="xxs:w-25 xs:w-50 sm:w-100 md:w-125 lg:w-150 xl:w-175 2xl:w-200 truncate">
                {{ title() }}
            </div>
        </td>
        <td>
           {{ editedAt() }}
        </td>
        <td>
            {{ viewedAt() }}
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

export class TableData {
    question = input<QuestionType>();
    folder = input<FolderType>();
    video = input<VideoType>();

    title = computed(() => {
        return [this.question()?.question, this.folder()?.name, this.video()?.title].find(v => v != undefined);
    });

    editedAt = computed(() => {
        const editedAt = [this.question()?.edited_at, this.folder()?.edited_at, this.video()?.edited_at].find(v => v != undefined);
        return editedAt ? new Date(editedAt).toLocaleDateString() : '';
    });

    viewedAt = computed(() => {
        const viewedAt = [this.question()?.viewed_at, this.folder()?.viewed_at, this.video()?.viewed_at].find(v => v != undefined);
        return viewedAt ? new Date(viewedAt).toLocaleDateString() : '';
    });

    router = inject(Router);
    route = inject(ActivatedRoute);

    questionService = inject(QuestionService);
    folderService = inject(FolderService);

    userId : string | undefined;
    optionMenu = signal(false);
    timeZone = moment.tz.guess(true);

    constructor(auth: AuthService) {
        auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
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