import { Component, inject, signal } from "@angular/core";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MenuButton } from "../components/MenuButton";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { shareReplay } from "rxjs";
import { AuthService } from "../services/AuthService";
import { FolderService } from "../services/FolderService";
import { orderDirection, sortFields } from "../../utils";
import { AsyncPipe } from "@angular/common";
import moment from "moment-timezone";
import { Router } from "@angular/router";
import { Table } from "../components/Table";
import { FolderDialog } from "../components/FolderDialog";
import { MatDialog } from "@angular/material/dialog";

@Component({
    templateUrl: './folder-list.html',
    imports: [
    MatProgressSpinner,
    MenuButton,
    MatPaginator,
    AsyncPipe,
    Table
],

})

export class FolderList {
    userId! : string;

    private router = inject(Router);
    private folderService = inject(FolderService);
    readonly dialog = inject(MatDialog);

    folders$ = this.folderService.getFolders().pipe(shareReplay(1)); 
    
    sortValue = signal("Last viewed");
    orderValue = signal("Newest first");
    folderTitle = signal('');

    length = 0;
    pageSize = 0;
    pageIndex = 0;

    constructor(public auth : AuthService) {
        this.auth.getCurrentUser().subscribe((res) => {
            this.userId = res!.user!.claims.sub;
        });
    }

    handlePageEvent(e: PageEvent) {
        this.pageSize = e.pageSize;
        this.pageIndex = e.pageIndex;

        this.folders$ = this.folderService.getFolders(this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }

    addFolder() {
       this.folderService.postFolder({user_id: this.userId,
        viewed_at: moment().tz(moment.tz.guess(true)).format()
       })
        .subscribe((folder) => {
            this.router.navigate(['folders', folder.id]);
        });
    }

    updateFolders = () => {
        this.folders$ = this.folderService.getFolders(this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }

    openDialog(): void {
        const dialogRef = this.dialog.open(FolderDialog, {
            data: {title: this.folderTitle()},
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result !== undefined) {
                this.folderTitle.set(result);
                this.saveFolder();
            }
        });
    }

    saveFolder() {
        const title = this.folderTitle();
        this.folderService.postFolder({name: title, user_id: this.userId}).subscribe((folder) => {
            this.router.navigate(['folders', folder.id]);
        });
    }
}