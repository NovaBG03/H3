import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Couple, FamilyMember, FamilyMemberDataDTO, Gender} from '../../shared/dtos.model';
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
  @Input() familyMember: FamilyMember; // if is null -> creating new user
  @Input() isOwner: boolean;
  @Input() parentCouple: Couple;
  @Input() isNewPartner = false;
  memberForm: FormGroup;
  genders: string[] = Object.values(Gender);
  isNotNew = true;

  // outputs true if change is made, false if it isn't
  @Output() finishEditing = new EventEmitter<boolean>();

  constructor(private memberService: MemberService) {
  }

  ngOnInit(): void {
    if (!this.familyMember) {
      this.familyMember = new FamilyMember(null,
        '',
        '',
        null,
        null,
        Gender.UNKNOWN);
      this.isNotNew = false;
    }

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
      value.gender.toUpperCase()
    );

    if (this.isNewPartner) {
      this.memberService
        .addPartner(this.treeId, familyMemberDataDTO, this.parentCouple.primaryParentId)
        .subscribe(familyMember => this.finishEditing.emit(true));
    } else if (this.isNotNew) {
      this.memberService
        .updateMember(this.treeId, this.familyMember.id, familyMemberDataDTO)
        .subscribe(familyMember => this.finishEditing.emit(true));
    } else {
      if (!this.parentCouple || (!this.parentCouple.primaryParentId && !this.parentCouple.partnerParentId)) {
        this.parentCouple = {
          primaryParentId: 0,
          partnerParentId: 0,
          depthIndex: null,
          leftIndex: null,
          rightIndex: null,
          partnerParentName: null,
          primaryParentName: null,
          treeId: null
        };
      }

      this.memberService
        .createMember(this.treeId, familyMemberDataDTO, this.parentCouple.primaryParentId, this.parentCouple.partnerParentId)
        .subscribe(familyMember => this.finishEditing.emit(true));
    }

  }


  onDelete(): void {
    this.memberService.deleteMember(this.treeId, this.familyMember.id)
      .subscribe(message => this.finishEditing.emit(true));
  }

  private initForm(): void {
    const birthday = this.familyMember.birthday;
    let ngbBirthday = null;
    if (birthday) {
      ngbBirthday = new NgbDate(birthday.getUTCFullYear(), birthday.getUTCMonth() + 1, birthday.getUTCDate());
    }

    const dateOfDeath = this.familyMember.dateOfDeath;
    let ngbDateOfDeath = null;
    if (dateOfDeath) {
      ngbDateOfDeath = new NgbDate(dateOfDeath.getUTCFullYear(), dateOfDeath.getUTCMonth() + 1, dateOfDeath.getUTCDate());
    }

    this.memberForm = new FormGroup({
      firstName: new FormControl(this.familyMember.firstName),
      lastName: new FormControl(this.familyMember.lastName),
      gender: new FormControl(this.familyMember.gender),
      birthday: new FormControl(ngbBirthday),
      dateOfDeath: new FormControl(ngbDateOfDeath),
    });

    // this.memberForm.valueChanges.subscribe(value => console.log(value));
  }
}
