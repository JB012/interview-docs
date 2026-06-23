import { Component, computed, inject, input, signal, SimpleChanges } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { QuestionService } from "../services/QuestionService";
import { AuthService } from "../services/AuthService";
import { MatMenuModule, MatMenu } from '@angular/material/menu';
import moment from "moment-timezone";
import { QuestionType } from "../types/QuestionType";
import { FolderType } from "../types/FolderType";
import { FolderService } from "../services/FolderService";
import { VideoType } from "../types/VideoType";
import { VideoService } from "../services/VideoService";
import { getUserIdNumber } from "../../utils";
import { MatDialog } from "@angular/material/dialog";
import { FolderDialog } from "./FolderDialog";
import { DeleteDialog } from "./DeleteDialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { TitleDialog } from "./TitleDialog";

@Component({
    selector: '[question], [folder], [video]',
    template:  `
        <td (click)="onClick()" class="p-4 xxs:w-25 xs:w-50 sm:w-100 md:w-125 lg:w-150 xl:w-175 2xl:w-200"> 
            <div class="xxs:w-25 xs:w-50 sm:w-100 md:w-125 lg:w-150 xl:w-175 2xl:w-200 truncate">
                {{ title() }}
            </div>
        </td>
        <td (click)="onClick()">
            {{ editedAt() }}
        </td>
        <td (click)="onClick()">
            {{sortValue() === "Date created" ? createdAt() : viewedAt() }}
        </td>
        <td>
            <div class="px-2">
                <span [matMenuTriggerFor]="menu" class="material-symbols-outlined xxs xs:material-symbols-outlined xs lg:material-symbols-outlined lg">&#xe5d4;</span>
                <mat-menu #menu="matMenu">
                <button (click)="onNewPageClick()" mat-menu-item>Open in a new tab</button>
                @if (video() || folder()) {
                    <button (click)="openEditDialog()" mat-menu-item>Edit</button>
                }
                <button (click)="openDialog(type(), id())"  mat-menu-item>Delete</button>
                </mat-menu>
            </div>
        </td>
        `,
    imports: [MatMenu, MatMenuModule]
})

export class TableData {
    question = input<QuestionType>();
    folder = input<FolderType>();
    video = input<VideoType>();
    sortValue = input("");
    folderId = input<number>();

    type = computed(() => {
        if (this.question()) return "question";
        else if (this.folder()) return "folder";
        return "video";
    });

    id = computed(() => {
        if (this.question()) return this.question()?.question_id!
        else if (this.folder()) return this.folder()?.folder_id!
        return this.video()?.video_id!;
    });

    title = computed(() => {
        return [this.question()?.question, this.folder()?.title, this.video()?.title].find(v => v != undefined);
    });

    editedAt = computed(() => {
        const editedAt = [this.question()?.edited_at, this.folder()?.edited_at, this.video()?.edited_at].find(v => v != undefined);
        return editedAt ? new Date(editedAt).toLocaleDateString() : '';
    });

    viewedAt = computed(() => {
        const viewedAt = [this.question()?.viewed_at, this.folder()?.viewed_at, this.video()?.viewed_at].find(v => v != undefined);
        return viewedAt ? new Date(viewedAt).toLocaleDateString() : '';
    });

    createdAt = computed(() => {
        const createdAt = [this.question()?.created_at, this.folder()?.created_at, this.video()?.created_at].find(v => v != undefined);
        return createdAt ? new Date(createdAt).toLocaleDateString() : '';
    });

    router = inject(Router);
    route = inject(ActivatedRoute);

    questionService = inject(QuestionService);
    folderService = inject(FolderService);
    videoService = inject(VideoService);

    userId : string | undefined;
    optionMenu = signal(false);
    timeZone = moment.tz.guess(true);

    private _snackBar = inject(MatSnackBar);
    readonly dialog = inject(MatDialog);
    
    updateQuestions = input<() => void>();
    updateFolders = input<() => void>();
    updateVideos = input<() => void>();

    constructor(auth: AuthService) {
        auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
    }

    updateOptions() {
        this.optionMenu.update(val => !val);        
    }
    
    clickOutside() {
        if (this.optionMenu()) {
            this.optionMenu.set(false);
        }
    }

