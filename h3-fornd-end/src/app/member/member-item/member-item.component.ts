import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FamilyMember, FamilyMemberDataDTO, FamilyMembers, Gender} from '../../shared/dtos.model';
import {FormControl, FormGroup} from '@angular/forms';
import {NgbDate} from '@ng-bootstrap/ng-bootstrap';
import {MemberService} from '../member.service';

@Component({
  selector: 'app-member-item',
  templateUrl: './member-item.component.html',
  styleUrls: ['./member-item.component.css']
})
export class MemberItemComponent implements OnInit {
  @Input() treeId: number;
  @Input('familyMembers') members: FamilyMembers;
  @Input('familyMember') member: FamilyMember;
  memberChildren: FamilyMember[];
  hasMadeChange = false;
  memberForm: FormGroup;
  genders: string[] = Object.values(Gender);

  // outputs true if change is made, false if it isn't
  @Output() finishEditing = new EventEmitter<boolean>();

  constructor(private memberService: MemberService) {
  }

  ngOnInit(): void {
    this.memberChildren = this.members.getChildren(this.member.id);
    this.initForm();
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
      birthday = new Date(ngbBirthday.year, ngbBirthday.month, ngbBirthday.day);
    }

    const ngbDateOfDeath: NgbDate = value.dateOfDeath;
    let dateOfDeath = null;
    if (ngbDateOfDeath) {
      dateOfDeath = new Date(ngbDateOfDeath.year, ngbDateOfDeath.month, ngbDateOfDeath.day);
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
      ngbBirthday = new NgbDate(birthday.getFullYear(), birthday.getMonth(), birthday.getDate());
    }

    const dateOfDeath = this.member.dateOfDeath;
    let ngbDateOfDeath = null;
    if (dateOfDeath) {
      ngbDateOfDeath = new NgbDate(dateOfDeath.getFullYear(), dateOfDeath.getMonth(), dateOfDeath.getDate());
    }

    this.memberForm = new FormGroup({
      firstName: new FormControl(this.member.firstName),
      lastName: new FormControl(this.member.lastName),
      gender: new FormControl(this.member.gender),
      birthday: new FormControl(ngbBirthday),
      dateOfDeath: new FormControl(ngbDateOfDeath),
    });
  }
}
