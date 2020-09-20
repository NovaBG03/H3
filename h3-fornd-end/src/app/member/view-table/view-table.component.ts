import {Component, OnInit} from '@angular/core';
import {MemberService} from '../member.service';
import {ActivatedRoute} from '@angular/router';
import {FamilyMember} from '../../shared/dtos.model';

@Component({
  selector: 'app-view-table',
  templateUrl: './view-table.component.html',
  styleUrls: ['./view-table.component.css']
})
export class ViewTableComponent implements OnInit {
  members: FamilyMember[] = null;
  selectedMemberId: number = null;

  constructor(private memberService: MemberService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.parent.url.subscribe(parentUrlSegment => {
      const treeId = +parentUrlSegment[0];
      this.memberService.getMembers(treeId).subscribe(familyMembers => {
        this.members = familyMembers;
      });
    });
  }

  getMember(id: number): FamilyMember {
    return this.members.find(member => member.id === id);
  }

  getMemberFullName(id: number): string {
    const member = this.getMember(id);

    if (!member) {
      return '-';
    }

    return member.firstName + ' ' + member.lastName;
  }

  onMemberSelected(id: number): void {
    this.selectedMemberId = id;
  }
}
