import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private host = environment.apiUrl;
  private hostLogin = environment.apiLoginUrl;

  constructor(private http: HttpClient) {}

  public login(loginRequest: {
    username: string;
    password: string;
  }): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http.post<any>(this.hostLogin, loginRequest, {
      observe: 'response',
    });
  }
}
