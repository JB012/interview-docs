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
    id = input.required<number>();
    created_at = input.required<string>();
    option = signal('text');
    questionMenu = signal(false);

    updateQuestionMenu() {
        this.questionMenu.update((value) => !value);
    }

    getTime() {
        const videoCreatedDate = new Date(this.created_at());
        
        return videoCreatedDate.toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit'
        });
    }
    onClickOutside() {
        if (this.questionMenu()) {
            this.questionMenu.set(false);
        }
    }
}