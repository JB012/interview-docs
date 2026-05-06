import {
  Component,
  ViewChild,
  OnInit,
  ElementRef,
  signal,
  inject
} from '@angular/core';
import { VideoControls } from "./VideoControls";
import { MatButtonModule } from '@angular/material/button';
import { VideoService } from '../services/VideoService';
import { PutObjectCommand } from '@aws-sdk/client-s3';
import { getUserIdNumber, s3Client } from  '../../utils';
import { AuthService } from '@auth0/auth0-angular';

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
    disableSave = signal(false);

    videoService = inject(VideoService);
    
    userID: string | undefined;

    constructor(public auth: AuthService) {
    }

    async ngOnInit() {
        this.retrieveStream();

        this.auth.user$.subscribe((user) => {
            if (user?.sub) {  
                this.userID = getUserIdNumber(user?.sub);
            }
        })
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

    async saveVideo() {
        try {          
            this.videoService.postVideo({user_id: this.userID, title: `title`}).subscribe(async () => {
                const videoBuffer = new Blob(this.recordedBlobs, {type: 'video/webm'});
                const videoFile = new File([videoBuffer], "test.mp4", {type: 'video/webm'});

                const command = new PutObjectCommand({
                    Key: `${this.userID}/title`,
                    Bucket: 'interviewdocs-videos',
                    Body: videoFile,
                    ContentType: videoFile.type
                });

                await s3Client.send(command);
                
                // show confirmation to client side that video has been saved, then disable save button
                this.disableSave.set(true);
            });
        }
        catch (err) {
            console.log(err);
        }
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
        if (document.fullscreenElement) {
            document.exitFullscreen();
            this.inFullScreen.set(false);
        }

        this.mediaRecorder.stop();
        this.isRecording = !this.isRecording;
        this.updateCurrentVideo('recorded');
        this.disableSave.set(false);
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