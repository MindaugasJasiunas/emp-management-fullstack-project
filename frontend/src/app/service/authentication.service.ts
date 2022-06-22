import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';

import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private hostLogin = environment.apiLoginUrl;
  private hostRegister = environment.apiRegisterUrl;
  private token: string | null = null;
  private loggedInUsername: string | null = null;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  public login(loginRequest: {
    username: string;
    password: string;
  }): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http.post<any>(this.hostLogin, loginRequest, {
      observe: 'response',
    });
  }

  public register(registerRequest: {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    profileImageUrl: string;
    dateOfBirth: Date;
  }): Observable<User | HttpErrorResponse> {
    return this.http.post<User | HttpErrorResponse>(
      this.hostRegister,
      registerRequest
    );
  }

  public logout(): void {
    this.token = null;
    this.loggedInUsername = null;

    // remove items from localStorage - token, user information
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  public addUserToLocalCache(user: User): void {
    this.loggedInUsername = user.username;
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user')!);
  }

  public loadTokenFromLocalCache(): void {
    this.token = localStorage.getItem('token')!;
  }

  public getTokenFromLocalCache(): string | null {
    return this.token;
  }

  public isLoggedIn(): boolean {
    this.loadTokenFromLocalCache();
    if (this.token != null && this.token !== '') {
      if (this.jwtHelper.decodeToken(this.token).sub != null || '') {
        if (!this.jwtHelper.isTokenExpired(this.token)) {
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        }
      }
    }
    this.logout();
    return false;
  }
}
