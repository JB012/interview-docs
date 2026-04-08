import { Component, signal } from "@angular/core";

@Component({
    selector: 'video-controls',
    templateUrl: './video-controls.html'
})

export class VideoControls {
    pressRecord = signal(false);

    updatePressRecord() {
        this.pressRecord.update((value) => !value);
    }
}