import { Component, inject, model } from "@angular/core";
import { MatDialogContent, MatDialogActions, MatDialogRef, MAT_DIALOG_DATA, MatDialogClose } from "@angular/material/dialog";
import { MatInputModule } from "@angular/material/input";
import { FolderList } from "../routes/FolderList";
import { MatFormFieldModule } from "@angular/material/form-field";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";

@Component({
    templateUrl: './folder-dialog.html',
    imports: [      
        MatFormFieldModule,
        MatInputModule,
        FormsModule,
        MatButtonModule,
        MatDialogContent,
        MatDialogActions,
        MatDialogClose,
    ],
})

export class FolderDialog {
    readonly dialogRef = inject(MatDialogRef<FolderList>);
    readonly data = inject<{title: string}>(MAT_DIALOG_DATA);
    readonly title = model(this.data.title);

    onNoClick(): void {
        this.dialogRef.close();
    }
}