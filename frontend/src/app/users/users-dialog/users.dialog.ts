import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { User } from 'src/app/model/user.model';

@Component({
  selector: 'users-dialog',
  templateUrl: 'users.dialog.html',
})
export class UsersDialog implements OnInit {
  form!: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<UsersDialog>,
    @Inject(MAT_DIALOG_DATA) public data: User
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    // initialize a form before rendering
    this.form = new FormGroup({
      // controls (key-value pairs)
      username: new FormControl(null, Validators.required),
      email: new FormControl(null, [Validators.required, Validators.email]),
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
      password: new FormControl(null, [
        Validators.required,
        Validators.minLength(8),
      ]),
      active: new FormControl(),
      notLocked: new FormControl(),
      file: new FormControl(),
      fileSource: new FormControl(),
    });

    // set loaded user values
    this.form.patchValue({
      username: this.data.username,
      email: this.data.email,
      firstName: this.data.firstName,
      lastName: this.data.lastName,
      active: this.data.active,
      notLocked: this.data.notLocked
    });
  }

  getErrorMessage(field: string): string {
    // if (this.form.get('email')!.hasError('required')) {
    //   return 'You must enter a value';
    // }
    switch (field) {
      case 'username':
        return 'Please enter valid username';
      case 'email':
        // if (this.form.get('email')?.invalid) {
        return 'Please enter valid email';
      // }
      case 'firstName':
        return 'Please enter valid first name';
      case 'lastName':
        return 'Please enter valid last name';
      case 'password':
        return 'Please enter valid password';
      default:
        return '';
    }
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

}
