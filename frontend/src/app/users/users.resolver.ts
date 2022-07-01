import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Resolve,
  RouterStateSnapshot,
} from '@angular/router';
import { catchError, Observable, of, take } from 'rxjs';
import { User } from '../model/user.model';
import { UserService } from '../service/user.service';

// @Injectable({ providedIn: 'root' })
export class UserResolver implements Resolve<User> {
  constructor(private userService: UserService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): User | Observable<User> | Promise<User> {
    return this.userService.getUsers().pipe(
      catchError(() => {
        return of(); // return EMPTY; (import EMPTY from rxjs)
      }),
      take(1) // for observable to eventually complete (for routing transition in page(if any) to work)
    );
  }
}
