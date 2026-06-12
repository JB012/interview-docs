import { Component, inject, signal } from "@angular/core";
import { MatAnchor } from "@angular/material/button";
import { FolderService } from "../services/FolderService";
import { AsyncPipe } from "@angular/common";
import { FolderType } from "../types/FolderType";
import { QuestionService } from "../services/QuestionService";
import { QuestionType } from "../types/QuestionType";
import { FormsModule } from "@angular/forms";
import { shareReplay } from "rxjs";

@Component({
    templateUrl: './session-mode.html',
    imports: [
        MatAnchor,
        AsyncPipe,
        FormsModule
    ]
})

export class SessionMode {
    folderRadioValue = "no_folder";
    folderService = inject(FolderService);
    questionService = inject(QuestionService);
    folders$ = this.folderService.getAllFolders().pipe(shareReplay(1));
    totalQuestions$ = this.questionService.getAllQuestions().pipe(shareReplay(1));
    selectedFolderId = 0;
    questionsInFolder : QuestionType[] | undefined;
    

    onFolderChange() {
        this.folderService.getAllQuestionsInFolder(this.selectedFolderId).subscribe((questions) => {
            this.questionsInFolder = questions;
        })
    }
}