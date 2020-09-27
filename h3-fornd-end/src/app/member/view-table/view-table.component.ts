import {Component, OnInit} from '@angular/core';
import {MemberService} from '../member.service';
import {ActivatedRoute} from '@angular/router';
import {FamilyMember, FamilyMembers, Gender} from '../../shared/dtos.model';

@Component({
  selector: 'app-view-table',
  templateUrl: './view-table.component.html',
  styleUrls: ['./view-table.component.css']
})
export class ViewTableComponent implements OnInit {
  treeId: number;
  familyMembers: FamilyMembers;
  displayMembers: FamilyMember[];
  highlightedMemberId: number = null;
  editingMember: FamilyMember = null;

  constructor(private memberService: MemberService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.parent.url.subscribe(parentUrlSegment => {
      this.treeId = +parentUrlSegment[0];
      this.loadMembers();
    });
  }

  getDisplayMember(id: number): FamilyMember {
    return this.displayMembers.find(member => member.id === id);
  }

  getDisplayMemberFullName(id: number): string {
    const member = this.getDisplayMember(id);

    if (!member) {
      return '-';
    }

    return member.firstName + ' ' + member.lastName;
  }

  onMemberHighlighted(id: number): void {
    this.highlightedMemberId = id;
  }

  onStartEditing(memberId: number): void {
    this.editingMember = this.familyMembers.getMember(memberId);
  }

  onFinishEditing(isChanged: boolean): void {
    this.editingMember = null;
    if (isChanged) {
      this.loadMembers();
    }
  }

  private loadMembers(): void {
    this.memberService.getMembers(this.treeId)
      .subscribe(familyMembers => {
        this.familyMembers = familyMembers;

        const membersClone: FamilyMember[] = [];
        familyMembers.members.forEach(member => {
          membersClone.push(new FamilyMember(
            member.id,
            member.firstName,
            member.lastName,
            member.birthday ? new Date(member.birthday) : null,
            member.dateOfDeath ? new Date(member.dateOfDeath) : null,
            member.gender,
            member.primaryParentId,
            member.secondaryParentId,
            member.partners.slice()));
        });

        this.displayMembers = membersClone.map(member => this.swapParentsIfNeeded(member, membersClone));
      });
  }

  private swapParentsIfNeeded(member: FamilyMember, familyMembers: FamilyMember[]): FamilyMember {
    const primary = familyMembers.find(fm => fm.id === member.primaryParentId);
    if (!primary) {
      return member;
    }

    const secondary = familyMembers.find(fm => fm.id === member.secondaryParentId);
    if (!secondary) {
      return member;
    }

    if (primary.gender === Gender.FEMALE && secondary.gender === Gender.MALE) {
      member.primaryParentId = secondary.id;
      member.secondaryParentId = primary.id;
      return member;
    }

    return member;
  }
}
