import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NotifierService } from 'angular-notifier';
import { Subscription } from 'rxjs';
import { User } from '../model/user.model';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
  form: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),
  });
  private subscriptions: Subscription[] = [];
  private error: string | null = null;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notifier: NotificationService
  ) {}

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigate(['/']);
    } else {
      this.router.navigate(['/login']);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  submit() {
    if (this.form.valid) {
      this.subscriptions.push(
        this.authenticationService
          .login({
            username: this.form.get('username')?.value,
            password: this.form.get('password')?.value,
          })
          .subscribe({
            next: (response: HttpResponse<User>) => {
              console.log(response);
              this.authenticationService.saveToken(
                response.headers.get('authorization')!
              );
              this.authenticationService.addUserToLocalCache(response.body!);
              this.router.navigate(['/']);
            },
            error: (err: HttpErrorResponse) => {
              this.error = err.error.message;
            },
          })
      );
    }
  }

  showError(): string | null {
    if (this.error) {
      return this.error;
    } else if (
      (!this.form.get('username')?.valid &&
        this.form.get('username')?.touched) ||
      (!this.form.get('password')?.valid && this.form.get('password')?.touched)
    ) {
      return 'Username or password invalid';
    }
    return null;
  }
}
