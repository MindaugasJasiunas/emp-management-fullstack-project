import { HttpErrorResponse, HttpEvent } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Data } from '@angular/router';
import { BehaviorSubject, Subscription, take } from 'rxjs';
import { RoleEnum } from '../enum/role.enum';
import { Role } from '../model/role.model';
import { User } from '../model/user.model';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { UserService } from '../service/user.service';
import { UsersDialog } from './users-dialog/users.dialog';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
})
export class UsersComponent implements OnInit, OnDestroy, AfterViewInit {
  dataSource!: MatTableDataSource<User[]>;
  displayedColumns: string[] = [
    'photo',
    'publicId',
    'firstName',
    'lastName',
    'username',
    'email',
    'status',
    'actions',
  ];
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  totalElements!: number;

  constructor(
    private userService: UserService,
    // private activatedRoute: ActivatedRoute,
    private authService: AuthenticationService,
    private notificationService: NotificationService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource();
    this.refresh();
  }

  refresh(page: number = 0, size: number = 5): void {
    this.userService
      // .getUsers2(page, size)
      .getUsers()
      .pipe(take(1)) // subscribes until 1 value taken - then automatically unsubscribes
      .subscribe({
        next: (response) => {
          this.totalElements = response.totalElements;
          // this.userService.addUsersToLocalStorage(response.content);
          this.dataSource.data = response.content;
          this.notificationService.showNotification('info', `${response.totalElements} ${response.totalElements === 1 ? `user` : `users`}  was successfully loaded.`);
        },
        error: (err: HttpErrorResponse) => {
          this.notificationService.showNotification(
            'error',
            'An error occured. Please try again'
          );
        },
      });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  ngOnDestroy(): void {}

  /*public loadPaginatorData(event: PageEvent) {
    console.log(event);
    console.log('this.paginator ', this.paginator);
    this.paginator.pageIndex = event.pageIndex;

    this.refresh(event.pageIndex, event.pageSize);
    return event;
  }*/

  applyFilter(field: KeyboardEvent) {
    const filterText = (field.target as HTMLInputElement).value;
    this.dataSource.filter = filterText.trim().toLowerCase();
  }

  onDelete(user: User) {
    // do stuff
    let confirmAction = confirm('Are you sure you want to delete the user?');
    if (confirmAction) {
      this.userService
        .deleteUser(user.publicId!)
        .pipe(take(1)) // subscribes until 1 value taken - then automatically unsubscribes
        .subscribe({
          next: (response) => {
            this.refresh();
            this.notificationService.showNotification(
              'success',
              `User with email ${user.email} was deleted successfully`
            );
          },
          error: (err: HttpErrorResponse) => {
            this.notificationService.showNotification(
              'error',
              err.error.message
            );
          },
        });
    }
  }

  openEditDialog(user: User): void {
    const dialogRef = this.dialog.open(UsersDialog, {
      height: '400px',
      width: '600px',
      data: user,
    });

    dialogRef.afterClosed().subscribe((result) => {
      // console.log('The dialog was closed');
      // console.log('RESULT:: ', result);
      if (
        result !== null &&
        result !== undefined &&
        !(typeof result === 'string' && result === '')
      ) {
        const updatedUserToSave: User = new User(user.publicId, result.username, result.email, (result.password === '' || result.password === undefined || result.password === null) ?
           'DEFAULT_PASSWORD': result.password, result.firstName, result.lastName, user.profileImageUrl, result.dao, result.active, result.notLocked, [new Role(0, result.role)]);

        // const updatedUser: User = result;
        // updatedUser.publicId = user.publicId;
        // updatedUser.profileImageUrl = user.profileImageUrl;
        // if (result.password === '' || result.password === undefined || result.password === null) {
        //   updatedUser.password = 'DEFAULT_PASSWORD';  // backend checks & users default password kept.
        // }
        // console.log(updatedUser)
        // update user in DB
        this.userService
          // .updateUser(updatedUser)
          .updateUser(updatedUserToSave)
          .pipe(take(1))
          .subscribe({
            next: (response: User) => {
              // update image if provided (refactor to external method)
              if(result.fileSource){

                const formData = new FormData();
                formData.append('email', response.email);
                formData.append('profileImage', result.fileSource);

                this.userService.updateProfileImage(formData)
                .pipe(take(1))
                .subscribe({
                  next: (response: HttpEvent<any>) => {
                    this.notificationService.showNotification(
                      'success',
                      `User ${updatedUserToSave.username} successfully updated.`
                    );
                    this.refresh();
                  },
                  error: (err: HttpErrorResponse) => {
                    console.log(err);
                    this.notificationService.showNotification(
                      'error',
                      'An error occured uploading user profile image. Please try again'
                    );
                  }
                });
              }else{
                this.notificationService.showNotification(
                  'success',
                  `User ${updatedUserToSave.username} successfully updated.`
                );
                this.refresh();
              }
            },
            error: (err: HttpErrorResponse) => {
              if (err.error.message === undefined) {
                this.notificationService.showNotification(
                  'error',
                  'An error occured. Please try again'
                );
              } else {
                this.notificationService.showNotification(
                  'error',
                  err.error.message
                );
              }
            },
          });
      }
    });
  }

  openCreateDialog(): void {
    const newDummyUser = new User(null, '', '', '', '', '', '', '', true, true, null);

    const dialogRef = this.dialog.open(UsersDialog, {
      height: '400px',
      width: '600px',
      data: newDummyUser,
    });

    dialogRef.afterClosed().subscribe((result) => {
      // console.log('The dialog was closed');
      // console.log('RESULT:: ', result);
      if (
        result !== null &&
        result !== undefined &&
        !(typeof result === 'string' && result === '')
      ) {
        const userToSave: User = new User(null, result.username, result.email, result.password, result.firstName, result.lastName, result.profileImageUrl, result.dao, result.active, result.notLocked, [new Role(0, result.role)]);
        this.userService
          .createUser(userToSave)
          .pipe(take(1))
          .subscribe({
            next: (response: User) => {
              // update image if provided (refactor to external method)
              if(result.fileSource){

                const formData = new FormData();
                formData.append('email', response.email);
                formData.append('profileImage', result.fileSource);

                this.userService.updateProfileImage(formData)
                .pipe(take(1))
                .subscribe({
                  next: (response: HttpEvent<any>) => {
                    this.notificationService.showNotification(
                      'success',
                      `User ${newDummyUser.username} successfully created.`
                    );
                    this.refresh();
                  },
                  error: (err: HttpErrorResponse) => {
                    console.log(err);
                    this.notificationService.showNotification(
                      'error',
                      'An error occured uploading user profile image. Please try again'
                    );
                  }
                });
              }else{
                this.notificationService.showNotification(
                  'success',
                  `User ${newDummyUser.username} successfully created.`
                );
                this.refresh();
              }
            },
            error: (err: HttpErrorResponse) => {
              if (err.error.message === undefined) {
                this.notificationService.showNotification(
                  'error',
                  'An error occured. Please try again'
                );
              } else {
                this.notificationService.showNotification(
                  'error',
                  err.error.message
                );
              }
            },
          });
      }
    });
  }

  checkForRole(role: string): boolean{
      return this.authService.checkForRole(role);
  }

}
