import { Component, signal } from "@angular/core";
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

    readonly addOnBlur = true;
    readonly separatorKeysCodes = [ENTER, COMMA] as const;
    readonly folders = signal<Folder[]>([{name: 'Lemon'}, {name: 'Lime'}, {name: 'Apple'}]);

    updateFolderMenuOpened() {
        this.folderMenuOpened.update((value) => !value);
    }

    onClickOutside() {
        if (this.folderMenuOpened()) {
            this.folderMenuOpened.set(false);
        }
    }

    onChange(event : InputEvent, value : string) {
        const target = event.target as HTMLInputElement;
        if (target.checked) {
            // TODO: Clicking on label should add chip
        }
    }

    add(event: MatChipInputEvent): void {
        const value = (event.value || '').trim();

        if (value) {
        this.folders.update(folders => [...folders, {name: value}]);
        }

        // Clear the input value
        event.chipInput!.clear();
    }

    remove(folder: Folder): void {
    this.folders.update(folders => {
      const index = folders.indexOf(folder);
      if (index < 0) {
        return folders;
      }

      folders.splice(index, 1);
      return [...folders];
    });
  }

}