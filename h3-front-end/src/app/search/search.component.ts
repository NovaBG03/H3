import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {TreeService} from '../tree/tree.service';
import OrgChart from '@balkangraph/orgchart.js';
import {FamilyTree} from '../shared/dtos.model';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  searchForm: FormGroup;
  trees: FamilyTree[];

  constructor(private treeService: TreeService) { }

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.searchForm = new FormGroup({
      treePattern: new FormControl()
    });
  }

  search(): void {
    const treePattern = this.searchForm.value.treePattern;

    this.treeService.findTree(treePattern)
      .subscribe(trees => {
        this.trees = trees;
        console.log(this.trees);
      });
  }
}
