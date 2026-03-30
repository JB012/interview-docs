import { Component, signal } from "@angular/core";
import { TuiTextfield, TuiDataListComponent, TuiOptGroup, TuiDataList } from '@taiga-ui/core';
import { TuiChevron, TuiDataListWrapper, TuiInputChip, TuiMultiSelect} from '@taiga-ui/kit';
import { FormsModule } from "@angular/forms";
import { tuiIsString } from "@taiga-ui/cdk/utils/miscellaneous";
import { CdkFixedSizeVirtualScroll, CdkVirtualScrollViewport } from "@angular/cdk/scrolling";

export interface Folder {
    name: string
}

@Component({
    selector: "folder-search",
    templateUrl: 'folder-search.html',
    imports: [
    FormsModule,
    CdkFixedSizeVirtualScroll,
    CdkVirtualScrollViewport,
    TuiTextfield,
    TuiInputChip,
    TuiMultiSelect,
    TuiChevron,
    TuiChevron,
    TuiDataList,
    TuiDataListWrapper
]
})

export class FolderSearch {
    maxAddedFoldersLimit = 5;
    
    protected readonly strings = tuiIsString;
    protected readonly stringify = ({name}: Folder): string => name;

    readonly allFolders = signal<Folder[]>([{name: 'Lemon'}, {name: 'Lime'}, {name: 'Apple'}
    ]);
    readonly addedFolders = signal<Folder[]>([]);


    shouldDisable = (folder: Folder) => {
        return this.addedFolders().length === 5 && !this.addedFolders().some((elem) => elem.name === folder.name);
    }
    
    content() {
        return `Pick folders (${this.addedFolders().length}/5)`
    }
}
