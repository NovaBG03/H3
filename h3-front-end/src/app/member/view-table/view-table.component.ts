import {Component, OnDestroy, OnInit} from '@angular/core';
import {MemberService} from '../member.service';
import {ActivatedRoute} from '@angular/router';
import {FamilyMember, FamilyMembers, Gender} from '../../shared/dtos.model';
import {AuthService} from '../../authentication/auth.service';
import {TreeService} from '../../tree/tree.service';
import {map, switchMap, tap} from 'rxjs/operators';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-view-table',
  templateUrl: './view-table.component.html',
  styleUrls: ['./view-table.component.css']
})
export class ViewTableComponent implements OnInit, OnDestroy {
  displayMembers: FamilyMember[];
  highlightedMemberId: number = null;

  treeId: number;
  familyMembers: FamilyMembers;
  editingMember: FamilyMember = null;
  isCreatingMember = false;
  isOwner: boolean;
  private userSub: Subscription;

  constructor(private memberService: MemberService,
              private route: ActivatedRoute,
              private authService: AuthService,
              private treeService: TreeService) {
  }

  ngOnInit(): void {
    this.userSub = this.route.parent.url.pipe(
      map(parentUrlSegment => +parentUrlSegment[0]),
      tap(treeId => this.treeId = treeId),
      switchMap(treeId => {
        return this.treeService.getTree(treeId).pipe(
          switchMap(tree => {
            return this.authService.user.pipe(
              map(user => user.username === tree.owner)
            );
          }));
      })
    ).subscribe(isOwner => {
      this.isOwner = isOwner;
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

  onStartCreating(): void {
    this.isCreatingMember = true;
  }

  onFinishEditing(isChanged: boolean): void {
    this.editingMember = null;
    this.isCreatingMember = false;
    if (isChanged) {
      this.loadMembers();
    }
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  private loadMembers(): void {
    // this.memberService.getCouples(this.treeId)
    //   .subscribe(familyMembers => {
    //     this.familyMembers = familyMembers;
    //
    //     const membersClone: FamilyMember[] = [];
    //     familyMembers.members.forEach(member => {
    //       membersClone.push(new FamilyMember(
    //         member.id,
    //         member.firstName,
    //         member.lastName,
    //         member.birthday ? new Date(member.birthday) : null,
    //         member.dateOfDeath ? new Date(member.dateOfDeath) : null,
    //         member.gender,
    //         member.primaryParentId,
    //         member.secondaryParentId,
    //         member.partners.slice()));
    //     });
    //
    //     this.displayMembers = membersClone.map(member => this.swapParentsIfNeeded(member, membersClone));
    //   });
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
