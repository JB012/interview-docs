import { Component, computed, ElementRef, inject, signal, ViewChild } from "@angular/core";
import { MatAnchor } from "@angular/material/button";
import { FolderService } from "../services/FolderService";
import { AsyncPipe, NgClass } from "@angular/common";
import { FolderType } from "../types/FolderType";
import { QuestionService } from "../services/QuestionService";
import { QuestionType } from "../types/QuestionType";
import { FormsModule } from "@angular/forms";
import { interval, Observable, shareReplay } from "rxjs";
import moment from "moment-timezone";
import { TitleDialog } from "../components/TitleDialog";
import { VideoService } from "../services/VideoService";
import { PutObjectCommand } from "@aws-sdk/client-s3";
import { getUserIdNumber, s3Client } from "../../utils";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AuthService } from "../services/AuthService";
import { MatDialog } from "@angular/material/dialog";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

@Component({
    templateUrl: './session-mode.html',
    imports: [
    MatAnchor,
    AsyncPipe,
    FormsModule,
    MatProgressSpinnerModule
]
})

export class SessionMode {
    folderRadioValue = "no_folder";
    folderService = inject(FolderService);
    questionService = inject(QuestionService);
    videoService = inject(VideoService);
    private _videoSnackBar = inject(MatSnackBar);
    readonly dialog = inject(MatDialog);
    folders$ = this.folderService.getAllFolders().pipe(shareReplay(1));
    totalQuestions$ = this.questionService.getAllQuestions().pipe(shareReplay(1));
    questionsInFolder = signal<QuestionType[]>([]);
    currentQuestion! : QuestionType;
    randomIndexes : number[] = [];
    selectedFolderId = 0;
    selectedNumberOfQuestions = 0;
    selectedTime = signal(60);
    selectedAnswer = "video";
    modeView = signal(false);
    finishedQuestion = signal(false);
    disableSave = signal(false);
    loading = signal(true);
    preparationTime = signal(10);
    timeState = signal("preparation");
    videoTitle = signal("");
    intervalId = 0;
    index = 0;
    answerInput = "";
    @ViewChild('recordedVideo') recordedVideoElement!: ElementRef<HTMLVideoElement>;
    @ViewChild('video') previewVideoElement!: ElementRef<HTMLVideoElement>;

    mediaRecorder: any;
    recordedBlobs: Blob[] = [];
    isRecording: boolean = false;
    downloadUrl!: string;
    stream!: MediaStream;
    timeCreated! : string;
    
    formattedTime = computed(() => {
        const minutes = Math.floor(this.selectedTime() / 60);
        const seconds = this.selectedTime() % 60;
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    });

    ngOnInit() {
        this.setInitialNumberQuestions();
    }

    setInitialNumberQuestions() {
        if (this.folderRadioValue === "no_folder") {    
            this.questionService.getQuestions().subscribe((res) => {
                this.selectedNumberOfQuestions = res.totalSize;
                this.loading.set(false);
            });
        }
    }

    onFolderRadioChange(event : Event) {
        if ((event.target as HTMLSelectElement).value === "no_folder") {
            this.totalQuestions$.subscribe((questions) => {
                this.selectedNumberOfQuestions = questions.length;
            });
        }
        else {
            this.selectedNumberOfQuestions = 0;
            this.questionsInFolder.set([]);
        }
    }

    computeRandomIndexes() {
        this.randomIndexes = [];
        
        for (let i = 0; i < this.selectedNumberOfQuestions; i++) {
            let index = Math.floor(Math.random() * this.selectedNumberOfQuestions);
            while (this.randomIndexes.includes(index)) {
                index = Math.floor(Math.random() * this.selectedNumberOfQuestions);
            }

            this.randomIndexes.push(index);
        }
    }

    onResponseTime() {
        this.intervalId = setInterval(() => {
            this.selectedTime.update((num) => num -= 1);

            if (this.selectedTime() === 0) {
                this.onFinishAnswering();
            }
        }, 1000);
    }

    onPreparationTime() {
        this.intervalId = setInterval(() => {
            this.preparationTime.update((num) => num -= 1);

            if (this.preparationTime() === 0) {
                clearInterval(this.intervalId);
                this.timeState.set("response");
                this.onResponseTime();

                if (this.selectedAnswer === "video") {
                    this.startRecording();
                }
            }
        }, 1000);
    }

    assignQuestion(id: number) {
        if (this.folderRadioValue === "no_folder") {
            this.totalQuestions$.subscribe((questions) => {
                this.currentQuestion = questions[id];
            });
        }
        else {
            this.currentQuestion = this.questionsInFolder()[id];
        }
    }

    onNumberQuestionChange(event : Event) {
        this.selectedNumberOfQuestions = parseInt((event.target as HTMLSelectElement).value);
    }

    startDisable() {
        if (this.selectedNumberOfQuestions === 0 || 
            this.folderRadioValue === "folder" && this.selectedFolderId === 0) {
            return true;
        }

        return false;
    }

