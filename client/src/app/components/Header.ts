import { Component, inject, model, } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { LoginButtonComponent } from "./login-button-component";
import { LogoutButtonComponent } from "./logout-button-component";
import { AuthService } from '@auth0/auth0-angular';
import { AsyncPipe } from "@angular/common";

@Component ({
    selector: 'app-header',
    templateUrl: 'header.html',
    imports: [
        LoginButtonComponent, 
        LogoutButtonComponent, 
        AsyncPipe
    ]
})

export class Header {
    protected auth = inject(AuthService);
    opened = model<boolean>(false);

    updateOpened(val : boolean):void {
        this.opened.update((currentValue) => !currentValue);
    }
}