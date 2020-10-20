import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {base64ToFile, ImageCroppedEvent} from 'ngx-image-cropper';
import {Ng2ImgMaxService} from 'ng2-img-max';

@Component({
  selector: 'app-image-uploader',
  templateUrl: './image-uploader.component.html',
  styleUrls: ['./image-uploader.component.css']
})
export class ImageUploaderComponent implements OnInit {
  imageChangedEvent: Event;
  croppedImageBase64: string;
  @Output() imageUploaded = new EventEmitter<Blob>();

  constructor(private ng2ImgMax: Ng2ImgMaxService) { }

  ngOnInit(): void {
  }

  onfileChange(event: Event): void {
    this.imageChangedEvent = event;
  }

  imageCropped(event: ImageCroppedEvent): void {
    this.croppedImageBase64 = event.base64;
  }

  imageLoaded(): void {
    // console.log('Image Loaded');
  }

  cropperReady(): void {
    // console.log('Cropper Ready');
  }

  loadImageFailed(): void {
    console.log('Loading Image Failed');
  }

  onUpload(): void {
    const blob = base64ToFile(this.croppedImageBase64);
    const image = this.blobToFile(blob, 'image');

    this.ng2ImgMax.resizeImage(image, 200, 200).subscribe(
      resultBlob => {
        this.imageUploaded.emit(resultBlob);
        this.imageChangedEvent = null;
      },
      error => {
        console.log('Image Resizing Error!', error);
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
