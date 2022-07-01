import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guard/auth.guard';
import { LoginComponent } from './login/login.component';
import { ProfileComponent } from './profile/profile.component';
import { RegisterComponent } from './register/register.component';
import { UsersComponent } from './users/users.component';
import { UserResolver } from './users/users.resolver';

const routes: Routes = [
  { path: `users`, component: UsersComponent, canActivate: [AuthGuard]/*, resolve: { users: UserResolver }*/ },
  { path: `profile`, component: ProfileComponent, canActivate: [AuthGuard] },
  { path: `login`, component: LoginComponent },
  { path: `register`, component: RegisterComponent },
  { path: ``, redirectTo: '/users', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
