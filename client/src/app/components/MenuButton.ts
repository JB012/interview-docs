import { Component, input, model, signal } from "@angular/core";
import { ClickOutside } from "../click-outside";
export interface StringArray {
    [index: string] : string
}

@Component ({
    selector: "menu-button",
    templateUrl: 'menu-button.html',
    imports: [ClickOutside]
})

export class MenuButton {
    type = input.required<string>();
    sortBy = model.required<string>();
    orderBy = model.required<string>();
    menuOpened = signal(false);
    updateQuestions = input<() => void>();
    updateFolders = input<() => void>();
    updateVideos = input<() => void>();

    updateSortBy(sortValue : string) {
        if (this.sortBy() === "Alphabetical" && sortValue === "Date created") {
            this.updateOrderBy('Newest first');
        }
        else if ((this.sortBy() === "Date created" || this.sortBy() === "Last viewed") && sortValue === "Alphabetical") {
            this.updateOrderBy("A-Z");
        }

        if (sortValue !== this.sortBy()) {
            this.sortBy.set(sortValue);

            if (this.type() === "questions") {
                this.updateQuestions()!();
            }
            else if (this.type() === "folders") {
                this.updateFolders()!();
            }
            else {
                this.updateVideos()!();
            }
        }
        
        this.updateMenuView();
    }

    updateOrderBy(orderValue : string) {
        if (orderValue !== this.orderBy()) {
            this.orderBy.set(orderValue);

            if (this.type() === "questions") {
                this.updateQuestions()?.();
            }
        }
    }

    updateMenuView() {
        this.menuOpened.update((value) => !value);
    }

    onClickOutside() {
        if (this.menuOpened()) {
            this.menuOpened.set(false);
        }
    }
    
}