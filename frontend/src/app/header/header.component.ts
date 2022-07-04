import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { AuthenticationService } from '../service/authentication.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  isLoggedIn(): boolean {
    return this.authenticationService.isLoggedIn();
  }
  getUsername(): string | null {
    return this.authenticationService.getUserFromLocalCache().username;
  }

  logout() {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
}
