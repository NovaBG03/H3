import {Component, OnDestroy, OnInit} from '@angular/core';
import {map, switchMap} from 'rxjs/operators';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {TreeService} from '../tree.service';
import {AuthService} from '../../authentication/auth.service';

@Component({
  selector: 'app-tree-view',
  templateUrl: './tree-view.component.html',
  styleUrls: ['./tree-view.component.css']
})
export class TreeViewComponent implements OnInit, OnDestroy {
  isOwner: boolean;
  private pages = ['graph', 'facts', 'settings'];
  private userSub: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private treeService: TreeService,
              private authService: AuthService) {
  }

  get currentPage(): string {
    const urlElements = this.router.url.split('/');
    return urlElements[urlElements.length - 1];
  }

  get nextPage(): string {
    let nextPageIndex = this.pages.indexOf(this.currentPage) + 1;
    if (nextPageIndex >= this.pages.length) {
      nextPageIndex = 0;
    }

    return this.pages[nextPageIndex];
  }

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

  displayPage(page: string): void {
    this.router.navigate([page], {relativeTo: this.route});
  }
}
