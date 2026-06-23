import { Component, inject, model, signal } from "@angular/core";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { RecordedVideo } from "../routes/RecordedVideo";
import { MatButtonModule } from "@angular/material/button";
import { FormsModule } from "@angular/forms";

@Component({
    selector: 'title-dialog',
    templateUrl: 'title-dialog.html',
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

export class TitleDialog {
    readonly dialogRef = inject(MatDialogRef<RecordedVideo>);
    readonly data = inject<{title: string}>(MAT_DIALOG_DATA);
    readonly title = model(this.data.title);

  onNoClick(): void {
    this.dialogRef.close();
  }
}