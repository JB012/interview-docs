import {
  Component,
  ViewChild,
  OnInit,
  ElementRef,
  signal
} from '@angular/core';
import { VideoControls } from "./VideoControls";
import { MatButtonModule } from '@angular/material/button';

declare var MediaRecorder: any;
@Component({
    selector: 'recorded-video',
    templateUrl: './recorded-video.html',
    imports: [
        VideoControls,
        MatButtonModule
    ]
})

export class RecordedVideo implements OnInit {
    @ViewChild('recordedVideo')
    recordedVideoElementRef!: ElementRef;
    @ViewChild('video')
    previewVideoElementRef!: ElementRef;
    @ViewChild('videoContainer')
    videoContainerRef!: ElementRef;

    previewVideoElement!: HTMLVideoElement;
    recordedVideoElement!: HTMLVideoElement;
    videoContainerElement!: HTMLDivElement;

    mediaRecorder: any;
    recordedBlobs: Blob[] = [];
    isRecording: boolean = false;
    downloadUrl!: string;
    stream!: MediaStream;
    
    videoLoaded = signal(false);
    currentVideo = signal('preview');
    inFullScreen = signal(false);
    
    constructor() {}

    async ngOnInit() {
        this.retrieveStream();
    }

    retrieveStream() {
        navigator.mediaDevices
        .getUserMedia({
            video: {
            width: 720
            },
            audio: true
        })
        .then(stream => {
            this.previewVideoElement = this.previewVideoElementRef.nativeElement;
            this.videoContainerElement = this.videoContainerRef.nativeElement;

            this.stream = stream;
            this.previewVideoElement.srcObject = this.stream;
            this.previewVideoElement.muted = true;

            this.videoLoaded.set(true);
        });

    }

    saveVideo() {
        
    }

    updateCurrentVideo(video : string) {
        this.currentVideo.set(video);
    }

    startRecording() {
        this.recordedBlobs = [];
        let options: any = { mimeType: 'video/webm' };

        try {
        this.mediaRecorder = new MediaRecorder(this.stream, options);
        } catch (err) {
        console.log(err);
        }

        this.mediaRecorder.start(); // collect 100ms of data
        this.isRecording = !this.isRecording;
        this.onDataAvailableEvent();
        this.onStopRecordingEvent();
    }

    stopRecording() {
        this.mediaRecorder.stop();
        this.isRecording = !this.isRecording;
        this.updateCurrentVideo('recorded');
        console.log('Recorded Blobs: ', this.recordedBlobs);
    }

    playRecording() {
        if (!this.recordedBlobs || !this.recordedBlobs.length) {
        console.log('cannot play.');
        return;
        }
        this.recordedVideoElement.play();
    }

    async updateFullScreen() {
        const updatedFullScreenValue = !this.inFullScreen();

        if (updatedFullScreenValue) {
            this.videoContainerElement.requestFullscreen();
        }
        else {
            await document.exitFullscreen();
        }

        this.inFullScreen.set(updatedFullScreenValue);
    }
    
    onDataAvailableEvent() {
        try {
        this.mediaRecorder.ondataavailable = (event: any) => {
            if (event.data && event.data.size > 0) {
            this.recordedBlobs.push(event.data);
            }
        };
        } catch (error) {
        console.log(error);
        }
    }

    onStopRecordingEvent() {
        try {
        this.mediaRecorder.onstop = (event: Event) => {
            const videoBuffer = new Blob(this.recordedBlobs, {
            type: 'video/webm'
            });
            
            this.downloadUrl = window.URL.createObjectURL(videoBuffer);
            
            this.recordedVideoElement = this.recordedVideoElementRef.nativeElement;
            this.recordedVideoElement.src = this.downloadUrl;
        };
        } catch (error) {
        console.log(error);
        }
    }
}