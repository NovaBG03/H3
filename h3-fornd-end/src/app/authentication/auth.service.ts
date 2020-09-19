import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {UserData, UserToken, UserTokenDTO} from '../shared/dtos.model';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {Router} from '@angular/router';

@Injectable({providedIn: 'root'})
export class AuthService {
  user = new BehaviorSubject<UserToken>(null);
  private logOutTimeout: any = null;

  constructor(private http: HttpClient, private router: Router) {
  }

  login(username: string, password: string): Observable<UserToken> {
    return this.http.post<UserTokenDTO>('http://localhost:8080/users/signIn', null, {
      params: new HttpParams()
        .append('username', username)
        .append('password', password)
    }).pipe(catchError(this.handleError),
      map(userTokenDTO => this.userTokenDTOToUserToken(userTokenDTO)),
      tap(userToken => this.handleAuthentication(userToken)));
  }

  autoLogin(): void {
    const userTokenDTO: UserTokenDTO = JSON.parse(localStorage.getItem('userToken'));

    if (!userTokenDTO) {
      return;
    }

    const userToken = this.userTokenDTOToUserToken(userTokenDTO);
    if (!userToken.isExpired) {
      this.user.next(userToken);
      this.autoLogout(userToken.timeTillExpiration);
    }
  }

  logout(): void {
    this.user.next(null);
    localStorage.removeItem('userToken');
    this.router.navigate(['/']);
    if (this.logOutTimeout) {
      clearTimeout(this.logOutTimeout);
      this.logOutTimeout = null;
    }
  }

  autoLogout(timeDuration: number): void {
    console.log(timeDuration);
    this.logOutTimeout = setTimeout(() => {
      this.logout();
    }, timeDuration);
  }

  register(userData: UserData): Observable<UserToken> {
    return this.http.post<UserTokenDTO>('http://localhost:8080/users/signUp', userData)
      .pipe(catchError(this.handleError),
        map(userTokenDTO => this.userTokenDTOToUserToken(userTokenDTO)),
        tap(userToken => this.handleAuthentication(userToken)));
  }

  private handleError(errorRes: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred!';
    if (!errorRes.error || !errorRes.error.message) {
      return throwError(errorMessage);
    }

    errorMessage = errorRes.error.message;
    // TODO don't display error messages from the server
    return throwError(errorMessage);
  }

  private handleAuthentication(userToken: UserToken): void {
    this.user.next(userToken);

    this.autoLogout(userToken.timeTillExpiration);
    localStorage.setItem('userToken', JSON.stringify(userToken));
  }

  private userTokenDTOToUserToken(userTokenDTO: UserTokenDTO): UserToken {
    return new UserToken(userTokenDTO.token,
      new Date(userTokenDTO.expiresIn),
      +userTokenDTO.id,
      userTokenDTO.username,
      userTokenDTO.email,
      userTokenDTO.roles);
  }
}
