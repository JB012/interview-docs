import { Component, inject, model } from "@angular/core";
import { MatCheckboxChange, MatCheckboxModule } from "@angular/material/checkbox";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent } from "@angular/material/dialog";
import { QuestionType } from "../types/QuestionType";
import { QuestionPage } from "../routes/question-page";
import { FolderType } from "../types/FolderType";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatPaginatorModule } from "@angular/material/paginator";

@Component({
    templateUrl: './folder-selected-dialog.html',
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

export class FolderSelectedDialog {
    readonly dialogRef = inject(MatDialogRef<QuestionPage>);
    readonly data = inject<{allFolders: FolderType[]}>(MAT_DIALOG_DATA);
    readonly allFolders = this.data.allFolders;
    filteredFolders = this.allFolders;
    selectedFolders : FolderType[] = this.allFolders.filter((folder) => folder.checked);

    onNoClick(): void {
        this.dialogRef.close();
    }

    handleFilter(event : Event) {
        const input = (event.target as HTMLInputElement).value;

        if (!input) {
            this.filteredFolders = this.allFolders;
        }
        else {
            this.filteredFolders = this.allFolders.filter(folder => {
                console.log(folder.title?.toLowerCase().includes(input.toLowerCase()));
                return folder.title?.toLowerCase().includes(input.toLowerCase());
            });
        }
    }

    onCheckboxChange(event : MatCheckboxChange, folder : FolderType) {
        if (event.checked) {
            this.selectedFolders.push(folder);
        } 
        else {
            const index = this.selectedFolders.findIndex(x => x.folder_id === folder.folder_id);
            if (index > -1) {
            this.selectedFolders.splice(index, 1);
            }
        }
    }
}