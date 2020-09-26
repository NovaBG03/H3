import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FamilyMember, FamilyMembers} from '../../shared/dtos.model';

@Component({
  selector: 'app-member-item',
  templateUrl: './member-item.component.html',
  styleUrls: ['./member-item.component.css']
})
export class MemberItemComponent implements OnInit {
  @Input('familyMembers') members: FamilyMembers;
  @Input('familyMember') member: FamilyMember;
  children: FamilyMember[];
  hasMadeChange = false;

  // outputs true if change is made, false if it isn't
  @Output() cancelEditing = new EventEmitter<boolean>();
  @Output() finishEditing = new EventEmitter<FamilyMember[]>();

  constructor() {
  }

  ngOnInit(): void {
    this.children = this.members.getChildren(this.member.id);
  }

  onCancel(): void {
    this.cancelEditing.emit(false);
  }
}
