import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpEvent,
  HttpHeaders,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private host = environment.apiUrl;
  private usersHost = environment.usersApiUrl;

  constructor(private http: HttpClient) {}

  public getUsers(): Observable<any | HttpErrorResponse> {
    return this.http.get<User[]>(this.usersHost);
  }

  public getUsers2(page: number, size: number): Observable<any> {
    return this.http.get<User[]>(
      `${this.usersHost}/?size=${size}&page=${page}`
    );
  }

  public createUser(user: User /*formData: FormData*/): Observable<User> {
    // return this.http.post<User>(this.usersHost, formData);
    return this.http.post<User>(this.usersHost, user);
  }

  public updateUser(user: User /*formData: FormData*/): Observable<User> {
    // return this.http.put<User>(`${this.usersHost}${publicId}`, formData);
    return this.http.put<User>(`${this.usersHost}/${user.publicId}`, user);
  }

  public deleteUser(publicId: string): Observable<void> {
    return this.http.delete<void>(`${this.usersHost}/${publicId}`);
  }

  // maybe move to authentication service !?
  public resetPassword(request: {email: string}): Observable<void> {
    return this.http.post<void>(`${this.host}/reset`, request, {headers : new HttpHeaders({ 'Content-Type': 'application/json' })});
  }
  public restorePassword(
    passwordRequest: { newPassword: string; newPasswordRepeated: string },
    link: string
  ): Observable<void> {
    return this.http.post<void>(`${this.host}/reset/${link}`, passwordRequest, {headers : new HttpHeaders({ 'Content-Type': 'application/json' })});
  }

  public updateProfileImage(
    data: FormData
  ): Observable<HttpEvent<any>> {
    return this.http.post<any>(
      `${this.usersHost}/updateProfileImage`,
      data,
      {
        // get progress events of an image upload
        reportProgress: true,
        // observe: 'events',
        // headers : new HttpHeaders({ 'Content-Type': 'multipart/form-data' }) // setting manually does not set multipart boundary - throws error in the backend
      }
    );
  }

  /*public addUsersToLocalStorage(users: User[]) {
    localStorage.setItem('users', JSON.stringify(users));
  }*/

  /*public getUsersFromLocalStorage(): User[] | null {
    if (localStorage.getItem('users')) {
      return JSON.parse(localStorage.getItem('users')!);
    }
    return null;
  }*/

  /*public createUserFormData(
    loggedInUsername: string,
    user: User,
    profileImage: File
  ): FormData {
    const formData = new FormData();
    formData.append('publicId', user.lastName);
    formData.append('username', user.lastName);
    formData.append('email', user.lastName);
    formData.append('password', user.password || '');
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('roles', JSON.stringify(user.roles));
    formData.append('profileImg', profileImage);
    formData.append('profileImageUrl', user.profileImageUrl);
    formData.append('isActive', JSON.stringify(user.active));
    formData.append('isNotLocked', JSON.stringify(user.notLocked));
    formData.append('dateOfBirth', user.dateOfBirth);
    return formData;
  }*/
}
