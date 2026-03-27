import { Component, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MenuButton } from "../components/MenuButton";

@Component ({
    templateUrl: './home.html',
    imports: [
        MatPaginatorModule,
        MatButtonModule,
        MenuButton,
    ]
})

export class Home {
    title="Alphabetical"
}