import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../authentication/auth.service';
import {User} from '../../shared/dtos.model';
import {UserService} from '../../shared/user.service';

@Component({
  selector: 'app-user-profile-settings',
  templateUrl: './user-profile-settings.component.html',
  styleUrls: ['./user-profile-settings.component.css']
})
export class UserProfileSettingsComponent implements OnInit, OnDestroy {
  user: User;
  showingImageUrl = null;
  isChoosingPicture = false;

  private userSub;

  constructor(private authService: AuthService, private userService: UserService) {
  }

  ngOnInit(): void {
    this.userSub = this.authService.user
      .subscribe(user => {
        this.user = user;
        this.userService.getProfilePictureUrl(this.user.username)
          .subscribe(img => this.showingImageUrl = img);
      });
  }

  updateProfilePic(blob: Blob): void {
    if (blob) {
      this.userService.uploadProfilePicture(blob)
        .subscribe(message => {
          console.log(message);
          this.userService.getProfilePictureUrl(this.user.username)
            .subscribe(img => {
              this.showingImageUrl = img;
            });
        });
    }
    this.isChoosingPicture = false;
  }

  changeProfilePicture(): void {
    this.isChoosingPicture = true;
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }
}
