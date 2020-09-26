import {Component, OnInit} from '@angular/core';
import {MemberService} from '../member.service';
import {ActivatedRoute} from '@angular/router';
import {FamilyMember, FamilyMembers} from '../../shared/dtos.model';

@Component({
  selector: 'app-view-table',
  templateUrl: './view-table.component.html',
  styleUrls: ['./view-table.component.css']
})
export class ViewTableComponent implements OnInit {
  familyMembers: FamilyMembers = null;
  displayMembers: FamilyMember[] = null;
  // add array of members if you need correct list of members to do CRUD ops
  highlightedMemberId: number = null;
  editingMember: FamilyMember = null;

  constructor(private memberService: MemberService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.parent.url.subscribe(parentUrlSegment => {
      const treeId = +parentUrlSegment[0];
      this.memberService.getMembers(treeId).subscribe(familyMembers => {
        this.familyMembers = familyMembers;
        const members = familyMembers.members;
        this.displayMembers = members.map(member => this.swapParentsIfNeeded(member, members));
      });
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

  private swapParentsIfNeeded(member: FamilyMember, familyMembers: FamilyMember[]): FamilyMember {
    const primary = familyMembers.find(fm => fm.id === member.primaryParentId);
    if (!primary) {
      return member;
    }

    const secondary = familyMembers.find(fm => fm.id === member.secondaryParentId);
    if (!secondary) {
      return member;
    }

    if (primary.gender === 'FEMALE' && secondary.gender === 'MALE') {
      member.primaryParentId = secondary.id;
      member.secondaryParentId = primary.id;
      return member;
    }

    return member;
  }
}
