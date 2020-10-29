import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../authentication/auth.service';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit, OnDestroy {
  private userSub: Subscription;

  constructor(private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.userSub = this.authService.user.subscribe(user => {
      if (user) {
        this.router.navigate(['/', user.username]);
      }
    });
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

}
