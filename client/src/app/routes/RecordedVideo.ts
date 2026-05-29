import {
  Component,
  ViewChild,
  OnInit,
  ElementRef,
  signal,
  inject,
  model
} from '@angular/core';
import { VideoControls } from "../components/VideoControls";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { VideoService } from '../services/VideoService';
import { PutObjectCommand } from '@aws-sdk/client-s3';
import { getUserIdNumber, s3Client } from  '../../utils';
import { VideoDialog } from '../components/VideoDialog';
import { AuthService } from '../services/AuthService';
import { ActivatedRoute, Router } from '@angular/router';
import moment from 'moment-timezone';
import { Observable } from 'rxjs';
import { VideoType } from '../types/VideoType';
import { AsyncPipe } from '@angular/common';
declare var MediaRecorder: any;

@Component({
    selector: 'recorded-video',
    templateUrl: './recorded-video.html',
    imports: [
    VideoControls,
    MatButtonModule,
    AsyncPipe
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
    
    private activatedRoute = inject(ActivatedRoute);
    videoService = inject(VideoService);
    private _videoSnackBar = inject(MatSnackBar);
    readonly dialog = inject(MatDialog);

    userID: string | undefined;
    
    timeCreated! : string;

    questionId! : number;
    videoTitle = signal('');
    video$ : Observable<VideoType> | undefined;


    router = inject(Router);
    route = inject(ActivatedRoute);

    constructor(public auth: AuthService) {
         this.activatedRoute.params.subscribe((params) => {
            this.questionId = parseInt(params['questionId']);

            if (params['videoId']) {
                this.video$ = this.videoService.getVideo(parseInt(params['videoId']));
            }
         });

         
    }

    navigateToVideoList() {
        this.router.navigate(['..'], {relativeTo: this.route});
    }

    openVideoSnackBar(message : string) {
        this._videoSnackBar.open(message, "Close", {
            duration: 5000
        });
    }

    openDialog(): void {
        const dialogRef = this.dialog.open(VideoDialog, {
        data: {title: this.videoTitle()},
        });

        dialogRef.afterClosed().subscribe(result => {
        if (result !== undefined) {
            this.videoTitle.set(result);
            this.saveVideo();
        }
        });
    }

    async ngOnInit() {
        this.retrieveStream();

        this.auth.getCurrentUser().subscribe((res) => {
            const user = res?.user;

            if (user?.claims.sub) {  
                this.userID = getUserIdNumber(user?.claims.sub);
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
            this.openVideoSnackBar("Saving..."); 
            const title = this.videoTitle().replaceAll(' ', '_');

            this.videoService.postVideo({user_id: this.userID, created_at: this.timeCreated, question_id: this.questionId, title: title}).subscribe(async () => {
                const videoBuffer = new Blob(this.recordedBlobs, {type: 'video/webm'});
                const videoFile = new File([videoBuffer], "test.mp4", {type: 'video/webm'});

                const command = new PutObjectCommand({
                    Key: `${this.userID}/${title}`,
                    Bucket: 'interviewdocs-videos',
                    Body: videoFile,
                    ContentType: videoFile.type
                });

                await s3Client.send(command);
        
                this.disableSave.set(true);
                this.openVideoSnackBar("Video saved!");
            });

            
            this.videoTitle.set("");
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

        this.timeCreated = moment().tz(moment.tz.guess(true)).format();
        
        console.log(this.timeCreated);

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