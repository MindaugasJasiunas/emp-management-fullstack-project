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

  private testAuthHeader() {
    const headerDict = {
      'Content-Type': 'application/json',
      Accept: 'application/json',
      'Access-Control-Allow-Headers': 'Content-Type',
      Authorization:
        'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJBcHBOYW1lIEFkbWluaXN0cmF0aW9uIiwic3ViIjoiYWRtaW4iLCJpc3MiOiJBcHBOYW1lIiwiZXhwIjoxNjU3MDk1NzE4LCJpYXQiOjE2NTU4ODYxMTgsImF1dGhvcml0aWVzIjpbInVzZXI6ZGVsZXRlIiwidXNlcjpjcmVhdGUiLCJ1c2VyOnJlYWQiLCJ1c2VyOnVwZGF0ZSJdfQ.gzkY6q0yf775HGqlw5P-LvRScY9Xu8v5fufuz7JnBitv0h91DRLITa6ic2BWjGdzcBopondPmOlZ1uKe6JyMPQ',
    };
    const requestOptions = {
      headers: new HttpHeaders(headerDict),
    };
    return requestOptions;
  }

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

        // params: {
        //   email: email,
        // },
        // headers: new HttpHeaders({
        //   Authorization:
        //     'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJBcHBOYW1lIEFkbWluaXN0cmF0aW9uIiwic3ViIjoiYWRtaW4iLCJpc3MiOiJBcHBOYW1lIiwiZXhwIjoxNjU3MDk1NzE4LCJpYXQiOjE2NTU4ODYxMTgsImF1dGhvcml0aWVzIjpbInVzZXI6ZGVsZXRlIiwidXNlcjpjcmVhdGUiLCJ1c2VyOnJlYWQiLCJ1c2VyOnVwZGF0ZSJdfQ.gzkY6q0yf775HGqlw5P-LvRScY9Xu8v5fufuz7JnBitv0h91DRLITa6ic2BWjGdzcBopondPmOlZ1uKe6JyMPQ',
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
