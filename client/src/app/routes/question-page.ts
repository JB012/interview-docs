import { Component, inject, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { ActivatedRoute, Router, RouterOutlet } from "@angular/router";
import { MenuButton } from "../components/MenuButton";
import { VideoFile } from "../components/VideoFile";
import { RecordedVideo } from "./RecordedVideo";
import { QuestionService } from "../services/QuestionService";
import { QuestionType } from "../types/QuestionType";
import { Observable } from "rxjs/internal/Observable";
import { AsyncPipe } from "@angular/common";
import { AuthService } from "../services/AuthService";
import { getUserIdNumber } from "../../utils";
import { VideoService } from "../services/VideoService";
import { VideoType } from "../types/VideoType";
import moment from "moment-timezone";
@Component({
    selector: "question-page",
    imports: [
    MatButtonModule,
    AsyncPipe,
    RouterOutlet
],
    templateUrl: "./question-page.html"
})

export class QuestionPage {
    private activatedRoute = inject(ActivatedRoute);
    
    id = signal(0);
    option = signal('text');
    clickedNewVideo = signal(true);
    questionInput = signal('');
    answerInput = signal('');
    saveState = signal('');
    question$!: Observable<QuestionType>;
    videos$! : Observable<VideoType[]>;
    timeoutID!: number;

    router = inject(Router);
    route = inject(ActivatedRoute);
    questionService = inject(QuestionService);
    videoService = inject(VideoService);
    user_id!: string;
    

    navigateToVideos() {
        const segments = this.route.snapshot.url;
        const urlPath = segments.map(segment => segment.path).join('/');

        if (!urlPath.includes("/videos")) {
            this.updateOption('video');
            this.router.navigate(['videos'], {relativeTo: this.route});
        }
    }

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

        const editedAt = moment().tz(moment.tz.guess(true)).format();

        this.timeoutID = setTimeout(() => {
            if (targetId === "questionInput") {
                const text = (event.target as HTMLSpanElement).textContent;
                this.questionService.putQuestion({question: text, edited_at: editedAt, user_id: getUserIdNumber(this.user_id)}, this.id())
                .subscribe();
            } 
            else if (targetId === "answerInput") {
                const text = (event.target as HTMLTextAreaElement).value;
                this.questionService.putQuestion({answer: text, edited_at: editedAt, user_id: getUserIdNumber(this.user_id)}, this.id()).subscribe();
            }
            
            this.saveState.set("saved");

            setTimeout(() => {
                this.saveState.set("");
            }, 3000);
        }, 2000);
    }
    constructor(public auth : AuthService ) {
        this.activatedRoute.params.subscribe((params) => {
            this.id.set(parseInt(params['questionId']));
            
            this.question$ = this.questionService.getQuestion(this.id());

            this.question$.subscribe((res) => {
                this.questionInput.set(res.question!);
            });

            this.videos$ = this.videoService.getAllVideos();

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