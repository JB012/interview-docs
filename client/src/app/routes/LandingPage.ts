import { Component } from "@angular/core";
import { LoginButtonComponent } from "../components/login-button-component";

@Component({
    selector: 'landing-page',
    templateUrl: "./landing-page.html",
    imports: [
        LoginButtonComponent
    ]
})

export class LandingPage {}