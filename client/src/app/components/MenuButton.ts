import { Component, input, signal } from "@angular/core";
import { ClickOutside } from "../click-outside";

@Component ({
    selector: "menu-button",
    templateUrl: 'menu-button.html',
    imports: [ClickOutside]
})

export class MenuButton {
    title = input('');
    menuOpened = signal(false);

    updateMenuView() {
        this.menuOpened.update((value) => !value);
    }

    onClickOutside() {
        if (this.menuOpened()) {
            this.menuOpened.set(false);
        }
    }
    
}