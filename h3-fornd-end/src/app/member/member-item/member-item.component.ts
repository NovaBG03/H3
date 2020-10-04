import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FamilyMember, FamilyMemberDataDTO, FamilyMembers, Gender} from '../../shared/dtos.model';
import {FormControl, FormGroup} from '@angular/forms';
import {NgbDate, NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {MemberService} from '../member.service';
import {merge, Observable, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';

@Component({
  selector: 'app-member-item',
  templateUrl: './member-item.component.html',
  styleUrls: ['./member-item.component.css']
})
export class MemberItemComponent implements OnInit {
  @Input() treeId: number;
  @Input('familyMembers') familyMembers: FamilyMembers;
  @Input('familyMember') member: FamilyMember;
  memberChildren: FamilyMember[];
  memberForm: FormGroup;
  genders: string[] = Object.values(Gender);

  // outputs true if change is made, false if it isn't
  @Output() finishEditing = new EventEmitter<boolean>();


  constructor(private memberService: MemberService) {
  }

  ngOnInit(): void {
    this.memberChildren = this.familyMembers.getChildren(this.member.id);
    this.initForm();
    console.log(this.member);
  }

  onCancel(): void {
    this.finishEditing.emit(false);
  }

  onSave(): void {
    // save member
    const value = this.memberForm.value;

    const ngbBirthday: NgbDate = value.birthday;
    let birthday = null;
    if (ngbBirthday) {
      birthday = new Date(ngbBirthday.year, ngbBirthday.month - 1, ngbBirthday.day + 1);
    }

    const ngbDateOfDeath: NgbDate = value.dateOfDeath;
    let dateOfDeath = null;
    if (ngbDateOfDeath) {
      dateOfDeath = new Date(ngbDateOfDeath.year, ngbDateOfDeath.month - 1, ngbDateOfDeath.day + 1);
    }

    const familyMemberDataDTO = new FamilyMemberDataDTO(
      value.firstName,
      value.lastName,
      birthday,
      dateOfDeath,
      value.gender.toUpperCase(),
      this.member.primaryParentId === 0 ? null : this.member.primaryParentId,
      this.member.secondaryParentId === 0 ? null : this.member.secondaryParentId
    );

    this.memberService.updateMember(this.treeId, this.member.id, familyMemberDataDTO)
      .subscribe(familyMember => this.finishEditing.emit(true));
  }

  private initForm(): void {
    const birthday = this.member.birthday;
    let ngbBirthday = null;
    if (birthday) {
      ngbBirthday = new NgbDate(birthday.getUTCFullYear(), birthday.getUTCMonth() + 1, birthday.getUTCDate());
    }

    const dateOfDeath = this.member.dateOfDeath;
    let ngbDateOfDeath = null;
    if (dateOfDeath) {
      ngbDateOfDeath = new NgbDate(dateOfDeath.getUTCFullYear(), dateOfDeath.getUTCMonth() + 1, dateOfDeath.getUTCDate());
    }

    this.memberForm = new FormGroup({
      firstName: new FormControl(this.member.firstName),
      lastName: new FormControl(this.member.lastName),
      gender: new FormControl(this.member.gender),
      birthday: new FormControl(ngbBirthday),
      dateOfDeath: new FormControl(ngbDateOfDeath),
      primaryParent: new FormControl(this.familyMembers.getMember(this.member.primaryParentId)),
      secondaryParent: new FormControl(this.familyMembers.getMember(this.member.secondaryParentId))
    });

    this.memberForm.valueChanges.subscribe(value => console.log(value));
  }
}
