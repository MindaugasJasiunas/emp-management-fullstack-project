import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-forgot-password-form',
  templateUrl: './forgot-password-form.component.html',
  styleUrls: ['./forgot-password-form.component.css'],
})
export class ForgotPasswordFormComponent implements OnInit {
  @ViewChild('f', { static: true }) form!: NgForm;

  ngOnInit(): void {}

  private error: string | null = null;

  constructor(
    private userService: UserService,
    private notifier: NotificationService,
    private route: ActivatedRoute
  ) {}

  submit() {
    this.route.queryParams.pipe(take(1)).subscribe(params => {
      const code = params['code'];

      if(!code) return;

      if (this.form.valid) {
        if (this.form.value.password !== this.form.value.passwordRepeat) {
          this.error = 'Passwords do not match!';
        } else {
          this.userService
            .restorePassword({
              newPassword: this.form.value.password,
              newPasswordRepeated: this.form.value.passwordRepeat,
            }, code)
            .pipe(take(1))
            .subscribe({
              next: (response) => {
                this.notifier.showNotification(
                  'success',
                  'Password was updated. Now you can login.'
                );
              },
              error: (err: HttpErrorResponse) => {
                this.error = err.error.message;
              },
            });
        }
      }
    })
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
