import { Component, signal } from "@angular/core";
import { TuiTextfield, TuiDataListComponent, TuiOptGroup, TuiDataList } from '@taiga-ui/core';
import { TuiChevron, TuiDataListWrapper, TuiInputChip, TuiMultiSelect} from '@taiga-ui/kit';
import { FormsModule } from "@angular/forms";
import { tuiIsString } from "@taiga-ui/cdk/utils/miscellaneous";
import { CdkFixedSizeVirtualScroll, CdkVirtualScrollViewport } from "@angular/cdk/scrolling";

export interface Folder {
    name: string
    id: string
    checked: boolean
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
    TuiDataListComponent,
    TuiOptGroup, 
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

    readonly allFolders = signal<Folder[]>([{name: 'Lemon', id: "1", checked: false}, {name: 'Lime', id: "2", checked: false}, {name: 'Apple', id: "3", checked: false}, 
        {name: 'Lemon', id: "4", checked: false}, {name: 'Lime', id: "5", checked: false}, {name: 'Apple', id: "6", checked: false},
        {name: 'Lemon', id: "7", checked: false}, {name: 'Lime', id: "8", checked: false}, {name: 'Apple', id: "9", checked: false}
    ]);
    readonly addedFolders = signal<Folder[]>([]);

    shouldDisable(folder: Folder) {
        return this.addedFolders().length === 5 && !this.addedFolders().some((elem) => elem.id === folder.id);
    }
    
    content() {
        return `Pick folders (${this.addedFolders().length}/5)`
    }
}
