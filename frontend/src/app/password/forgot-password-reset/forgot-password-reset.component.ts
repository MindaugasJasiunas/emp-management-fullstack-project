import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { take } from 'rxjs';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-forgot-password-reset',
  templateUrl: './forgot-password-reset.component.html',
  styleUrls: ['./forgot-password-reset.component.css'],
})
export class ForgotPasswordResetComponent implements OnInit {
  @ViewChild('f', { static: true }) form!: NgForm;

  ngOnInit(): void {}

  private error: string | null = null;

  constructor(
    private userService: UserService,
    private notifier: NotificationService
  ) {}

  submit() {
    if (this.form.valid) {
      this.userService
        .resetPassword({email:this.form.value.email})
        .pipe(take(1))
        .subscribe({
          next: (response: void) => {
            this.notifier.showNotification('info', 'Check your email to reset password.');
          },
          error: (err: HttpErrorResponse) => {
            this.error = err.error.message;
          },
        });
    }
  }

  showError(): string | null {
    if (this.error) {
      return this.error;
    } else if (this.form.touched && this.form.invalid) {
      return 'Please enter a valid email';
    }
    return null;
  }
}
