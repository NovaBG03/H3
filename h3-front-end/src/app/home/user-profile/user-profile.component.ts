import {Component, OnDestroy, OnInit} from '@angular/core';
import {ImageDTO, UserToken} from '../../shared/dtos.model';
import {Subscription} from 'rxjs';
import {AuthService} from '../../authentication/auth.service';
import {HttpClient, HttpEventType} from '@angular/common/http';
import {base64ToFile, ImageCroppedEvent} from 'ngx-image-cropper';
import {Ng2ImgMaxService} from 'ng2-img-max';
import {UserService} from '../../shared/user.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit, OnDestroy {
  user: UserToken;
  showingImageUrl: any;
  private userSub: Subscription;

  constructor(private authService: AuthService, private http: HttpClient,
              private userService: UserService) {
  }

  ngOnInit(): void {
    this.userSub = this.authService.user.subscribe(user => this.user = user);
    this.userService.getProfilePictureUrl(this.user.username)
      .subscribe(img => this.showingImageUrl = img);
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
