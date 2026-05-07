import { Component, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  searchQuery = '';
  unreadNotifications = 0;
  showMoreMenu = false;

  constructor(
    private router: Router,
    public authService: AuthService,
    private notifService: NotificationService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.notifService.getUnreadCount(user.id).subscribe(data => {
        this.unreadNotifications = data.count;
      });
    }
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
}
