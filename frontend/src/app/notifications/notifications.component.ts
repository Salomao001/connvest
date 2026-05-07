import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.component.html'
})
export class NotificationsComponent implements OnInit {
  notifications: any[] = [];
  loading = true;
  currentUser: any;

  constructor(
    private notifService: NotificationService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.notifService.getNotifications(this.currentUser.id).subscribe(data => {
        this.notifications = data;
        this.loading = false;
      });
    }
  }

  markAllRead() {
    if (!this.currentUser) return;
    this.notifService.markAllAsRead(this.currentUser.id).subscribe(() => {
      this.notifications.forEach(n => n.read = true);
    });
  }

  clickNotification(n: any) {
    if (!n.read) {
      this.notifService.markAsRead(n.id).subscribe();
      n.read = true;
    }
    if (n.relatedType === 'proposal') this.router.navigate(['/propostas']);
    else if (n.relatedType === 'user') this.router.navigate(['/mensagens']);
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
