import { Component, ElementRef, signal, ViewChild } from "@angular/core";
import { LoginButtonComponent } from "../components/login-button-component";

@Component({
    selector: 'landing-page',
    templateUrl: "./landing-page.html",
    imports: [
        LoginButtonComponent
    ]
})

export class LandingPage {
    imageIndex = signal(1);
    @ViewChild('practiceImage')
    practiceImageRef!: ElementRef<HTMLImageElement>;

    onClick(num : number) {
        if (num <= 2 && num > 0) {
            if (num === 1) {
                this.practiceImageRef.nativeElement.classList.add("animate-left-slide");
            }
            else {
                this.practiceImageRef.nativeElement.classList.add("animate-right-slide");
            }

            setTimeout(() => {
                if (num === 1) {
                    this.practiceImageRef.nativeElement.classList.remove("animate-left-slide");
                }
                else {
                    this.practiceImageRef.nativeElement.classList.remove("animate-right-slide");
                }
                this.imageIndex.set(num);
            } ,300);
        }
    }
}