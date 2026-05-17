import { Component, inject, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { ActivatedRoute } from "@angular/router";
import { MenuButton } from "../components/MenuButton";
import { VideoFile } from "../components/VideoFile";
import { RecordedVideo } from "../components/RecordedVideo";
import { QuestionService } from "../services/QuestionService";
import { QuestionType } from "../types/QuestionType";
import { Observable } from "rxjs/internal/Observable";
import { AsyncPipe } from "@angular/common";
import { AuthService } from "../services/AuthService";
import { getUserIdNumber } from "../../utils";
@Component({
    selector: "question-page",
    imports: [
    MatButtonModule,
    MenuButton,
    VideoFile,
    RecordedVideo,
    AsyncPipe
],
    templateUrl: "./question-page.html"
})

export class QuestionPage {
    private activatedRoute = inject(ActivatedRoute);
    
    id = signal(0);
    option = signal('video');
    clickedNewVideo = signal(true);
    questionInput = signal('');
    answerInput = signal('');
    saveState = signal('');
    question$!: Observable<QuestionType>;
    timeoutID!: number;

    questionService = inject(QuestionService);
    user_id!: string;

    updateQuestion(event : Event) {
        const targetId = (event.target as HTMLElement).id;

        if (targetId === "questionInput") {
            const text = (event.target as HTMLSpanElement).textContent;
            this.questionInput.set(text);
        }
        else if (targetId === "answerInput") {
            const text = (event.target as HTMLTextAreaElement).value;
            this.answerInput.set(text);
        }
        
        clearTimeout(this.timeoutID);

        this.timeoutID = setTimeout(() => {
            if (targetId === "questionInput") {
                const text = (event.target as HTMLSpanElement).textContent;
                this.questionService.putQuestion({question: text, user_id: getUserIdNumber(this.user_id)}, this.id())
                .subscribe();
            } 
            else if (targetId === "answerInput") {
                const text = (event.target as HTMLTextAreaElement).value;
                this.questionService.putAnswer(text, this.id()).subscribe();
            }
            
            this.saveState.set("saved");

            setTimeout(() => {
                this.saveState.set("");
            }, 3000);
        }, 2000);
    }
    constructor(public auth : AuthService ) {
        this.activatedRoute.params.subscribe((params) => {
            this.id.set(parseInt(params['id']));
            
            this.question$ = this.questionService.getQuestion(this.id());

            this.question$.subscribe((res) => {
                this.questionInput.set(res.question);
            });

            auth.getCurrentUser().subscribe((res) => {
                this.user_id = res!.user!.claims.sub;
            })
        });
    }
    
    updateClickedNewVideo(newValue : boolean) {
        this.clickedNewVideo.update((value) => newValue);
    } 

    updateOption(newValue : string) {
        this.option.set(newValue);
    }
}