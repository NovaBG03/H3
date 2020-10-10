import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AuthService} from './auth.service';
import {Observable} from 'rxjs';
import {UserData, UserToken} from '../shared/dtos.model';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent implements OnInit {
  isLogin = true;
  authForm: FormGroup;
  error: string = null;

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService) {
  }

  get state(): string {
    return this.isLogin ? 'Login' : 'Sign Up';
  }

  ngOnInit(): void {
    this.route.queryParams
      .subscribe(queryParams => {
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
        this.resetForm();
      });
  }

  authenticate(): void {
    if (this.authForm.invalid) {
      return;
    }

    const value = this.authForm.value;
    console.log(value);

    let authObservable: Observable<UserToken>;
    if (this.isLogin) {
      authObservable = this.authService.login(value.username, value.passwordGroup.password);
    } else {
      authObservable = this.authService.register(
        new UserData(value.username, value.email, value.passwordGroup.password));
    }

    authObservable.subscribe(res => {
        console.log(res);
        this.router.navigate(['home']);
      },
      errorMessage => this.error = errorMessage);
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
    const emailValidators = [];
    const passwordGroupValidators = [];
    if (!this.isLogin) {
      emailValidators.push(
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(225),
        Validators.pattern(/(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/)
      );
      passwordGroupValidators.push(this.passwordConfirmedValidator);
    }

    this.authForm = new FormGroup({
      username: new FormControl(null, [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(225)]),
      email: new FormControl(null, emailValidators),
      passwordGroup: new FormGroup({
        password: new FormControl(null, [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(225)]),
        confirmPassword: new FormControl()
      }, passwordGroupValidators)
    });
  }

  private resetForm(): void {
    this.initForm();
    this.error = null;
  }

  passwordConfirmedValidator(formGroup: FormGroup): any {
    const password = formGroup.get('password').value;
    const confirmPassword = formGroup.get('confirmPassword').value;

    return password === confirmPassword ? null : { notSame: true };
  }
}
