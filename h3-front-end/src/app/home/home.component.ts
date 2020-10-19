import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../authentication/auth.service';
import {UserToken} from '../shared/dtos.model';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  user: UserToken;
  private userSub: Subscription;

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.userSub = this.authService.user.subscribe(user => this.user = user);
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

}
