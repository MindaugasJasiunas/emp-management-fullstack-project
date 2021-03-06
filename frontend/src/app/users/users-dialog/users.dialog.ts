import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RoleEnum } from 'src/app/enum/role.enum';
import { Role } from 'src/app/model/role.model';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/service/authentication.service';

@Component({
  selector: 'users-dialog',
  templateUrl: 'users.dialog.html',
})
export class UsersDialog implements OnInit {
  form!: FormGroup;
  roles: Role[] = [new Role(0, RoleEnum.USER), new Role(0, RoleEnum.MANAGER), new Role(0, RoleEnum.HR), new Role(0, RoleEnum.ADMIN)];

  constructor(
    public dialogRef: MatDialogRef<UsersDialog>,
    @Inject(MAT_DIALOG_DATA) public data: User,
    private authService: AuthenticationService
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
      // password: new FormControl(null, [Validators.required, Validators.minLength(8)]),
      password: new FormControl(null, this.data.publicId ? [Validators.nullValidator] :  [Validators.required, Validators.minLength(8)]),
      active: new FormControl(),
      notLocked: new FormControl(),
      dob: new FormControl(null, Validators.required),
      file: new FormControl(),
      fileSource: new FormControl(),
      role: new FormControl(),
    });

    // set loaded user values
    this.form.patchValue({
      username: this.data.username,
      email: this.data.email,
      firstName: this.data.firstName,
      lastName: this.data.lastName,
      active: this.data.active,
      notLocked: this.data.notLocked,
      dob: this.data.dateOfBirth,
      role: this.data.roles? this.data.roles![0].roleName : RoleEnum.USER
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

  checkForRole(role: string): boolean{
    return this.authService.checkForRole(role);
  }

}
