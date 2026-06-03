import { Component, inject, model } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogContent, MatDialogActions, MatDialogClose, MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { FolderPage } from "../routes/FolderPage";
import {MatCheckboxChange, MatCheckboxModule} from '@angular/material/checkbox';
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { QuestionType } from "../types/QuestionType";

@Component({
    templateUrl: './question-list-dialog.html',
    imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatCheckboxModule,
    MatPaginatorModule
],
})

export class QuestionListDialog {
    readonly dialogRef = inject(MatDialogRef<FolderPage>);
    readonly data = inject<{allQuestions: QuestionType[]}>(MAT_DIALOG_DATA);
    readonly allQuestions = model(this.data.allQuestions);
    filteredQuestions = this.allQuestions();
    selectedQuestions : QuestionType[] = [];

    onNoClick(): void {
        this.dialogRef.close();
    }

    handleFilter(event : Event) {
        const input = (event.target as HTMLInputElement).value;

        if (!input) {
            this.filteredQuestions = this.allQuestions();
        }
        else {
            this.filteredQuestions = this.allQuestions().filter(question => 
                question.question?.toLowerCase().includes(input.toLowerCase()))
        }
    }

    onCheckboxChange(event : MatCheckboxChange, question : QuestionType) {
        if (event.checked) {
            this.selectedQuestions.push(question);
        } 
        else {
            const index = this.selectedQuestions.findIndex(x => x.id === question.id);
            if (index > -1) {
            this.selectedQuestions.splice(index, 1);
            }
        }
    }
}