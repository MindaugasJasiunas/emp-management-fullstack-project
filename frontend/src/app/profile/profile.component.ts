import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription, take } from 'rxjs';
import { User } from '../model/user.model';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  user!: User;
  subscriptions: Subscription[] = [];

  constructor(private authService: AuthenticationService, private userService: UserService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.user = this.authService.getUserFromLocalCache();

    this.form = new FormGroup({
      username: new FormControl(null, Validators.required),
      email: new FormControl(null, [Validators.required, Validators.email]),
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
      role: new FormControl(null, Validators.required),
      active: new FormControl(null, Validators.required),
      notLocked: new FormControl(null, Validators.required),
      publicId: new FormControl(),
      file: new FormControl(),
      fileSource: new FormControl()
    });

    // set loaded user values
    this.form.patchValue({
      username: this.user.username,
      email: this.user.email,
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      role: this.user.roles![0].roleName,
      active: this.user.active,
      notLocked: this.user.notLocked,
      publicId: this.user.publicId,
    });
  }

  submit(): void {
    const user: User = this.form.value;
    user.password = 'DEFAULT_PASSWORD';
    this.userService
          .updateUser(user)
          .pipe(take(1))
          .subscribe({
            next: (response: User) => {
              // update image if provided (refactor to external method)
              if(this.form.value.fileSource){
                const formData = new FormData();
                formData.append('email', response.email);
                formData.append('profileImage', this.form.value.fileSource);

                this.subscriptions.push(
                this.userService.updateProfileImage(formData)
                // .pipe(take(1))
                .subscribe({
                  next: (event: HttpEvent<any>) => {
                    // console.log(event);
                    // this.reportUploadProgress(event);
                  },
                  error: (err: HttpErrorResponse) => {
                    console.log(err);
                    this.notificationService.showNotification(
                      'error',
                      'An error occured uploading user profile image. Please try again'
                    );
                  }
                }));
              }else{
                this.notificationService.showNotification(
                  'success',
                  `User ${user.username} successfully updated.`
                );
              }
            },
            error: (err: HttpErrorResponse) => {
              if (err.error.message === undefined) {
                this.notificationService.showNotification(
                  'error',
                  'An error occured. Please try again'
                );
              } else {
                this.notificationService.showNotification(
                  'error',
                  err.error.message
                );
              }
            }
          });
  }

  onFileChange(fileInputEvent: any) {
    // console.log(fileInputEvent.target.files[0]);
    if (fileInputEvent.target.files.length > 0) {
      const file = fileInputEvent.target.files[0];
      this.form.patchValue({
        fileSource: file,
      });
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  // fileUploadStatus: {status: string, percentage: number} = {status: '', percentage: 0};

  // reportUploadProgress(event: HttpEvent<any>) {
  //   switch(event.type){
  //     case HttpEventType.UploadProgress:
  //       this.fileUploadStatus.percentage = Math.round(100 * event.loaded / event.total!);
  //       this.fileUploadStatus.status = 'progress';
  //       break;
  //     case HttpEventType.Response:
  //       if(event.status === 200){
  //         this.user.profileImageUrl = `${event.body.profileImageUrl}`;
  //         this.notificationService.showNotification('success', `${event.body.firstName}'s profile image successfully updated.`);
  //       }
  //       this.fileUploadStatus.percentage = 100;
  //       this.fileUploadStatus.status = 'done';
  //       break;
  //     default:
  //         this.notificationService.showNotification('error', `Unable to update profile picture.`);
  //   }
  // }

}


