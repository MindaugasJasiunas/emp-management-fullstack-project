import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { User } from '../model/user.model';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit {
  form: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),
    email: new FormControl('', [Validators.required, Validators.email]),
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    dateOfBirth: new FormControl('', [Validators.required]),
  });
  private error: string | null = null;
  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notifier: NotificationService
  ) {}

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  submit() {
    if (this.form.valid) {
      this.subscriptions.push(
        this.authenticationService
          .register({
            username: this.form.get('username')?.value,
            email: this.form.get('email')?.value,
            password: this.form.get('password')?.value,
            firstName: this.form.get('firstName')?.value,
            lastName: this.form.get('lastName')?.value,
            dateOfBirth: this.form.get('dateOfBirth')?.value,
          })
          .subscribe({
            next: (response: User) => {
              console.log(response);
              this.router.navigate(['/login']);
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
    }
    return null;
  }
}
