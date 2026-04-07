import { Component, input, signal } from "@angular/core";
import { ClickOutside } from "../click-outside";

@Component({
    selector: 'custom-video',
    templateUrl: './video.html',
    imports: [
        ClickOutside
    ]
})

export class Video {
    title = input.required<string>();
    option = signal('text');
    questionMenu = signal(false);

    updateQuestionMenu() {
        this.questionMenu.update((value) => !value);
    }

    onClickOutside() {
        if (this.questionMenu()) {
            this.questionMenu.set(false);
        }
    }
}