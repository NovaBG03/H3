import { Component, OnInit } from '@angular/core';
import {FamilyTree} from '../../shared/dtos.model';

@Component({
  selector: 'app-tree-list',
  templateUrl: './tree-list.component.html',
  styleUrls: ['./tree-list.component.css']
})
export class TreeListComponent implements OnInit {
  trees: FamilyTree[] = [
    new FamilyTree(1, 'Test Tree', true, new Date()),
    new FamilyTree(2, 'Custom Tree', false, new Date()),
    new FamilyTree(3, 'Another Tree', true, new Date()),
  ];

  constructor() { }

  ngOnInit(): void {
  }

}
