import {Component, Input, OnInit} from '@angular/core';
import {FamilyTree} from '../../../shared/dtos.model';

@Component({
  selector: 'app-tree-item',
  templateUrl: './tree-item.component.html',
  styleUrls: ['./tree-item.component.css']
})
export class TreeItemComponent implements OnInit {
  @Input() tree: FamilyTree;

  constructor() { }

  ngOnInit(): void {
    console.log(this.tree);
  }

}
