import { Component, signal } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MenuButton } from "../components/MenuButton";
import { FolderSearch } from "../components/FolderSearch";
import {AsyncPipe} from '@angular/common';

@Component ({
    templateUrl: './home.html',
    imports: [
        MatPaginatorModule,
        MatButtonModule,
        MenuButton,
        FolderSearch,
    ]
})

export class Home {}