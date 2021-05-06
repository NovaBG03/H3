import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit} from '@angular/core';
import {User, UserToken} from '../../shared/dtos.model';
import {Subscription} from 'rxjs';
import {AuthService} from '../../authentication/auth.service';
import {UserService} from '../../shared/user.service';
import {ActivatedRoute, Router} from '@angular/router';
import {switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit, OnDestroy, AfterViewInit {
  user: User;
  loggedUser: UserToken;
  showingImageUrl: any;

  private initialBackgroundColor: string;
  private userSub: Subscription;
  private loggedSub: Subscription;

  constructor(private authService: AuthService,
              private userService: UserService,
              private router: Router,
              private route: ActivatedRoute,
              private elementRef: ElementRef) {
  }

  ngOnInit(): void {
    this.userSub = this.route.paramMap.pipe(
      switchMap(params => {
        const username = params.get('username');
        return this.userService.getUserByUsername(username);
      }),
    ).subscribe(user => {
      this.user = user;
      this.userService.getProfilePictureUrl(this.user.username)
        .subscribe(img => this.showingImageUrl = img);
    });

    this.loggedSub = this.authService.user
      .subscribe(user => this.loggedUser = user);
  }

  ngAfterViewInit(): void {
    this.initialBackgroundColor = this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor;
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = '#17141d';
  }

  isOwnProfile(): boolean {
    return this.loggedUser.username === this.user.username;
  }

  openSettings(): void {
    if (this.isOwnProfile()) {
      this.router.navigate(['settings', 'user']);
    }
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
    this.loggedSub.unsubscribe();
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = this.initialBackgroundColor;
  }
}
