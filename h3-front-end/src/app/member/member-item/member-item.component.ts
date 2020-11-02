import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FamilyMember, FamilyMemberDataDTO, FamilyMembers, Gender} from '../../shared/dtos.model';
import {FormArray, FormControl, FormGroup} from '@angular/forms';
import {NgbDate} from '@ng-bootstrap/ng-bootstrap';
import {MemberService} from '../member.service';

@Component({
  selector: 'app-member-item',
  templateUrl: './member-item.component.html',
  styleUrls: ['./member-item.component.css']
})
export class MemberItemComponent implements OnInit {
  @Input() treeId: number;
  @Input('familyMembers') familyMembers: FamilyMembers;
  @Input('familyMember') member: FamilyMember; // if is null -> creating new user
  @Input() isOwner: boolean;
  memberChildren: FamilyMember[] = [];
  memberForm: FormGroup;
  genders: string[] = Object.values(Gender);
  partnersFormArray: FormArray;
  childrenFormArray: FormArray;
  isNotNew = true;

  // outputs true if change is made, false if it isn't
  @Output() finishEditing = new EventEmitter<boolean>();

  constructor(private memberService: MemberService) {
  }

  ngOnInit(): void {
    if (!this.member) {
      this.member = new FamilyMember(null,
        '',
        '',
        null,
        null,
        Gender.UNKNOWN,
        null,
        null,
        []);
      this.isNotNew = false;
    }

    this.memberChildren = this.familyMembers.getChildren(this.member.id);
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
      value.gender.toUpperCase(),
      value.primaryParent ? value.primaryParent.id : null,
      value.secondaryParent ? value.secondaryParent.id : null
    );

    if (this.isNotNew) {

      // some big brain stuff
      const childrenToUpdate: FamilyMember[] = [];
      const partnersToUpdate: FamilyMember[] = this.member.partners.map(id => this.familyMembers.getMember(id));
      for (let i = 0; i < value.partners.length; i++) {
        const partner = this.familyMembers.getMember(value.partners[i].id);

        const partnerIndex = partnersToUpdate.indexOf(partner);
        if (partnerIndex > -1) {
          partnersToUpdate.splice(partnerIndex, 1);
        }

        const multipleChildrenToRemove = this.familyMembers.getMultipleChildren(this.member.id, partner.id);
        for (const child of value.children[i]) {
          const childMember = this.familyMembers.getMember(child.id);

          const childIndex = multipleChildrenToRemove.indexOf(childMember);
          if (childIndex > -1) {
            multipleChildrenToRemove.splice(childIndex, 1);
          }

          if (this.member.isDirectHeir || this.member.id === this.familyMembers.mainMember.id) {
            if (childMember.primaryParentId !== this.member.id || childMember.secondaryParentId !== partner.id) {
              childMember.primaryParentId = this.member.id;
              childMember.secondaryParentId = partner.id;
              childrenToUpdate.push(childMember);
            }
          } else {
            if (childMember.primaryParentId !== partner.id || childMember.secondaryParentId !== this.member.id) {
              childMember.primaryParentId = partner.id;
              childMember.secondaryParentId = this.member.id;
              childrenToUpdate.push(childMember);
            }
          }
        }

        for (const multipleChild of multipleChildrenToRemove) {
          if (!childrenToUpdate.includes(multipleChild)) {
            multipleChild.primaryParentId = null;
            multipleChild.secondaryParentId = null;
            childrenToUpdate.push(multipleChild);
          }
        }
      }

      for (const partner of partnersToUpdate) {
        const multipleChildren = this.familyMembers.getMultipleChildren(this.member.id, partner.id);
        for (const multipleChild of multipleChildren) {
          if (!childrenToUpdate.includes(multipleChild)) {
            multipleChild.primaryParentId = null;
            multipleChild.secondaryParentId = null;
            childrenToUpdate.push(multipleChild);
          }
        }
      }

      // TODO update members in one request
      childrenToUpdate.map(child => {
        this.memberService.updateMember(this.treeId, child.id, new FamilyMemberDataDTO(
          child.firstName,
          child.lastName,
          child.birthday !== null ? child.birthday.toJSON() : null,
          child.dateOfDeath !== null ? child.dateOfDeath.toJSON() : null,
          child.gender.toUpperCase(),
          child.primaryParentId,
          child.secondaryParentId
        )).subscribe();
      });

      this.memberService.updateMember(this.treeId, this.member.id, familyMemberDataDTO)
        .subscribe(familyMember => this.finishEditing.emit(true));
    } else {
      this.memberService.createMember(this.treeId, familyMemberDataDTO)
        .subscribe(familyMember => this.finishEditing.emit(true));
    }

  }

  addEmptyPartnerControl(): void {
    this.partnersFormArray.push(new FormControl());
    this.childrenFormArray.push(new FormArray([]));
  }

  removePartnerControl(index: number): void {
    this.partnersFormArray.removeAt(index);
    this.childrenFormArray.removeAt(index);
  }

  getChildControls(parentControlIndex: number): FormArray {
    return this.childrenFormArray.controls[parentControlIndex] as FormArray;
  }

  addEmptyChildArray(parentIndex: number): void {
    this.getChildControls(parentIndex).push(new FormControl());
  }

  removeChildControl(parentIndex: number, childIndex: number): void {
    this.getChildControls(parentIndex).removeAt(childIndex);
  }

  onDelete(): void {
    this.memberService.deleteMember(this.treeId, this.member.id)
      .subscribe(message => this.finishEditing.emit(true));
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

    this.partnersFormArray = new FormArray(this.member.partners.map(partnerId =>
      new FormControl(this.familyMembers.getMember(partnerId))
    ));

    this.childrenFormArray = new FormArray(this.partnersFormArray.controls.map(control => {
      const controlMember = control.value;

      if (!controlMember) {
        return new FormArray([]);
      }

      const multipleChildrenControls = this.familyMembers
        .getMultipleChildren(this.member.id, controlMember.id)
        .map(member => new FormControl(member));

      return new FormArray(multipleChildrenControls);
    }));

    this.memberForm = new FormGroup({
      firstName: new FormControl(this.member.firstName),
      lastName: new FormControl(this.member.lastName),
      gender: new FormControl(this.member.gender),
      birthday: new FormControl(ngbBirthday),
      dateOfDeath: new FormControl(ngbDateOfDeath),
      primaryParent: new FormControl(this.familyMembers.getMember(this.member.primaryParentId)),
      secondaryParent: new FormControl(this.familyMembers.getMember(this.member.secondaryParentId)),
      partners: this.partnersFormArray,
      children: this.childrenFormArray
    });
    // this.memberForm.valueChanges.subscribe(value => console.log(value));
  }
}
