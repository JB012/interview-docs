import { Component, inject } from "@angular/core";
import { MenuButton } from "../components/MenuButton";
import { AsyncPipe } from "@angular/common";
import { VideoService } from "../services/VideoService";
import { Observable } from "rxjs";
import { VideoType } from "../types/VideoType";
import { VideoFile } from "../components/VideoFile";
import { ActivatedRoute, Router, RouterOutlet } from "@angular/router";
import { MatButton } from "@angular/material/button";

@Component({
    templateUrl: './video-list.html',
    imports: [
    MenuButton,
    AsyncPipe,
    VideoFile,
    MatButton
    ]
})

export class VideoList {
    sortValue = "Last viewed";
    orderValue = "Newest first";

    videos$! : Observable<VideoType[]>;
    videoService = inject(VideoService);
    private router = inject(Router);
    private route = inject(ActivatedRoute);

    navigateToExistingVideo(id: number) {
        this.router.navigate([id], {relativeTo: this.route});    
    }

    createNewVideo() {
        const segments = this.route.snapshot.url;
        const urlPath = segments.map(segment => segment.path).join('/');

        if (!urlPath.includes("/videos/new")) {
            this.router.navigate(['new'], {relativeTo: this.route});
        }
    }

    constructor() {
        this.videos$ = this.videoService.getAllVideos();
    }

}