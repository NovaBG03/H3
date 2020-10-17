import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {
  @Input() message: string;
  @Output() onClose = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

  close(): void {
    this.onClose.emit();
  }

}
