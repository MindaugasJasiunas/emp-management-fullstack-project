import { Component } from '@angular/core';
import { AuthenticationService } from './service/authentication.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [],
})
export class AppComponent {
  title = 'frontend';

  constructor(){}
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
}
