import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit} from '@angular/core';
import {FamilyTree} from '../../shared/dtos.model';
import {TreeService} from '../tree.service';
import {Subscription} from 'rxjs';
import {AuthService} from '../../authentication/auth.service';
import {UserService} from '../../shared/user.service';
import {ActivatedRoute} from '@angular/router';
import {switchMap, tap} from 'rxjs/operators';


@Component({
  selector: 'app-tree-list',
  templateUrl: './tree-list.component.html',
  styleUrls: ['./tree-list.component.css']
})
export class TreeListComponent implements OnInit, OnDestroy, AfterViewInit {
  trees: FamilyTree[];
  username: string;
  isMyTrees = false;
  profilePictures: any = {};

  private initialBackgroundColor: string;
  private treesSub: Subscription;
  private newTreeSub: Subscription;

  constructor(private treeService: TreeService,
              private authService: AuthService,
              private userService: UserService,
              private route: ActivatedRoute,
              private elementRef: ElementRef) {
  }

  ngOnInit(): void {
    this.treesSub = this.route.parent.url.pipe(
      switchMap(parentUrlSegment => {
        const value = parentUrlSegment[0].path;
        if (value === 'trees') {
          this.isMyTrees = true;
          return this.treeService.getOwnTrees().pipe(
            tap(trees => this.newTreeSub = this.treeService.createdNewTree
              .subscribe(familyTree => trees.push(familyTree)))
          );
        } else if (value === 'search') {
          this.isMyTrees = false;
          return this.route.url.pipe(
            switchMap(urlSegment => {
              let treePattern = '';
              if (urlSegment.length) {
                treePattern = urlSegment[0].path;
              }
              return this.treeService.findTree(treePattern);
            })
          );
        }
      }),
      tap(trees => [...new Set(trees.map(tree => tree.owner))]
        .forEach(owner => this.userService.getProfilePictureUrl(owner)
          .subscribe(img => this.profilePictures[owner] = img))
      )
    ).subscribe(trees => this.trees = trees);
  }

  ngAfterViewInit(): void {
    this.initialBackgroundColor = this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor;
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = '#17141d';
  }

  ngOnDestroy(): void {
    this.treesSub.unsubscribe();
    this.newTreeSub?.unsubscribe();
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = this.initialBackgroundColor;
  }
}
