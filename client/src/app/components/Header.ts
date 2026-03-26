import { Component, model, } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";

@Component ({
    selector: 'app-header',
    templateUrl: 'header.html',
    imports: [
        MatButtonModule
    ]
})

export class Header {
    opened = model<boolean>(false);

    updateOpened(val : boolean):void {
        this.opened.update((currentValue) => !currentValue);
    }
}