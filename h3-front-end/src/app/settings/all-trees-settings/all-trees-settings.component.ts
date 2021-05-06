import {Component, OnInit} from '@angular/core';
import {TreeService} from '../../tree/tree.service';
import {FamilyTree} from '../../shared/dtos.model';
import {ActivatedRoute, Router} from '@angular/router';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-all-trees-settings',
  templateUrl: './all-trees-settings.component.html',
  styleUrls: ['./all-trees-settings.component.css']
})
export class AllTreesSettingsComponent implements OnInit {
  familyTrees: FamilyTree[];
  selectedId: number;

  constructor(private treeService: TreeService, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.treeService.getOwnTrees()
      .subscribe(familyTrees => {
        this.familyTrees = familyTrees;

        if (this.route.firstChild) {
          this.route.firstChild.params.pipe(
            map(params => +params.id),
          ).subscribe(
            id => {
              if (id && this.familyTrees.find(tree => tree.id === id)) {
                this.selectedId = id;
                return;
              }
              this.router.navigate(['settings', 'trees']);
            },
            error => this.router.navigate(['settings', 'trees'])
          );
        }
      });
  }

  onChange(event: any): void {
    this.selectedId = +event.target.value;
    if (!this.selectedId) {
      this.router.navigate(['settings', 'trees']);
      return;
    }

    this.router.navigate(['settings', 'trees', this.selectedId]);
  }
}
