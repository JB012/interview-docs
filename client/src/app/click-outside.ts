import { Directive, ElementRef, output } from '@angular/core';

@Directive({
  selector: '[appClickOutside]',
  host: {
    '(document:click)': 'onClick($event)'
  }
})
export class ClickOutside {
  clickOutside = output();

  constructor(private elementRef : ElementRef) {}

  onClick(event : Event) {
    const clickedInside = this.elementRef.nativeElement.contains(event.target);

    console.log(clickedInside);
    
    if (!clickedInside) {
      this.clickOutside.emit();
    }
  }
}
