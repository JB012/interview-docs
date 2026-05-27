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

    private router = inject(Router);
    private route = inject(ActivatedRoute);

    private folderService = inject(FolderService);
    private questionService = inject(QuestionService);
    private videoService = inject(VideoService);

    sortValue = input.required<string>();
    orderValue = input.required<string>();
    
    userId? : string;

    constructor(public auth : AuthService) {
        this.auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
    }

    navigateToFolderPage(id: number) {
        this.folderService.putFolder({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
            user_id: this.userId }, id).subscribe(() => 
            {
                this.router.navigate(['questions', id]);
            }
        );
    }

    navigateToQuestionPage(id: number) {
        this.questionService.putQuestion({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
            user_id: this.userId }, id).subscribe(() => 
            {
                this.router.navigate(['questions', id]);
            }
        );
    }
    
    navigateToVideoPage(id: number) {
        this.videoService.putVideo({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
        user_id: this.userId }, id).subscribe(() => 
            {
                this.router.navigate([id], {relativeTo: this.route}); 
            }
        );   
    }
}