import { Injectable } from '@angular/core';
import { NotifierService } from 'angular-notifier';

@Injectable({providedIn: 'root'})
export class NotificationService {
  constructor(private notifier: NotifierService) {}

  showNotification(type: notificationType, message: string): void {
    this.notifier.notify(type, message);
  }
}

type notificationType = 'default' | 'info' | 'success' | 'warning' | 'error';
