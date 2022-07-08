import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {
  Observable,
  take,
  tap,
  switchMap,
  throwError
} from 'rxjs';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthenticationService, private router: Router) {}
  // runs before request leaves
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (
      request.url.includes(this.authService.hostLogin) ||
      request.url.includes(this.authService.hostRefreshToken) ||
      request.url.includes(this.authService.hostRegister) ||
      request.url.includes(this.authService.hostResetPassword)
    ) {
      // pass request through
      return next.handle(request);
    }

    this.authService.loadAccessTokenFromLocalCache();
    let jwtToken = this.authService.getAccessTokenFromLocalCache();
    let modifiedRequest = request.clone({setHeaders: { Authorization: `${jwtToken}` }});

    if (this.authService.isTokenExpired(jwtToken?.substring(7))) {
      // handle expired JWT before sending request
      this.authService.loadRefreshTokenFromLocalCache();
      const refreshToken = this.authService.getRefreshTokenFromLocalCache();
      if (!refreshToken) {
        // no refresh token - logout & pass through
        this.authService.logout();
        this.router.navigate(['/login'])
        // return next.handle(request);
        throw throwError(()=> new Error("no refresh token")); // dont send request
      }


      return this.authService.getAccessToken(refreshToken)
      .pipe(
        take(1),
        tap((response: HttpResponse<any>) => {
          this.authService.saveAccessToken(response.headers.get('authorization')!);
        }),
        switchMap((response: HttpResponse<any>) => {  // transformation operator that maps to an Observable<T>
          // return request with updated access token
          const newRequest = request.clone({
            setHeaders: {
              Authorization: response.headers.get('authorization')!
            }
          });
          return next.handle(newRequest);
        })
      );
    }

    // pass request through
    return next.handle(modifiedRequest);

    // handle expired JWT from response (bad way - user must retry request)
    /*
    return next.handle(modifiedRequest).pipe(
      catchError(error => {
        // if response is error
        if (
          error instanceof HttpErrorResponse &&
          error.status === 403 &&
          error.error &&
          error.error.message === environment.expiredJWTMessage
        ) {
          // update access token
          this.handleExpiredAccessJWT(request, next);
        }
        // pass request through
        return next.handle(modifiedRequest);
      })
    );
    */
  }
}
