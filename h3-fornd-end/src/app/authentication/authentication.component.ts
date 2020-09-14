import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, FormGroup} from '@angular/forms';
import {AuthService} from './auth.service';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent implements OnInit {
  isLogin = true;
  authForm: FormGroup;

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService) {
  }

  get state(): string {
    return this.isLogin ? 'Login' : 'Sign Up';
  }

  ngOnInit(): void {
    this.initForm();

    this.route.queryParams
      .subscribe(queryParams => {
        this.authForm.reset();

        if (!queryParams.type) {
          this.changeTypeQueryParam('login');
        }

        if (queryParams.type === 'login') {
          this.isLogin = true;
        } else if (queryParams.type === 'register') {
          this.isLogin = false;
        } else {
          this.changeTypeQueryParam('login');
        }
      });
  }

  authenticate(): void {
    const value = this.authForm.value;
    console.log(value);
    if (this.isLogin) {
      this.authService.login(value.username, value.password);
    } else {
      this.authService.register(value);
    }
  }

  toggleLoginState(): void {
    let type = 'login';
    if (this.isLogin) {
      type = 'register';
    }

    this.changeTypeQueryParam(type);
  }

  private changeTypeQueryParam(type: string): void {
    this.router.navigate([], {queryParams: {type}, queryParamsHandling: 'merge', relativeTo: this.route});
  }

  private initForm(): void {
    this.authForm = new FormGroup({
      username: new FormControl(),
      email: new FormControl(),
      password: new FormControl()
    });
  }
}
