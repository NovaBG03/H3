import {Component, OnInit} from '@angular/core';
import {AuthService} from './authentication/auth.service';
import {UserService} from './shared/user.service';
import {flatMap} from 'rxjs/operators';
import {interval} from 'rxjs';
import {environment} from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  isServerOn = false;

  constructor(private authService: AuthService, private userService: UserService) {
  }

  ngOnInit(): void {
    this.authService.autoLogin();

    this.userService.isServerUp()
      .subscribe(isUp => {
        if (!environment.production) {
          console.log(isUp ? 'Connected!' : 'Connecting to the server...');
        }
        if (!isUp) {
          this.startCheckingServerStatus();
        }
        this.isServerOn = isUp;
      });
  }

  private startCheckingServerStatus(): void {
    const intervalSub = interval(10 * 1000)
      .pipe(
        flatMap(() => this.userService.isServerUp())
      )
      .subscribe(isUp => {
        if (!environment.production) {
          console.log(isUp ? 'Connected!' : 'Connecting to the server...');
        }
        if (isUp) {
          intervalSub.unsubscribe();
          this.isServerOn = isUp;
          location.reload();
        }
      });
  }
}
