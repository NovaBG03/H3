import {Component, OnInit} from '@angular/core';
import {Fact, FamilyMember} from '../../shared/dtos.model';
import {FactService} from './fact.service';
import {map, switchMap, tap} from 'rxjs/operators';
import {of} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {MemberService} from '../member.service';

@Component({
  selector: 'app-facts',
  templateUrl: './facts.component.html',
  styleUrls: ['./facts.component.css']
})
export class FactsComponent implements OnInit {
  facts: Fact[];
  familyMembers: FamilyMember[];

  constructor(private factService: FactService,
              private memberService: MemberService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.url.pipe(
      map(urlSegment => +urlSegment[0].path),
      switchMap(treeId => treeId ? of(treeId) : this.route.parent.url.pipe(
        map(parentUrlSegment => +parentUrlSegment[0].path)
      )),
      tap(treeId => this.memberService.getFamilyMembers(treeId)
        .subscribe(members => this.familyMembers = members)),
      switchMap(treeId => this.factService.getAllFacts(treeId))
    ).subscribe(facts => {
      this.facts = facts.sort((a, b) =>
        a.date.getTime() - b.date.getTime());
    });
  }

  getMember(familyMemberId: number): FamilyMember {
    return this.familyMembers.find(familyMember => familyMember.id === familyMemberId)
  }
}
