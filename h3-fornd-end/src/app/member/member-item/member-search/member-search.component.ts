import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FamilyMember, FamilyMembers} from '../../../shared/dtos.model';
import {merge, Observable, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';
import {NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-member-search',
  templateUrl: './member-search.component.html',
  styleUrls: ['./member-search.component.css']
})
export class MemberSearchComponent implements OnInit {
  @Input() controlName: string;
  @Input() control: FormControl;

  @Input() label: string;
  @Input() parentForm: FormGroup;
  @Input() familyMembers: FamilyMembers;
  @Input() familyMember: FamilyMember;

  @Input() canClose = false;
  @Output() closeButtonClicked = new EventEmitter<void>();

  @ViewChild('instance', {static: false}) instance: NgbTypeahead;
  focus$ = new Subject<string>();
  click$ = new Subject<string>();

  constructor() {
  }

  ngOnInit(): void {
  }

  onClose(): void {
    this.closeButtonClicked.emit();
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
  };
}
