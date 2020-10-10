import {Injectable} from '@angular/core';
import {AuthService} from './auth.service';
import {HttpEvent, HttpHandler, HttpHeaders, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {exhaustMap, take} from 'rxjs/operators';

@Injectable()
export class AuthInterceptorService {
  constructor(private authService: AuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.authService.user.pipe(
      take(1),
      exhaustMap(user => {
        if (!user) {
          return next.handle(req);
        }
        const modifiedReq = req.clone({
          headers: new HttpHeaders({
            Authorization: 'Bearer ' + user.token
          })
        });
        return next.handle(modifiedReq);
      })
    );
  }
}
