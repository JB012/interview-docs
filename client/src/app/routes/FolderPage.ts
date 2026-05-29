import { Component, inject, signal } from "@angular/core";
import { AuthService } from "../services/AuthService";
import { Observable } from "rxjs";
import { PagedQuestionType } from "../types/QuestionType";
import { ActivatedRoute } from "@angular/router";
import { FolderService } from "../services/FolderService";
import { orderDirection, sortFields } from "../../utils";
import { MenuButton } from "../components/MenuButton";
import { AsyncPipe } from "@angular/common";
import { FolderType } from "../types/FolderType";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { Table } from "../components/Table";

@Component({
    templateUrl: './folder-page.html',
    imports: [
    MenuButton,
    AsyncPipe,
    MatProgressSpinner,
    MatPaginator,
    Table
]
})

export class FolderPage {
    id = signal(0);
    sortValue = signal("Last viewed");
    orderValue = signal("Newest first");
    private activatedRoute = inject(ActivatedRoute);
    private folderService = inject(FolderService);

    questions$: Observable<PagedQuestionType> | undefined; 
    folder$: Observable<FolderType> | undefined;
    length = 0;
    pageSize = 0;
    pageIndex = 0;

    constructor(public auth : AuthService ) {
        this.activatedRoute.params.subscribe((params) => {
            this.id.set(parseInt(params['id']));
            
            this.questions$ = this.folderService.getQuestionsFromFolder(parseInt(params['id']));
            this.folder$ = this.folderService.getFolder(parseInt(params['id']));
        });
    } 
    
    handlePageEvent(e: PageEvent) {
        this.pageSize = e.pageSize;
        this.pageIndex = e.pageIndex;

        this.questions$ = this.folderService.getQuestionsFromFolder(this.id(), this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }

    updateQuestions = () => {
        this.questions$ = this.folderService.getQuestionsFromFolder(this.id(), this.pageIndex, this.pageSize, 
            {field: sortFields[this.sortValue()], direction: orderDirection[this.orderValue()]});
    }
    
}