import { Component, inject, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { ActivatedRoute } from "@angular/router";
import { MenuButton } from "../components/MenuButton";
import { Video } from "../components/Video";

@Component({
    selector: "question-page",
    imports: [
        MatButtonModule,
        MenuButton,
        Video
    ],
    templateUrl: "./question-page.html"
})

export class QuestionPage {
    id = signal('');
    option = signal('text');
    private activatedRoute = inject(ActivatedRoute);

    updateOption(newValue : string) {
        this.option.set(newValue);
    }

    constructor() {
        this.activatedRoute.params.subscribe((params) => {
        this.id.set(params['id']);
        });
    }
}