import { Component, inject, signal } from "@angular/core";
import { MenuButton } from "../components/MenuButton";
import { AsyncPipe } from "@angular/common";
import { VideoService } from "../services/VideoService";
import { Observable } from "rxjs";
import { PagedVideoType } from "../types/VideoType";
import { ActivatedRoute, Router } from "@angular/router";
import { MatButton } from "@angular/material/button";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { AuthService } from "../services/AuthService";
import { orderDirection, sortFields } from "../../utils";
import { Table } from "../components/Table";

@Component({
    templateUrl: './video-list.html',
    imports: [
    MenuButton,
    AsyncPipe,
    MatButton,
    MatProgressSpinner,
    MatPaginator,
    Table
]
})

export class VideoList {
    sortValue = signal("Last viewed");
    orderValue = signal("Newest first");

    videoService = inject(VideoService);
    videos$ = this.videoService.getAllVideos();
    
    private router = inject(Router);
    private route = inject(ActivatedRoute);

    userId?: string;
    
    length = 0;
    pageSize = 0;
    pageIndex = 0;

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

    constructor(public auth : AuthService) {
        this.auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
    }

    handlePageEvent(e: PageEvent) {
        this.pageSize = e.pageSize;
        this.pageIndex = e.pageIndex;

        this.videos$ = this.videoService.getAllVideos(this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }

    
    updateVideos = () => {
        this.videos$ = this.videoService.getAllVideos(this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }
}

