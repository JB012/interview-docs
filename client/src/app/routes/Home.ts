import { Component, effect, inject, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MenuButton } from "../components/MenuButton";
import { FolderSearch } from "../components/FolderSearch";
import { AsyncPipe } from '@angular/common';
import { Question } from "../components/Question";
import { QuestionType } from "../types/QuestionType";
import { Observable } from "rxjs";
import { QuestionService } from "../services/QuestionService";

@Component ({
    templateUrl: './home.html',
    imports: [
        MatPaginatorModule,
        MatButtonModule,
        MenuButton,
        FolderSearch,
        Question, 
        AsyncPipe
    ]
})

export class Home {
    questions$!: Observable<QuestionType[]>;

    private questionService = inject(QuestionService);
    
    constructor() {
        effect(() => {
        this.questions$ = this.questionService.getAllQuestions();
        });
    }
}