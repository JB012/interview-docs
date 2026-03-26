import { Component } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule } from "@angular/material/paginator";

@Component ({
    templateUrl: './home.html',
    imports: [
        MatPaginatorModule,
        MatButtonModule,
    ]
})

export class Home {}