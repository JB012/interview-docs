import { Component, input, signal } from "@angular/core";
import { ClickOutside } from "../click-outside";

@Component ({
    selector: "menu-button",
    templateUrl: 'menu-button.html',
    imports: [ClickOutside]
})

export class MenuButton {
    sortBy = signal('Alphabetical');
    orderBy = signal('Z-A');
    menuOpened = signal(false);

    updateSortBy(sortValue : string) {
        if (this.sortBy() === "Alphabetical" && sortValue === "Date created") {
            this.updateOrderBy('Newest first');
        }
        else if (this.sortBy() === "Date created" && sortValue === "Alphabetical") {
            this.updateOrderBy("Z-A");
        }
        
        this.sortBy.update(() => sortValue);
        
        this.updateMenuView();
    }

    updateOrderBy(orderValue : string) {
        this.orderBy.set(orderValue);
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