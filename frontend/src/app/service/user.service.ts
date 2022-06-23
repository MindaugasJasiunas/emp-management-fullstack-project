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

  public createUser(formData: FormData): Observable<User | HttpErrorResponse> {
    // return this.http.post<User>(this.usersHost, formData);
    const user = new User(
      null,
      'usr',
      'usr@example.com',
      'password',
      'Usr',
      'Lst',
      'http://',
      '1999-05-22',
      null,
      '1999-05-22',
      '1999-05-22',
      true,
      true,
      null
    );
    return this.http.post<User>(this.usersHost, user /*this.testAuthHeader()*/);
  }

  public updateUser(formData: FormData): Observable<User | HttpErrorResponse> {
    // return this.http.put<User>(`${this.usersHost}${publicId}`, formData);
    const publicId: string = 'e1849a7d-ec51-46a7-8afc-ec2a77e00bd6';
    const user = new User(
      null,
      'usr',
      'usr@example.com',
      'password',
      'Usr',
      'Lst',
      'http://',
      '1999-05-22',
      null,
      '1999-05-22',
      '1999-05-22',
      true,
      true,
      null
    );
    return this.http.put<User>(
      `${this.usersHost}/${publicId}`,
      user
      /*this.testAuthHeader()*/
    );
  }

  public deleteUser(publicId: string): Observable<void | HttpErrorResponse> {
    return this.http.delete<void>(
      `${this.usersHost}/${publicId}`
      /*this.testAuthHeader()*/
    );
  }

  // maybe move to authentication service !?
  public resetPassword(email: string): Observable<void | HttpErrorResponse> {
    return this.http.post<void>(`${this.host}/reset`, email);
  }
  public restorePassword(
    passwordRequest: { password: string; repeatPassword: string },
    link: string
  ): Observable<void | HttpErrorResponse> {
    return this.http.post<void>(`${this.host}/reset/${link}`, passwordRequest);
  }

  public updateProfileImage(
    formData: FormData
  ): Observable<HttpEvent<any> | HttpErrorResponse> {
    return this.http.post<any>(
      `${this.usersHost}/updateProfileImage`,
      formData,
      {
        // get progress events of an image upload
        reportProgress: true,
        observe: 'events',
      }
    );
  }

  public addUsersToLocalStorage(users: User[]) {
    localStorage.setItem('users', JSON.stringify(users));
  }

  public getUsersFromLocalStorage(): User[] | null {
    if (localStorage.getItem('users')) {
      return JSON.parse(localStorage.getItem('users')!);
    }
    return null;
  }

  public createUserFormData(
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
  }
}
