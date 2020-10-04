import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {FamilyMember, FamilyMembers} from '../../../shared/dtos.model';
import {merge, Observable, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';
import {NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-member-search',
  templateUrl: './member-search.component.html',
  styleUrls: ['./member-search.component.css']
})
export class MemberSearchComponent implements OnInit {
  @Input() controlName: string;
  @Input() label: string;
  @Input() parentForm: FormGroup;
  @Input() familyMembers: FamilyMembers;
  @Input() familyMember: FamilyMember;

  @ViewChild('instance', {static: true}) instance: NgbTypeahead;
  focus$ = new Subject<string>();
  click$ = new Subject<string>();

  constructor() { }

  ngOnInit(): void {
  }

  formatter(member: FamilyMember): string {
    return member.fullName;
  }

  search = (text$: Observable<string>) => {
    const debouncedText$ = text$.pipe(debounceTime(200), distinctUntilChanged());
    const clicksWithClosedPopup$ = this.click$.pipe(filter(() => !this.instance.isPopupOpen()));
    const inputFocus$ = this.focus$;

    return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
      map(term => (term === '' ? this.familyMembers.members
        : this.familyMembers.members.filter(m => m.fullName.toLowerCase().indexOf(term.toLowerCase()) > -1)).slice(0, 10))
    );
  }
}
