import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../services/notification.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.component.html'
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: any[] = [];
  loading = true;
  currentUser: any;

  private incomingSub?: Subscription;

  constructor(
    private notifService: NotificationService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) return;

    this.notifService.getNotifications(this.currentUser.id).subscribe(data => {
      this.notifications = data;
      this.loading = false;
    });

    this.incomingSub = this.notifService.incoming$.subscribe((event: any) => {
      if (event.type === 'MESSAGE') return;
      const notif = {
        id: event.id,
        type: event.type,
        title: event.title,
        body: event.body,
        relatedId: event.relatedId,
        read: false,
        createdAt: new Date().toISOString()
      };
      this.notifications = [notif, ...this.notifications];
    });
  }

  ngOnDestroy() {
    this.incomingSub?.unsubscribe();
  }

  markAllRead() {
    if (!this.currentUser) return;
    this.notifService.markAllAsRead(this.currentUser.id).subscribe(() => {
      this.notifications.forEach(n => n.read = true);
      this.notifService.unreadNotif$.next(0);
    });
  }

  clickNotification(n: any) {
    if (!n.read) {
      this.notifService.markAsRead(n.id).subscribe();
      n.read = true;
      const current = this.notifService.unreadNotif$.getValue();
      this.notifService.unreadNotif$.next(Math.max(0, current - 1));
    }
    if (n.type === 'FOLLOW') this.router.navigate(['/user', n.relatedId]);
    else if (n.type === 'MESSAGE') this.router.navigate(['/mensagens'], { queryParams: { partnerId: n.relatedId } });
    else if (n.type === 'PROPOSAL') this.router.navigate(['/propostas']);
    else if (n.type === 'COMMENT' || n.type === 'LIKE') this.router.navigate(['/']);
    else if (n.type === 'INVITE') this.router.navigate(['/startup']);
  }

  typeIcon(type: string): string {
    const icons: Record<string, string> = { LIKE: '♥', COMMENT: '♢', FOLLOW: '♙', PROPOSAL: '▱', MESSAGE: '□', INVITE: '✉' };
    return icons[type] || '•';
  }

  typeColor(type: string): string {
    const colors: Record<string, string> = {
      LIKE: 'bg-rose-100 text-rose-600',
      COMMENT: 'bg-blue-100 text-blue-600',
      FOLLOW: 'bg-emerald-100 text-emerald-600',
      PROPOSAL: 'bg-amber-100 text-amber-600',
      MESSAGE: 'bg-violet-100 text-violet-600',
      INVITE: 'bg-sky-100 text-sky-600'
    };
    return colors[type] || 'bg-slate-100 text-slate-600';
  }

  timeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const diff = Date.now() - new Date(dateStr).getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 60) return `há ${mins}min`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `há ${hrs}h`;
    return `há ${Math.floor(hrs / 24)}d`;
  }

  get unreadCount() { return this.notifications.filter(n => !n.read).length; }
}
