import { Component, inject, Signal, signal } from "@angular/core";
import { MenuButton } from "../components/MenuButton";
import { AsyncPipe } from "@angular/common";
import { VideoService } from "../services/VideoService";
import { Subject } from "rxjs";
import { PagedVideoType } from "../types/VideoType";
import { ActivatedRoute, Router, ROUTER_OUTLET_DATA } from "@angular/router";
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
    videoSource = new Subject<PagedVideoType>();
    videos$ = this.videoSource.asObservable();
    
    private router = inject(Router);
    private route = inject(ActivatedRoute);
    
    userId?: string;
    outletData = inject(ROUTER_OUTLET_DATA) as Signal<{questionId: number}>;

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
    
    ngOnInit(): void {
        this.videoService.getAllVideos(this.outletData().questionId).subscribe((videos) => {
            this.length = videos.totalSize;
            this.pageSize = videos.pageSize;
            this.pageIndex = videos.pageNumber;

            this.videoSource.next(videos);
        });
    }
    constructor(public auth : AuthService) {
        this.auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user;
        });
    }

    handlePageEvent(e: PageEvent) {
        this.pageSize = e.pageSize;
        this.pageIndex = e.pageIndex;

        this.updateVideos();
    }

    
    updateVideos = () => {
        const field = this.sortValue() === "Alphabetical" ? "title" : sortFields[this.sortValue()];
        this.videoService.getAllVideos(this.outletData().questionId, this.pageIndex, this.pageSize, 
            {field: field, direction: orderDirection[this.orderValue()]})
            .subscribe((videos) => {
                this.videoSource.next(videos);
                    
                this.length = videos.totalSize;
                this.pageSize = videos.pageSize
                this.pageIndex = videos.pageNumber;
            })
    }
}