    onStartMode() {
        this.modeView.set(true);
        this.onPreparationTime();
        this.computeRandomIndexes();
        if (this.randomIndexes.length > 0) {
            this.assignQuestion(this.randomIndexes[this.index++]);
        }
        if (this.selectedAnswer === "video") {
            this.retrieveStream();
        }
    }

    onFinishAnswering() {
        if (!this.finishedQuestion()) {
            clearInterval(this.intervalId);
            if (this.timeState() === "response") {
                if (this.selectedAnswer === "text" && this.answerInput) {
                    this.finishedQuestion.set(true);
                }
                else if (this.selectedAnswer === "video") {
                    this.stopRecording();
                    this.finishedQuestion.set(true);
                }
            }
        }
    }

    saveAnswer() {
        if (this.selectedAnswer === "text") {
            this.openSnackBar("Saving...");
            this.questionService.putQuestion({user_id: this.currentQuestion.user_id, 
                answer: this.currentQuestion.answer + "\n" + this.answerInput
            }, this.currentQuestion.question_id!).subscribe(() => {
                this.openSnackBar("Answer saved");
                this.disableSave.set(true);
            });
        }
        else {
            this.openDialog(getUserIdNumber(this.currentQuestion.user_id!), this.currentQuestion.question_id!);
        }
    }

    openDialog(userId: string, questionId: number): void {
        const dialogRef = this.dialog.open(TitleDialog, {
        data: {title: this.videoTitle()},
        });

        dialogRef.afterClosed().subscribe(result => {
        if (result !== undefined) {
            this.videoTitle.set(result);
            this.saveVideo(userId, questionId);
        }
        });
    }
    
    openSnackBar(message : string) {
        this._videoSnackBar.open(message, "Close", {
            duration: 5000
        });
    }
    
    async saveVideo(userId: string, questionId: number) {
        try {
            const title = this.videoTitle().replaceAll(' ', '_');

            this.videoService.postVideo({user_id: userId, created_at: this.timeCreated, title: title}, questionId)
            .subscribe(async () => {
                const videoBuffer = new Blob(this.recordedBlobs, {type: 'video/webm'});
                const videoFile = new File([videoBuffer], "test.mp4", {type: 'video/webm'});

                const command = new PutObjectCommand({
                    Key: `${userId}/${title}`,
                    Bucket: 'interviewdocs-videos',
                    Body: videoFile,
                    ContentType: videoFile.type
                });

                await s3Client.send(command);
        
                this.disableSave.set(true);

                    
                this.openSnackBar("Answer saved");
                
                this.videoTitle.set("");
            });
        }
        catch (err) {
            console.log(err);
        }
    }

    resetVariables() {
        this.preparationTime.set(10);
        this.selectedTime.set(60);
        this.timeState.set("preparation");
        this.finishedQuestion.set(false);
        this.disableSave.set(false);
    }

    nextQuestion() {
        this.assignQuestion(this.randomIndexes[this.index++]);
        this.resetVariables();
        this.onPreparationTime();
        this.disableSave.set(false);

        if (this.selectedAnswer === "video") {
            this.retrieveStream();
        }
        else {
            this.answerInput = "";
        }
    }

    onFinishMode() {
        this.resetVariables();
        this.setInitialNumberQuestions();
        this.modeView.set(false);
        this.index = 0;
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
            this.stream = stream;
            this.previewVideoElement.nativeElement.srcObject = this.stream;
            this.previewVideoElement.nativeElement.muted = true;
        });
    }

    startRecording() {
        this.recordedBlobs = [];
        let options: any = { mimeType: 'video/webm' };

        try {
        this.mediaRecorder = new MediaRecorder(this.stream, options);
        } catch (err) {
        console.log(err);
        }

        this.mediaRecorder.start();
        this.isRecording = !this.isRecording;

        this.timeCreated = moment().tz(moment.tz.guess(true)).format();

        this.onDataAvailableEvent();
        this.onStopRecordingEvent();
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
            this.recordedVideoElement.nativeElement.src = this.downloadUrl;
        };
        } catch (error) {
        console.log(error);
        }
    }

    stopRecording() {
        this.mediaRecorder.stop();
        this.isRecording = !this.isRecording;
    }

    playRecording() {
        if (!this.recordedBlobs || !this.recordedBlobs.length) {
        console.log('cannot play.');
        return;
        }
        this.recordedVideoElement.nativeElement.play();
    }

    onFolderChange(event : Event) {
        this.selectedFolderId = Number((event.target as HTMLSelectElement).value);
        // folder_id = 0 means that no folders have been selected
        if (Number(this.selectedFolderId) !== 0) {    
            this.folderService.getAllQuestionsInFolder(this.selectedFolderId)
            .subscribe((questions) => {
                this.questionsInFolder.set(questions);
                this.selectedNumberOfQuestions = this.questionsInFolder().length;
                
            });
        }
        else {
            this.selectedNumberOfQuestions = 0;
        }
    }
}