import { Component } from "@angular/core";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MenuButton } from "../components/MenuButton";
import { MatPaginator, PageEvent } from "@angular/material/paginator";

@Component({
    templateUrl: 'folder-page.html',
    imports: [MatProgressSpinner, MenuButton, MatPaginator],

})

export class FolderPage {
handlePageEvent($event: PageEvent) {
throw new Error('Method not implemented.');
}

}