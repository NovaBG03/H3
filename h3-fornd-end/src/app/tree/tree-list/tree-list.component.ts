import {Component, OnDestroy, OnInit} from '@angular/core';
import {FamilyTree} from '../../shared/dtos.model';
import {TreeService} from '../tree.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-tree-list',
  templateUrl: './tree-list.component.html',
  styleUrls: ['./tree-list.component.css']
})
export class TreeListComponent implements OnInit, OnDestroy {
  trees: FamilyTree[];
  newTreeSub: Subscription;

  constructor(private treeService: TreeService) {
  }

  ngOnInit(): void {
    this.treeService.getOwnTrees().subscribe(familyTrees => {
      this.trees = familyTrees;
    });

    this.newTreeSub = this.treeService.createdNewTree.subscribe(familyTree => {
      this.trees.push(familyTree);
    });
  }

  ngOnDestroy(): void {
    this.newTreeSub.unsubscribe();
  }

}
