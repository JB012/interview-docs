import { Component, model } from "@angular/core";
import { LoginButtonComponent } from "./login-button-component";
import { LogoutButtonComponent } from "./logout-button-component";
import { AsyncPipe } from "@angular/common";
import { AuthService } from "../services/AuthService";

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
    opened = model<boolean>(false);
    
    constructor(public auth: AuthService) {}
    
    updateOpened(val : boolean):void {
        this.opened.update((currentValue) => !currentValue);
    }
}