import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit} from '@angular/core';
import {FamilyTree, UserToken} from '../../shared/dtos.model';
import {TreeService} from '../tree.service';
import {Subscription} from 'rxjs';
import {AuthService} from '../../authentication/auth.service';

@Component({
  selector: 'app-tree-list',
  templateUrl: './tree-list.component.html',
  styleUrls: ['./tree-list.component.css']
})
export class TreeListComponent implements OnInit, OnDestroy, AfterViewInit {
  trees: FamilyTree[];
  newTreeSub: Subscription;
  initialBackgroundColor: string;
  username: string;
  userSub: Subscription;

  constructor(private treeService: TreeService, private authService: AuthService, private elementRef: ElementRef) {
  }

  ngOnInit(): void {
    this.userSub = this.authService.user.subscribe(user => this.username = user.username);

    this.treeService.getOwnTrees().subscribe(familyTrees => {
      this.trees = familyTrees;
    });

    this.newTreeSub = this.treeService.createdNewTree.subscribe(familyTree => {
      this.trees.push(familyTree);
    });
  }

  ngAfterViewInit(): void {
    this.initialBackgroundColor = this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor;
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = '#17141d';
  }

  ngOnDestroy(): void {
    this.newTreeSub.unsubscribe();
    this.userSub.unsubscribe();
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = this.initialBackgroundColor;
  }
}
