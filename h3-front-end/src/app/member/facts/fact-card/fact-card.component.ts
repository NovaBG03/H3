import {Component, Input, OnInit} from '@angular/core';
import {Fact, FamilyMember} from '../../../shared/dtos.model';

@Component({
  selector: 'app-fact-card',
  templateUrl: './fact-card.component.html',
  styleUrls: ['./fact-card.component.css']
})
export class FactCardComponent implements OnInit {
  @Input() fact: Fact;
  @Input() familyMember: FamilyMember;
  @Input() pictureUrl: string;

  constructor() {
  }

  ngOnInit(): void {
  }

}
