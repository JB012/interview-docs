import { Component, computed, ElementRef, output, signal, ViewChild } from "@angular/core";

@Component({
    selector: 'video-controls',
    templateUrl: './video-controls.html'
})

export class VideoControls {
    pressRecord = signal(false);

    @ViewChild('videoControls')
    videoControlsElementRef! : ElementRef;

    startRecording = output<void>();
    stopRecording = output<void>();
    updateFullScreen = output<void>();
    
    intervalID?: number | null;
    videoDuration = signal(0);
    maxDuration = 60 * 10;

    minute = computed(() => Math.floor((this.videoDuration() % 3600) / 60).toString().padStart(2, '0'));
    second = computed(() => Math.floor(this.videoDuration() % 60).toString().padStart(2, '0'));
    
    startCounter() {
        this.intervalID ??= setInterval(() => {
            this.videoDuration.update(num => num + 1);

            if (this.videoDuration() === this.maxDuration + 1) {
                this.updatePressRecord();
            }
        }, 1000);
    }

    stopCounter() {
        if (this.intervalID) {    
            clearInterval(this.intervalID);
            this.videoDuration.set(0);
            this.intervalID = null;
        }
    }
    
    updatePressRecord() {
        const changedValue = !this.pressRecord();

        if (changedValue) {
            this.startRecording.emit();
            this.startCounter();
        }
        else {
            this.stopRecording.emit();
            this.stopCounter();
        }

        this.pressRecord.set(changedValue);
    }
}