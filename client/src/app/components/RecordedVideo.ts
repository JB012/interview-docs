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
    recordVideoElementRef!: ElementRef;
    @ViewChild('video')
    videoElementRef!: ElementRef;
    @ViewChild('videoContainer')
    videoContainerRef!: ElementRef;

    videoElement!: HTMLVideoElement;
    recordVideoElement!: HTMLVideoElement;
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
            this.videoElement = this.videoElementRef.nativeElement;
            this.videoContainerElement = this.videoContainerRef.nativeElement;

            this.stream = stream;
            this.videoElement.srcObject = this.stream;
            this.videoElement.muted = true;

            this.videoLoaded.set(true);
        });

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
        this.recordVideoElement.play();
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
            this.downloadUrl = window.URL.createObjectURL(videoBuffer); // you can download with <a> tag
            
            this.recordVideoElement = this.recordVideoElementRef.nativeElement;
            this.recordVideoElement.src = this.downloadUrl;
        };
        } catch (error) {
        console.log(error);
        }
    }
}