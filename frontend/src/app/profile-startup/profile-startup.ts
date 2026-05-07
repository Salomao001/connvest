import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { FollowService } from '../services/follow.service';
import { SavedStartupService } from '../services/saved-startup.service';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-profile-startup',
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './profile-startup.html',
  styleUrl: './profile-startup.scss',
})
export class ProfileStartup implements OnInit {
  startup: any;
  startupForm: any = {};
  startupId = 1;
  editing = false;
  loading = false;
  canEdit = false;
  currentStartupRole = '';
  errorMessage = '';
  successMessage = '';
  members: any[] = [];
  memberSearchQuery = '';
  inviteResults: any[] = [];
  inviteRole = 'Viewer';
  inviteMessage = '';
  inviteLoading = false;
  inviteError = '';
  inviteSuccess = '';
  inviteRoles = ['Admin', 'Editor', 'Viewer', 'Advisor', 'Co-founder'];
  memberRoles = ['Admin', 'Editor', 'Viewer', 'Advisor', 'Co-founder'];
  activeTab: 'about' | 'metrics' | 'members' | 'analytics' = 'about';
  followCount = 0;
  isFollowing = false;
  isSaved = false;
  recentPosts: any[] = [];
  showBoostModal = false;

  constructor(
    private startupService: StartupService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private userService: UserService,
    private followService: FollowService,
    private savedService: SavedStartupService,
    private postService: PostService
  ) {}

  ngOnInit() {
    this.startupId = Number(this.route.snapshot.paramMap.get('id') ?? 1);
    const user = this.authService.getCurrentUser();

    this.startupService.getStartup(this.startupId, user?.id).subscribe(data => {
      this.startup = data;
      this.startupForm = this.createFormFromStartup(data);
    });

    this.loadMembers();

    if (user) {
      this.userService.getUserStartups(user.id).subscribe(startups => {
        const membership = startups.find(startup => Number(startup.startupId) === this.startupId);
        this.currentStartupRole = membership?.role ?? '';
        this.canEdit = membership?.role === 'Owner' || membership?.role === 'Admin';
      });
      this.followService.getStatus(user.id, 'STARTUP', this.startupId).subscribe(data => {
        this.isFollowing = data.following;
        this.followCount = data.count;
      });
      this.savedService.checkSaved(user.id, this.startupId).subscribe(data => {
        this.isSaved = data.saved;
      });
    }

    this.postService.getPosts().subscribe(posts => {
      this.recentPosts = posts.filter((p: any) => p.startupId === this.startupId).slice(0, 5);
    });
  }

  toggleFollow() {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.followService.toggleFollow(user.id, 'STARTUP', this.startupId).subscribe(data => {
      this.isFollowing = data.following;
      this.followCount = data.count;
    });
  }

  toggleSave() {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    if (this.isSaved) {
      this.savedService.unsave(user.id, this.startupId).subscribe(() => { this.isSaved = false; });
    } else {
      this.savedService.save(user.id, this.startupId).subscribe(() => { this.isSaved = true; });
    }
  }

  get startupScore(): number {
    if (!this.startup) return 0;
    let score = 40;
    if (this.startup.monthlyRevenue) score += 15;
    if (this.startup.usersCount > 0) score += 10;
    if (this.startup.mrr) score += 10;
    if (this.startup.currentObjective) score += 5;
    if (this.followCount > 2) score += 10;
    if (this.recentPosts.length > 0) score += 10;
    return Math.min(100, score);
  }

  startEditing() {
    this.startupForm = this.createFormFromStartup(this.startup);
    this.errorMessage = '';
    this.successMessage = '';
    this.editing = true;
  }

  cancelEditing() {
    this.editing = false;
    this.errorMessage = '';
  }

  saveStartup() {
    if (!this.startupForm.name?.trim()) {
      this.errorMessage = 'Nome da startup obrigatorio.';
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage = 'Usuario nao autenticado.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.startupService.updateStartup(this.startup.id, user.id, {
      ...this.startupForm,
      name: this.startupForm.name.trim()
    }).subscribe({
      next: (updated) => {
        this.startup = updated;
        this.startupForm = this.createFormFromStartup(updated);
        this.editing = false;
        this.loading = false;
        this.successMessage = 'Startup atualizada com sucesso.';
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error?.status === 403
          ? 'Voce nao tem permissao para editar esta startup.'
          : 'Nao foi possivel salvar a startup.';
      }
    });
  }

  loadMembers() {
    this.startupService.getStartupMembers(this.startupId).subscribe(members => {
      this.members = members;
    });
  }

  private createFormFromStartup(startup: any) {
    return {
      name: startup?.name ?? '',
      logo: startup?.logo ?? '',
      shortDescription: startup?.shortDescription ?? '',
      description: startup?.description ?? '',
      fullDescription: startup?.fullDescription ?? '',
      sector: startup?.sector ?? '',
      stage: startup?.stage ?? '',
      location: startup?.location ?? '',
      websiteUrl: startup?.websiteUrl ?? '',
      currentObjective: startup?.currentObjective ?? '',
      mainMetrics: startup?.mainMetrics ?? '',
      monthlyRevenue: startup?.monthlyRevenue ?? '',
      usersCount: startup?.usersCount ?? null,
      growthPercent: startup?.growthPercent ?? '',
      clientsCount: startup?.clientsCount ?? null,
      mrr: startup?.mrr ?? '',
      churn: startup?.churn ?? '',
      metricsPublic: startup?.metricsPublic ?? true
    };
  }

  hasStructuredMetrics(): boolean {
    return !!(
      this.startup?.monthlyRevenue ||
      this.startup?.usersCount === 0 ||
      this.startup?.usersCount ||
      this.startup?.growthPercent ||
      this.startup?.clientsCount === 0 ||
      this.startup?.clientsCount ||
      this.startup?.mrr ||
      this.startup?.churn
    );
  }

  searchInviteUsers() {
    const query = this.memberSearchQuery.trim();
    this.inviteError = '';
    this.inviteSuccess = '';

    if (!query) {
      this.inviteResults = [];
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    this.userService.searchUsers(query).subscribe(users => {
      this.inviteResults = users.filter(user => user.id !== currentUser?.id);
    });
  }

  inviteMember(user: any) {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.inviteError = 'Usuario nao autenticado.';
      return;
    }

    this.inviteLoading = true;
    this.inviteError = '';
    this.inviteSuccess = '';

    this.startupService.inviteMember(
      this.startupId,
      currentUser.id,
      user.id,
      this.inviteRole,
      this.inviteMessage
    ).subscribe({
      next: () => {
        this.inviteLoading = false;
        this.inviteSuccess = 'Convite enviado com sucesso.';
        this.memberSearchQuery = '';
        this.inviteMessage = '';
        this.inviteResults = [];
      },
      error: (error) => {
        this.inviteLoading = false;
        this.inviteError = error?.error?.error || 'Nao foi possivel enviar o convite.';
      }
    });
  }

  canManageMembers(): boolean {
    return this.currentStartupRole === 'Owner';
  }

  updateMemberRole(member: any, role: string) {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      return;
    }

    this.startupService.updateMemberRole(this.startupId, member.userId, currentUser.id, role).subscribe(updated => {
      member.role = updated.role;
    });
  }

  removeMember(member: any) {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      return;
    }

    this.startupService.removeMember(this.startupId, member.userId, currentUser.id).subscribe(() => {
      this.members = this.members.filter(existing => existing.userId !== member.userId);
    });
  }
}
