import {Injectable, OnInit} from '@angular/core';
import {AuthService} from '../authentication/auth.service';
import {HttpClient, HttpEvent, HttpEventType} from '@angular/common/http';
import {ImageDTO, MessageDTO, User, UserResponseDTO} from './dtos.model';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {environment} from '../../environments/environment';

@Injectable({ providedIn: 'root'})
export class UserService {
  constructor(private authService: AuthService, private http: HttpClient) {
  }

  uploadProfilePicture(blob: Blob): Observable<string> {
    const fd = new FormData();
    fd.append('image', blob);

    return this.http.post<MessageDTO>(environment.domain + '/users/profilePicture', fd)
      .pipe(map(messageDTO => messageDTO.message));
  }

  getProfilePictureUrl(username: string): Observable<string> {
    return this.http.get<ImageDTO>(environment.domain + '/users/profilePicture/' + username)
      .pipe(map(imageDTO => {
        if (!imageDTO.imageBytes) {
          return 'assets/img/default-profile-pic.png';
        }
        return 'data:image/jpeg;base64,' + imageDTO.imageBytes;
      }));
  }

  getUserByUsername(username: string): Observable<User> {
    return this.http.get<UserResponseDTO>(environment.domain + '/users/' + username)
      .pipe(map(userResponseDTO => {
        return new User(userResponseDTO.id, userResponseDTO.username, userResponseDTO.email);
      }));
  }
}
