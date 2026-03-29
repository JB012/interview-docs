import { Component, ElementRef, signal, ViewChild } from "@angular/core";
import { ClickOutside } from "../click-outside";
import {MatChipEditedEvent, MatChipInputEvent, MatChipsModule} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { MatInputModule } from "@angular/material/input";
import {MatIconModule} from '@angular/material/icon';

export interface Folder {
    name: string
}

@Component({
    selector: "folder-search",
    templateUrl: 'folder-search.html',
    imports: [
        ClickOutside,
        MatChipsModule,
        MatInputModule,
        MatIconModule
    ]
})

export class FolderSearch {
    folderMenuOpened=signal(false);
    @ViewChild('folderInput') folderInput : ElementRef<HTMLInputElement> | undefined;
    @ViewChild('testRef') testRef : ElementRef<HTMLInputElement> | undefined
    
    readonly addOnBlur = true;
    readonly separatorKeysCodes = [ENTER, COMMA] as const;
    readonly allFolders = signal<Folder[]>([{name: 'Lemon'}, {name: 'Lime'}, {name: 'Apple'}]);
    readonly addedFolders = signal<Folder[]>([]);

    updateFolderMenuOpened() {
        this.folderMenuOpened.update((value) => !value);
    }

    onClick(event : Event, folder : Folder) {
        if (event.target) {
            const target = event.target as HTMLInputElement;
            
            if (target.checked) {
                this.addedFolders.update(addedFolders => [...addedFolders, folder]);
            }
            else {
                this.remove(folder);
            }
        }
    }

    onClickOutside() {
        if (this.folderMenuOpened()) {
            this.folderMenuOpened.set(false);
        }
    }

    add(event: MatChipInputEvent): void {
        const value = (event.value || '').trim().toLowerCase();

        const findFolder = this.allFolders().find((folder) => folder.name.toLowerCase() === value);
        const inFolder = this.addedFolders().some((folder) => folder.name.toLowerCase() === value);

        if (findFolder && !inFolder) {
            this.addedFolders.update(addedFolders => [...addedFolders, findFolder]);
        }

        // Clear the input value
        event.chipInput!.clear();
    }

    remove(folder: Folder): void {
        this.addedFolders.update(addedFolders => {
        const index = addedFolders.indexOf(folder);

        if (index < 0) {
            return addedFolders;
        }

        addedFolders.splice(index, 1);
        return [...addedFolders];
        });
    }

}