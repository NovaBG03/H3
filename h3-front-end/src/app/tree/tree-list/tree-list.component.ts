import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit} from '@angular/core';
import {FamilyTree} from '../../shared/dtos.model';
import {TreeService} from '../tree.service';
import {Subscription} from 'rxjs';
import {AuthService} from '../../authentication/auth.service';
import {UserService} from '../../shared/user.service';

@Component({
  selector: 'app-tree-list',
  templateUrl: './tree-list.component.html',
  styleUrls: ['./tree-list.component.css']
})
export class TreeListComponent implements OnInit, OnDestroy, AfterViewInit {
  trees: FamilyTree[];
  username: string;
  profilePictureUrl: string;

  private initialBackgroundColor: string;
  private userSub: Subscription;
  private newTreeSub: Subscription;

  constructor(private treeService: TreeService, private authService: AuthService, private elementRef: ElementRef,
              private userService: UserService) {
  }

  ngOnInit(): void {
    this.userSub = this.authService.user.subscribe(user => {
      this.username = user.username;
      this.userService.getProfilePictureUrl(this.username)
        .subscribe(img => this.profilePictureUrl = img);
    });

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
