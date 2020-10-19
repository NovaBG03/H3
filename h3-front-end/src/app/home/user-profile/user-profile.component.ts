import {Component, OnDestroy, OnInit} from '@angular/core';
import {MessageDTO, UserToken} from '../../shared/dtos.model';
import {Subscription} from 'rxjs';
import {AuthService} from '../../authentication/auth.service';
import {HttpClient, HttpEventType} from '@angular/common/http';
import {base64ToFile, ImageCroppedEvent} from 'ngx-image-cropper';
import {Ng2ImgMaxService} from 'ng2-img-max';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit, OnDestroy {
  user: UserToken;
  uploadedImage: any;

  imageChangedEvent: Event;
  private croppedImageBase64: string;
  private selectedFileName: string;
  private userSub: Subscription;

  constructor(private authService: AuthService, private http: HttpClient, private ng2ImgMax: Ng2ImgMaxService,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    this.userSub = this.authService.user.subscribe(user => this.user = user);
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  onfileChange(event: Event): void {
    this.imageChangedEvent = event;
  }

  imageCropped(event: ImageCroppedEvent): void {
    this.croppedImageBase64 = event.base64;
  }

  imageLoaded(): void {
    console.log('Image Loaded');
  }

  cropperReady(): void {
    console.log('Cropper Ready');
  }

  loadImageFailed(): void {
    console.log('Loading Image Failed');
  }

  onFileSelected(event): void {
    const selectedFile: File = event.target.files[0] as File;
    this.selectedFileName = selectedFile.name;
  }

  onUpload(): void {
    const blob = base64ToFile(this.croppedImageBase64);
    const image = this.blobToFile(blob, 'image');

    this.ng2ImgMax.resizeImage(image, 250, 250).subscribe(
      resultBlob => {
        const objectURL = URL.createObjectURL(resultBlob);
        this.uploadedImage = this.sanitizer.bypassSecurityTrustUrl(objectURL);

        const fd = new FormData();
        fd.append('image', resultBlob, this.selectedFileName);

        this.http.post<MessageDTO>('http://localhost:8080/users/profilePicture', fd,
          {
            reportProgress: true,
            observe: 'events'
          })
          .subscribe(event => {
            if (event.type === HttpEventType.UploadProgress) {
              console.log('Uploaded Progress: ' + event.loaded / event.total * 100 + '%');
            } else if (event.type === HttpEventType.Response) {
              console.log(event.body.message);
            }
          });
      },
      error => {
        console.log('😢 Oh no!', error);
      }
    );
  }

  private blobToFile(blob: Blob, fileName: string): File {
    const obj: any = blob;
    obj.lastModifiedDate = new Date();
    obj.name = fileName;

    return obj as File;
  }
}
