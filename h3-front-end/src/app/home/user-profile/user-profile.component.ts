import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from '../../shared/dtos.model';
import {Subscription} from 'rxjs';
import {AuthService} from '../../authentication/auth.service';
import {UserService} from '../../shared/user.service';
import {ActivatedRoute} from '@angular/router';
import {switchMap, tap} from 'rxjs/operators';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit, OnDestroy {
  user: User;
  showingImageUrl: any;
  private userSub: Subscription;

  constructor(private authService: AuthService,
              private userService: UserService,
              private route: ActivatedRoute) {
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
  }

  updateProfilePic(blob: Blob): void {
    this.userService.uploadProfilePicture(blob)
      .subscribe(message => {
        console.log(message);
        this.userService.getProfilePictureUrl(this.user.username)
          .subscribe(img => {
            this.showingImageUrl = img;
          });
      });
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }
}
