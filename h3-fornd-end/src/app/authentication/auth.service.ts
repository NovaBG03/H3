import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UserDataDTO} from '../shared/dtos.model';

@Injectable({providedIn: 'root'})
export class AuthService {

  constructor(private http: HttpClient) {
  }

  login(username: string, password: string): any {
    this.http.post('http://localhost:8080/users/signIn', null, {
      params: new HttpParams()
        .append('username', username)
        .append('password', password)
    }).subscribe(res => console.log(res));
  }

  register(userDataDTO: UserDataDTO): void {
    this.http.post('http://localhost:8080/users/signUp', userDataDTO)
      .subscribe(res => console.log(res));
  }
}
