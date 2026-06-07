import { Component, inject, model } from "@angular/core";
import { TableData } from "./TableData";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogClose } from "@angular/material/dialog";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

@Component({
    templateUrl: './delete-dialog.html',
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

export class DeleteDialog {
    readonly dialogRef = inject(MatDialogRef<TableData>);

    onNoClick(): void {
        this.dialogRef.close();
    }
}