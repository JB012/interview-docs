import { Component } from "@angular/core";
import { MatPaginatorModule } from "@angular/material/paginator";

@Component ({
    template: './home.html',
    imports: [
        MatPaginatorModule
    ]
})

export class Home {}