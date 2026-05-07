import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { ChatService } from '../services/chat.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit, OnDestroy {
  searchQuery = '';
  unreadNotifications = 0;
  unreadMessages = 0;
  showMoreMenu = false;

  private streamSub?: Subscription;
  private notifSub?: Subscription;
  private msgSub?: Subscription;

  constructor(
    private router: Router,
    public authService: AuthService,
    public notifService: NotificationService,
    private chatService: ChatService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.chatService.connect(user.id);
      this.notifService.getGeneralUnreadCount(user.id).subscribe(d => this.notifService.unreadNotif$.next(d.count));
      this.notifService.getMessageUnreadCount(user.id).subscribe(d => this.notifService.unreadMsg$.next(d.count));
      this.notifSub = this.notifService.unreadNotif$.subscribe(n => this.unreadNotifications = n);
      this.msgSub   = this.notifService.unreadMsg$.subscribe(n => this.unreadMessages = n);
      this.streamSub = this.notifService.streamNotifications(user.id).subscribe(event => {
        if (!event) return;
        this.notifService.incoming$.next(event);
        if (event.type === 'MESSAGE') {
          if (this.chatService.activePartnerId !== event.relatedId) {
            this.notifService.unreadMsg$.next(this.notifService.unreadMsg$.getValue() + 1);
          }
        } else {
          this.notifService.unreadNotif$.next(this.notifService.unreadNotif$.getValue() + 1);
        }
      });
    }
  }

  ngOnDestroy() {
    this.streamSub?.unsubscribe();
    this.notifSub?.unsubscribe();
    this.msgSub?.unsubscribe();
  }

  onSearch() {
    if (this.searchQuery.trim()) {
      this.router.navigate(['/descobrir'], { queryParams: { q: this.searchQuery } });
    }
  }

  logout() { this.authService.logout(); }

  get currentUserName(): string { return this.authService.getCurrentUser()?.name ?? 'Usuário'; }
  get currentUserRole(): string { return this.authService.getCurrentUser()?.profileTypes?.[0] ?? 'Founder'; }
  get currentUserInitial(): string { return this.currentUserName.charAt(0).toUpperCase(); }
  get currentUserPhoto(): string { return this.authService.getCurrentUser()?.photo ?? ''; }

  hasProfileType(type: string): boolean {
    return this.authService.getCurrentUser()?.profileTypes?.includes(type) ?? false;
  }

  get isDiscoverPage(): boolean {
    return this.router.url.startsWith('/descobrir');
  }
}
