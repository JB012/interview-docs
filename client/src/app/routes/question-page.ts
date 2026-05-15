import { Component, inject, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { ActivatedRoute } from "@angular/router";
import { MenuButton } from "../components/MenuButton";
import { VideoFile } from "../components/VideoFile";
import { RecordedVideo } from "../components/RecordedVideo";
import { VideoControls } from "../components/VideoControls";
import { form, FormField, debounce } from '@angular/forms/signals'
import { QuestionService } from "../services/QuestionService";
@Component({
    selector: "question-page",
    imports: [
    MatButtonModule,
    MenuButton,
    VideoFile,
    RecordedVideo,
    FormField
],
    templateUrl: "./question-page.html"
})

export class QuestionPage {
    private activatedRoute = inject(ActivatedRoute);
    
    id = signal('');
    option = signal('video');
    clickedNewVideo = signal(true);
    questionInput = signal('');
    
    timeoutID!: number;

    questionModel = signal({
        question: '',
        answer: ''
    });

    questionService = inject(QuestionService);
    
    questionForm = form(this.questionModel, (schemaPath) => {
        debounce(schemaPath.question, () => {
            return new Promise<void>((resolve) => {
                clearTimeout(this.timeoutID);
                this.timeoutID = setTimeout(() => {
                    this.questionService.putQuestion({question: this.questionModel().question}, parseInt(this.id()))
                    .subscribe(() => resolve());                
                }, 2000);
            });
        });
    });

    constructor() {
        this.activatedRoute.params.subscribe((params) => {
        this.id.set(params['id']);
        });
    }
    
    updateClickedNewVideo(newValue : boolean) {
        this.clickedNewVideo.update((value) => newValue);
    } 

    updateOption(newValue : string) {
        this.option.set(newValue);
    }
}