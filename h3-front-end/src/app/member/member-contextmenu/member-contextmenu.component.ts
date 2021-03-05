import {AfterViewInit, Component, ElementRef, EventEmitter, Input, Output, Renderer2, ViewChild} from '@angular/core';

@Component({
  selector: 'app-member-contextmenu',
  templateUrl: './member-contextmenu.component.html',
  styleUrls: ['./member-contextmenu.component.css']
})
export class MemberContextmenuComponent implements AfterViewInit {

  @ViewChild('h3Contextmenu', {read: ElementRef}) elementRef: ElementRef;
  @Output() memberInfo: EventEmitter<void> = new EventEmitter<void>();

  isPrimary: boolean;

  constructor(private renderer: Renderer2) {
  }

  @Input('rightClickInfo') set rightClickInfo(info: { x: number, y: number, isPrimary: boolean }) {
    if (!this.elementRef) {
      return;
    }

    if (info) {
      this.isPrimary = info.isPrimary;
      this.showElement(info.x, info.y);
    } else {
      this.hideElement();
    }
  }

  ngAfterViewInit(): void {
  }

  onMemberInfoClicked(): void {
    this.memberInfo.emit();
  }

  private hideElement(): void {
    this.renderer
      .setStyle(this.elementRef.nativeElement, 'display', 'none');
  }

  private showElement(x: number, y: number): void {
    this.renderer
      .setStyle(this.elementRef.nativeElement, 'top', y + 'px');

    this.renderer
      .setStyle(this.elementRef.nativeElement, 'left', x + 'px');

    this.renderer
      .setStyle(this.elementRef.nativeElement, 'display', 'block');
  }
}
