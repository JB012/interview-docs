import { Component, inject, Signal } from "@angular/core";
import { ROUTER_OUTLET_DATA, RouterOutlet } from "@angular/router";

@Component({
    template: `<router-outlet [routerOutletData]="outletData()"></router-outlet>`,
    imports: [RouterOutlet]
})

export class VideoPage {
    outletData = inject(ROUTER_OUTLET_DATA) as Signal<{questionId: number}>;
}