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

    if (this.isNotNew) {

      // // some big brain stuff
      // const childrenToUpdate: FamilyMember[] = [];
      // const partnersToUpdate: FamilyMember[] = this.member.partners.map(id => this.familyMembers.getMember(id));
      // for (let i = 0; i < value.partners.length; i++) {
      //   const partner = this.familyMembers.getMember(value.partners[i].id);
      //
      //   const partnerIndex = partnersToUpdate.indexOf(partner);
      //   if (partnerIndex > -1) {
      //     partnersToUpdate.splice(partnerIndex, 1);
      //   }
      //
      //   const multipleChildrenToRemove = this.familyMembers.getMultipleChildren(this.member.id, partner.id);
      //   for (const child of value.children[i]) {
      //     const childMember = this.familyMembers.getMember(child.id);
      //
      //     const childIndex = multipleChildrenToRemove.indexOf(childMember);
      //     if (childIndex > -1) {
      //       multipleChildrenToRemove.splice(childIndex, 1);
      //     }
      //
      //     if (this.member.isDirectHeir || this.member.id === this.familyMembers.mainMember.id) {
      //       if (childMember.primaryParentId !== this.member.id || childMember.secondaryParentId !== partner.id) {
      //         childMember.primaryParentId = this.member.id;
      //         childMember.secondaryParentId = partner.id;
      //         childrenToUpdate.push(childMember);
      //       }
      //     } else {
      //       if (childMember.primaryParentId !== partner.id || childMember.secondaryParentId !== this.member.id) {
      //         childMember.primaryParentId = partner.id;
      //         childMember.secondaryParentId = this.member.id;
      //         childrenToUpdate.push(childMember);
      //       }
      //     }
      //   }
      //
      //   for (const multipleChild of multipleChildrenToRemove) {
      //     if (!childrenToUpdate.includes(multipleChild)) {
      //       multipleChild.primaryParentId = null;
      //       multipleChild.secondaryParentId = null;
      //       childrenToUpdate.push(multipleChild);
      //     }
      //   }
      // }
      //
      // for (const partner of partnersToUpdate) {
      //   const multipleChildren = this.familyMembers.getMultipleChildren(this.member.id, partner.id);
      //   for (const multipleChild of multipleChildren) {
      //     if (!childrenToUpdate.includes(multipleChild)) {
      //       multipleChild.primaryParentId = null;
      //       multipleChild.secondaryParentId = null;
      //       childrenToUpdate.push(multipleChild);
      //     }
      //   }
      // }
      //
      // // TODO update members in one request
      // childrenToUpdate.map(child => {
      //   this.memberService.updateMember(this.treeId, child.id, new FamilyMemberDataDTO(
      //     child.firstName,
      //     child.lastName,
      //     child.birthday !== null ? child.birthday.toJSON() : null,
      //     child.dateOfDeath !== null ? child.dateOfDeath.toJSON() : null,
      //     child.gender.toUpperCase(),
      //     child.primaryParentId,
      //     child.secondaryParentId
      //   )).subscribe();
      // });

      // todo dont send request for all members
      this.memberService
        .updateMember(this.treeId, this.familyMember.id, familyMemberDataDTO)
        .subscribe(familyMember => this.finishEditing.emit(true));
    } else {
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
