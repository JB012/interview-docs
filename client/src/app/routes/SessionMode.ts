import { Component, computed, ElementRef, inject, signal, ViewChild } from "@angular/core";
import { MatAnchor } from "@angular/material/button";
import { FolderService } from "../services/FolderService";
import { AsyncPipe, NgClass } from "@angular/common";
import { FolderType } from "../types/FolderType";
import { QuestionService } from "../services/QuestionService";
import { QuestionType } from "../types/QuestionType";
import { FormsModule } from "@angular/forms";
import { shareReplay } from "rxjs";
import moment from "moment-timezone";

@Component({
    templateUrl: './session-mode.html',
    imports: [
    MatAnchor,
    AsyncPipe,
    FormsModule,
]
})

export class SessionMode {
    folderRadioValue = "no_folder";
    folderService = inject(FolderService);
    questionService = inject(QuestionService);
    folders$ = this.folderService.getAllFolders().pipe(shareReplay(1));
    totalQuestions$ = this.questionService.getAllQuestions().pipe(shareReplay(1));
    questionsInFolder : QuestionType[] | undefined;
    randomIndexes : number[] = [];
    selectedFolderId = 0;
    selectedNumberOfQuestions = 1;
    selectedTime = signal(60);
    selectedAnswer = "video";
    modeView = signal(false);
    finishedQuestion = signal(false);
    preparationTime = signal(5);
    timeState = signal("preparation");
    intervalId = 0;
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

    computeRandomIndexes() {
        for (let i = 0; i < this.selectedNumberOfQuestions; i++) {
            let index = Math.floor(Math.random() * this.selectedNumberOfQuestions);
            while (this.randomIndexes.includes(index)) {
                index = Math.floor(Math.random() * this.selectedNumberOfQuestions);
            }

            this.randomIndexes.includes(index);
        }
    }

    onCountDown() {
        const timeoutId = setTimeout(() => {
            // if video start recording
            // text allow user to edit
        }, 1000 * this.selectedTime());
    }

    onResponseTime() {
        this.intervalId = setInterval(() => {
            this.selectedTime.update((num) => num -= 1);

            if (this.selectedTime() === 0) {
                clearInterval(this.intervalId);
                this.onFinishAnswering();

                if (this.selectedAnswer === "video") {
                    this.stopRecording();
                }
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

    onStartMode() {
        this.modeView.set(true);
        this.onPreparationTime();
        if (this.selectedAnswer === "video") {
            this.retrieveStream();
        }
    }

    onFinishAnswering() {
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

    saveAnswer() {
        // save as text: append text to question answer
        // save as video: add video to question video list
    }
    nextQuestion() {
        // mark question as selected. next index for question. reset variables
    }

    onFinishMode() {
        this.modeView.set(false);
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
        
        console.log(this.timeCreated);

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

    onFolderChange() {
        this.folderService.getAllQuestionsInFolder(this.selectedFolderId).subscribe((questions) => {
            this.questionsInFolder = questions.map((question) => {
                question.checked = false;
                return question;
            });
        })
    }
}