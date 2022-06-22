import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { User } from './model/user.model';
import { AuthenticationService } from './service/authentication.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [],
})
export class AppComponent {
  title = 'frontend';

  constructor() {}

  /*constructor(private authService: AuthenticationService) {
    authService.login({ username: 'johnd', password: 'password' }).subscribe({
      next: (data) => {
        console.log(data);
        console.log(data.headers.get('Authorization'));
      },
      error: (err: Error) => {
        console.log(err);
        console.error(err.message);
      },
    });
  }*/

  /*constructor(private authService: AuthenticationService) {
    authService
      .register({
        username: 'temp',
        email: 'temp@example.com',
        password: 'password',
        firstName: 'Temp',
        lastName: 'Temporary',
        profileImageUrl: 'http://temp.com',
        dateOfBirth: new Date(),
      })
      .subscribe({
        next: (data: User | HttpErrorResponse) => {
          console.log(data as User);
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
          console.log(err.error.message);
        },
      });
  }*/
}