    navigateToFolderPage(id: number, newPage = false) {
        this.folderService.putFolder({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
            user_id: this.userId }, id).subscribe(() => 
            {
                if (!newPage) {   
                    this.router.navigate(['folders', id]);
                }
                else {
                    const urlTree = this.router.createUrlTree(['/folders', this.folder()!.folder_id]);
                    const serializedUrl = this.router.serializeUrl(urlTree);
                    window.open(serializedUrl, "_blank");
                }
            }
        );
    }

    navigateToQuestionPage(id: number, newPage = false) {
        this.questionService.putQuestion({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
            user_id: this.userId }, id).subscribe(() => 
            {
                if (!newPage) {
                    this.router.navigate(['questions', id]);
                }
                else {
                    const urlTree = this.router.createUrlTree(['/questions', this.question()!.question_id]);
                    const serializedUrl = this.router.serializeUrl(urlTree);
                    window.open(serializedUrl, "_blank");
                }
            }
        );
    }
    
    navigateToVideoPage(id: number, newPage = false) {
        this.videoService.putVideo({viewed_at: moment().tz(moment.tz.guess(true)).format(), 
        user_id: getUserIdNumber(this.userId!)}, id).subscribe(() => 
            {
                if (!newPage) {  
                    this.router.navigate([id], {relativeTo: this.route}); 
                }
                else {
                    const urlTree = this.router.createUrlTree([id], {relativeTo: this.route});
                    const serializedUrl = this.router.serializeUrl(urlTree);
                    window.open(serializedUrl, "_blank");
                }
            }
        );   
    }

    openQuestionToNewPage() {
        if (this.question()) {
            const urlTree = this.router.createUrlTree(['/questions', this.question()!.question_id]);

            const serializedUrl = this.router.serializeUrl(urlTree);
            
            window.open(serializedUrl, "_blank")
        }
    }
    
    onClick() {
        if (this.question()) {
            this.navigateToQuestionPage(this.question()!.question_id!);
        }
        else if (this.folder()) {
            this.navigateToFolderPage(this.folder()!.folder_id!);
        }
        else if (this.video()) {
            this.navigateToVideoPage(this.video()!.video_id!);
        }
    }

    openSnackBar(message: string) {
        this._snackBar.open(message, "Close", {
            duration: 5000
        });
    }

    openDialog(type: string, id: number): void {
        const dialogRef = this.dialog.open(DeleteDialog);

        dialogRef.afterClosed().subscribe((res) => {
            if (res) {
                if (type === "video") {
                    this.videoService.deleteVideo(id).subscribe(() => {
                        this.updateVideos()?.();
                    });
                }
                else if (type === "folder") {
                    this.folderService.deleteFolder(id).subscribe(() => {
                        this.updateFolders()?.();
                    });
                }
                else {
                    if (this.folderId()) {
                        this.folderService.deleteQuestionInFolder(this.folderId()!, id).subscribe(() => {
                            this.updateQuestions()?.();
                        });
                    }
                    else {   
                        this.questionService.deleteQuestion(id).subscribe(() => {
                            this.updateQuestions()?.();
                        });
                    }
                }

                this.openSnackBar("Item deleted");
            }
        });
    }
    
    openEditDialog(): void {
        const dialogRef = this.dialog.open(TitleDialog, {
        data: {title: this.title()},
        });

        dialogRef.afterClosed().subscribe(result => {
        if (result !== undefined) {
            this.editTitle(result);
        }
        });
    }

    editTitle(newTitle : string) {
        this.openSnackBar("Editing...")
        if (this.video()) {
            this.videoService.putVideo({user_id: getUserIdNumber(this.userId!), title: newTitle}, this.video()?.video_id!).subscribe(() => {
                this.updateVideos()?.();
                this.openSnackBar("Video edited");
            });
        }
        else {
            this.folderService.putFolder({user_id: this.userId, title: newTitle}, this.folder()?.folder_id!).subscribe(() => {
                    this.updateFolders()?.();
                    this.openSnackBar("Folder edited");
                });
        }
    }

    onNewPageClick() {
        if (this.question()) {
            this.navigateToQuestionPage(this.question()!.question_id!, true);
        }
        else if (this.folder()) {
            this.navigateToFolderPage(this.folder()!.folder_id!, true);
        }
        else if (this.video()) {
            this.navigateToVideoPage(this.video()!.video_id!, true);
        }
    }
}