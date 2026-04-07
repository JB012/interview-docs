import { Component, input } from "@angular/core";

@Component({
    selector: 'question',
    template:  `
    <li class="flex justify-between">
        <a [href]="'questions/' + this.id()" class="truncate cursor-pointer p-2 w-full hover:backdrop-brightness-97"> 
            {{ this.question() }}
        </a>
        <span class="material-symbols-outlined">&#xe5cc;</span>
    </li>
        `
})

export class Question {
    id = input.required<string>();
    question = input.required<string>();
}