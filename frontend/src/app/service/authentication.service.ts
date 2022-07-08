import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpResponse,
} from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';

import { JwtHelperService } from '@auth0/angular-jwt';
import { Role } from '../model/role.model';
import { RoleEnum } from '../enum/role.enum';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  public hostLogin = environment.apiLoginUrl;
  public hostRefreshToken = environment.hostRefreshToken;
  public hostRegister = environment.apiRegisterUrl;
  public hostResetPassword = environment.apiResetPasswordUrl;
  private token: string | null = null;
  private refreshToken: string | null = null;
  private loggedInUsername: string | null = null;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  public login(loginRequest: {
    username: string;
    password: string;
  }): Observable<HttpResponse<User>> {
    return this.http.post<User>(this.hostLogin, loginRequest, {
      observe: 'response',
    });
  }

  public getAccessToken(refreshToken: string): Observable<HttpResponse<any>> {
    this.isLoggedIn();
    return this.http.get<User>(this.hostRefreshToken, {
      observe: 'response',
      headers: new HttpHeaders({'Authorization': refreshToken})
    });
  }

  public register(registerRequest: {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    // profileImageUrl: string;
    dateOfBirth: Date;
  }): Observable<User> {
    return this.http.post<User>(this.hostRegister, registerRequest);
  }

  public logout(): void {
    this.token = null;
    this.loggedInUsername = null;

    // remove items from localStorage - token, user information
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    // localStorage.removeItem('users');
  }

  public saveRefreshToken(refreshToken: string): void {
    this.refreshToken = refreshToken;
    localStorage.setItem('refreshToken', refreshToken);
  }

  public saveAccessToken(token: string): void {
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

  public loadAccessTokenFromLocalCache(): void {
    this.token = localStorage.getItem('token')!;
  }

  public loadRefreshTokenFromLocalCache(): void {
    this.refreshToken = localStorage.getItem('refreshToken')!;
  }

  public getAccessTokenFromLocalCache(): string | null {
    return this.token;
  }

  public getRefreshTokenFromLocalCache(): string | null {
    return this.refreshToken;
  }

  public isLoggedIn(): boolean {
    // if refresh token is valid - user is logged in
    this.loadRefreshTokenFromLocalCache();
    if (this.refreshToken !== null && this.refreshToken !== '') {
      if (this.jwtHelper.decodeToken(this.refreshToken).sub != null || '') {
        if (!this.jwtHelper.isTokenExpired(this.refreshToken)) {
          this.loggedInUsername = this.jwtHelper.decodeToken(this.refreshToken).sub;
          return true;
        }
      }
    }
    this.logout();
    return false;
  }

  private getUserRole(): Role {
    return this.getUserFromLocalCache().roles![0] as Role;
  }

  public checkForRole(role: string) {
    switch (role.toUpperCase()) {
      case 'ADMIN':
        return this.isAdmin;
      case 'MANAGER':
        return this.isManager;
      case 'HR':
        return this.isHR;
      case 'USER':
        return this.isUser;
      default:
        return false;
    }
  }

  private get isAdmin(): boolean{
    return this.getUserRole().roleName === RoleEnum.ADMIN;
  }

  private get isManager(): boolean{
    return this.getUserRole().roleName === RoleEnum.MANAGER || this.isAdmin;
  }

  private get isHR(): boolean{
    return this.getUserRole().roleName === RoleEnum.HR || this.isManager || this.isAdmin;
  }

  private get isUser(): boolean{
    return this.getUserRole().roleName === RoleEnum.USER;
  }

  public isTokenExpired(token: string | undefined){
    return this.jwtHelper.isTokenExpired(token);
  }
}
