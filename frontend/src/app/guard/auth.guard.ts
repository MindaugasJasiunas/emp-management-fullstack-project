import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { AuthenticationService } from "../service/authentication.service";
import { NotificationService } from "../service/notification.service";

@Injectable()
export class AuthGuard implements CanActivate {

    constructor(private authService: AuthenticationService, private router: Router, private notificationService: NotificationService){}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable< boolean | UrlTree> | Promise< boolean | UrlTree> {
      if(this.authService.isLoggedIn()){
        return true;
      }
      this.notificationService.showNotification("error", "You need to login to access this page!");
      this.router.navigate(['/login']);
      return false;
    }

}
