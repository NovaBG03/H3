import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../authentication/auth.service';
import {UserToken} from '../shared/dtos.model';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  // isMenuCollapsed = false;
  user: UserToken = null;
  userSubscription: Subscription;

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
     this.userSubscription = this.authService.user.subscribe(userToken => this.user = userToken);
  }

/*  toggleMenu(): void {
    this.isMenuCollapsed = !this.isMenuCollapsed;
  }

  closeMenu(): void {
    this.isMenuCollapsed = true;
  }*/

  onLogout(): void {
    this.authService.logout();
  }

  ngOnDestroy(): void {
    this.userSubscription.unsubscribe();
  }

  isAuthRouteActive(): boolean {
    return this.router.isActive('/auth?type=login', true) || this.router.isActive('/auth?type=register', true);
  }
}
