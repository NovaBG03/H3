import {Component, OnDestroy, OnInit} from '@angular/core';
import {map, switchMap, tap} from 'rxjs/operators';
import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {TreeService} from '../tree.service';
import {AuthService} from '../../authentication/auth.service';

@Component({
  selector: 'app-tree-view',
  templateUrl: './tree-view.component.html',
  styleUrls: ['./tree-view.component.css']
})
export class TreeViewComponent implements OnInit, OnDestroy {
  isOwner: boolean;
  private userSub: Subscription;

  constructor(private route: ActivatedRoute,
              private treeService: TreeService,
              private authService: AuthService) { }

  ngOnInit(): void {
    this.userSub = this.route.url.pipe(
      map(urlSegment => +urlSegment[0]),
      switchMap(treeId => {
        return this.treeService.getTree(treeId).pipe(
          switchMap(tree => {
            return this.authService.user.pipe(
              map(user => user.username === tree.owner)
            );
          }));
      })
    ).subscribe(isOwner => {
      this.isOwner = isOwner;
    });
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

}
