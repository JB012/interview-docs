import { Component, inject, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { ActivatedRoute } from "@angular/router";
import { MenuButton } from "../components/MenuButton";
import { VideoFile } from "../components/VideoFile";
import { RecordedVideo } from "../components/RecordedVideo";
import { VideoControls } from "../components/VideoControls";

@Component({
    selector: "question-page",
    imports: [
    MatButtonModule,
    MenuButton,
    VideoFile,
    RecordedVideo
],
    templateUrl: "./question-page.html"
})

export class QuestionPage {
    private activatedRoute = inject(ActivatedRoute);
    id = signal('');
    option = signal('video');
    clickedNewVideo = signal(true);
    
    updateClickedNewVideo(newValue : boolean) {
        this.clickedNewVideo.update((value) => newValue);
    } 

    updateOption(newValue : string) {
        this.option.set(newValue);
    }

    constructor() {
        this.activatedRoute.params.subscribe((params) => {
        this.id.set(params['id']);
        });
    }
}