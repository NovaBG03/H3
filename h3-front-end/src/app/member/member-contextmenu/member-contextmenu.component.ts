import {AfterViewInit, Component, ElementRef, EventEmitter, Input, Output, Renderer2, ViewChild} from '@angular/core';

@Component({
  selector: 'app-member-contextmenu',
  templateUrl: './member-contextmenu.component.html',
  styleUrls: ['./member-contextmenu.component.css']
})
export class MemberContextmenuComponent implements AfterViewInit {

  @ViewChild('h3Contextmenu', {read: ElementRef}) elementRef: ElementRef;
  @Output() memberInfo: EventEmitter<void> = new EventEmitter<void>();

  constructor(private renderer: Renderer2) {
  }

  @Input('coordinates') set coordinates(coordinates: { x: number, y: number }) {
    if (!this.elementRef) {
      return;
    }

    if (coordinates) {
      this.showElement(coordinates);
    } else {
      this.hideElement();
    }
  }

  ngAfterViewInit(): void {
  }

  private hideElement(): void {
    this.renderer
      .setStyle(this.elementRef.nativeElement, 'display', 'none');
  }

  private showElement(coordinates: { x: number, y: number }): void {
    this.renderer
      .setStyle(this.elementRef.nativeElement, 'top', coordinates.y + 'px');

    this.renderer
      .setStyle(this.elementRef.nativeElement, 'left', coordinates.x + 'px');

    this.renderer
      .setStyle(this.elementRef.nativeElement, 'display', 'block');
  }

  onMemberInfoClicked(): void {
    this.memberInfo.emit();
  }
}
