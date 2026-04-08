import { Component, input, signal } from "@angular/core";
import { ClickOutside } from "../click-outside";

@Component({
    selector: 'video-file',
    templateUrl: './video-file.html',
    imports: [
        ClickOutside
    ]
})

export class VideoFile {
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