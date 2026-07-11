import { Component, inject, input } from "@angular/core";
import { FolderType } from "../types/FolderType";
import { VideoType } from "../types/VideoType";
import { QuestionType } from "../types/QuestionType";
import { TableData } from "./TableData";
import { AuthService } from "../services/AuthService";
import moment from "moment-timezone";
import { ActivatedRoute, Router } from "@angular/router";
import { FolderService } from "../services/FolderService";
import { VideoService } from "../services/VideoService";
import { QuestionService } from "../services/QuestionService";
import { getUserIdNumber } from "../../utils";

@Component({
    selector: '[questions], [folders], [videos]',
    templateUrl: './table.html',
    imports: [
        TableData
    ]

})

export class Table {
    questions = input<QuestionType[]>();
    folders = input<FolderType[]>();
    videos = input<VideoType[]>();

    sortValue = input.required<string>();
    orderValue = input.required<string>();

    updateQuestions = input<() => void>();
    updateFolders = input<() => void>();
    updateVideos = input<() => void>();
    
    folderId = input<number>();
    
    userId? : string;

    constructor(public auth : AuthService) {
        this.auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!;
        });
    }
}