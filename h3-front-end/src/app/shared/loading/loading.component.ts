import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit} from '@angular/core';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css']
})
export class LoadingComponent implements OnInit, AfterViewInit, OnDestroy {
  private initialOverFlow;

  constructor(private elementRef: ElementRef) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    console.log(this.elementRef.nativeElement.ownerDocument.body.style.overflow);
    this.initialOverFlow = this.elementRef.nativeElement.ownerDocument.body.style.overflow;
    this.elementRef.nativeElement.ownerDocument.body.style.overflow = 'hidden';
  }

  ngOnDestroy(): void {
    this.elementRef.nativeElement.ownerDocument.body.style.overflow = this.initialOverFlow;
  }

}
