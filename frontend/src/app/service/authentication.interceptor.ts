import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthenticationService) {}
  // runs before request leaves
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (
      request.url.includes(this.authService.hostLogin) ||
      request.url.includes(this.authService.hostRegister) ||
      request.url.includes(this.authService.hostResetPassword)
    ) {
      // pass request through
      return next.handle(request);
    }
    this.authService.loadTokenFromLocalCache();

    const jwtToken = this.authService.getTokenFromLocalCache();
    const modifiedRequest = request.clone({
      setHeaders: { Authorization: `Bearer ${jwtToken}` },
    });

    // continue with modified request
    return next.handle(modifiedRequest);
  }
}
