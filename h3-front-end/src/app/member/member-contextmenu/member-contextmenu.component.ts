import {Component, ElementRef, EventEmitter, Input, Output, Renderer2, ViewChild} from '@angular/core';

@Component({
  selector: 'app-member-contextmenu',
  templateUrl: './member-contextmenu.component.html',
  styleUrls: ['./member-contextmenu.component.css']
})
export class MemberContextmenuComponent {

  @ViewChild('h3Contextmenu', {read: ElementRef}) elementRef: ElementRef;
  @Output() memberInfo: EventEmitter<void> = new EventEmitter<void>();
  @Output() addChild: EventEmitter<void> = new EventEmitter<void>();
  @Output() addPartner: EventEmitter<void> = new EventEmitter<void>();
  @Output() delete: EventEmitter<void> = new EventEmitter<void>();
  @Output() changePicture: EventEmitter<void> = new EventEmitter<void>();

  isPrimary: boolean;
  isMain: boolean;
  isSolo: boolean;
  isOwner: boolean;

  constructor(private renderer: Renderer2) {
  }

  @Input('rightClickInfo') set rightClickInfo(
    info: { x: number, y: number, isPrimary: boolean, isMain: boolean, isSolo: boolean, isOwner: boolean }) {

    if (!this.elementRef) {
      return;
    }

    if (info) {
      this.isPrimary = info.isPrimary;
      this.isMain = info.isMain;
      this.isSolo = info.isSolo;
      this.isOwner = info.isOwner;
      this.showElement(info.x, info.y);
    } else {
      this.hideElement();
    }
  }

  onMemberInfoClicked(): void {
    this.memberInfo.emit();
  }

  onAddChildClicked(): void {
    this.addChild.emit();
  }

  onAddPartner(): void {
    this.addPartner.emit();
  }

  onDelete(): void {
    this.delete.emit();
  }

  onChangePicture(): void {
    this.changePicture.emit();
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
