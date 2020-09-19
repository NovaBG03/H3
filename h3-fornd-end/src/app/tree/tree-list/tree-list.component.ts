import { Component, OnInit } from '@angular/core';
import {FamilyTree} from '../../shared/dtos.model';
import {TreeService} from '../tree.service';

@Component({
  selector: 'app-tree-list',
  templateUrl: './tree-list.component.html',
  styleUrls: ['./tree-list.component.css']
})
export class TreeListComponent implements OnInit {
  trees: FamilyTree[];

  constructor(private treeService: TreeService) { }

  ngOnInit(): void {
    this.treeService.getOwnTrees().subscribe(familyTrees => {
      this.trees = familyTrees;
    });
  }

}
