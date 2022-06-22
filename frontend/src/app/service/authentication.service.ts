import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private host = environment.apiUrl;
  private hostLogin = environment.apiLoginUrl;
  private hostRegister = environment.apiRegisterUrl;

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
}
