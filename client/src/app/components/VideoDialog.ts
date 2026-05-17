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
import { RecordedVideo } from "./RecordedVideo";
import { MatButtonModule } from "@angular/material/button";
import { FormsModule } from "@angular/forms";

@Component({
    selector: 'video-dialog',
    templateUrl: 'video-dialog.html',
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

export class VideoDialog {
    readonly dialogRef = inject(MatDialogRef<RecordedVideo>);
    readonly data = inject<{title: string}>(MAT_DIALOG_DATA);
    readonly title = model(this.data.title);

  onNoClick(): void {
    this.dialogRef.close();
  }
}