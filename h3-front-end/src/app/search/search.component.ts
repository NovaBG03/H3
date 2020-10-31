import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  searchForm: FormGroup;

  constructor(private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  search(): void {
    let treePattern = this.searchForm.value.treePattern;

    if (!treePattern) {
      treePattern = '';
    }

    this.router.navigate(['trees', 'search', treePattern]);

    /*this.treeService.findTree(treePattern)
      .subscribe(trees => {
        this.trees = trees;
        console.log(this.trees);
      });*/
  }

  private initForm(): void {
    this.searchForm = new FormGroup({
      treePattern: new FormControl()
    });
  }
}
