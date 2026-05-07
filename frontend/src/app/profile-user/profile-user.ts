import { Component, OnInit } from '@angular/core';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';
import { FollowService } from '../services/follow.service';
import { ProposalService } from '../services/proposal.service';
import { SavedStartupService } from '../services/saved-startup.service';

@Component({
  selector: 'app-profile-user',
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './profile-user.html',
  styleUrl: './profile-user.scss',
})
export class ProfileUser implements OnInit {
  user: any;
  userStartups: any[] = [];
  profileForm: any = {};
  editing = false;
  activeTab: 'about' | 'investor' | 'startups' | 'analytics' = 'about';
  loading = false;
  errorMessage = '';
  successMessage = '';
  followCount = 0;
  sentProposals: any[] = [];
  savedStartups: any[] = [];
  isOwnProfile = true;
  following = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private followService: FollowService,
    private proposalService: ProposalService,
    private savedService: SavedStartupService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const paramId = params.get('id');
      const currentUser = this.authService.getCurrentUser();

      if (paramId && Number(paramId) !== currentUser?.id) {
        this.isOwnProfile = false;
        const userId = Number(paramId);
        this.loadPublicProfile(userId, currentUser?.id ?? 0);
      } else {
        this.isOwnProfile = true;
        const userId = currentUser?.id ?? 1;
        this.loadOwnProfile(userId);
      }
    });
  }

  private loadOwnProfile(userId: number) {
    this.userService.getUser(userId).subscribe(data => {
      this.user = data;
      this.profileForm = this.createFormFromUser(data);
    });
    this.userService.getUserStartups(userId).subscribe(s => { this.userStartups = s; });
    this.followService.getStatus(0, 'USER', userId).subscribe(data => { this.followCount = data.count; });
    this.proposalService.getSent(userId).subscribe(data => { this.sentProposals = data; });
    this.savedService.getSaved(userId).subscribe(data => { this.savedStartups = data; });
  }

  private loadPublicProfile(userId: number, currentUserId: number) {
    this.userService.getUser(userId).subscribe(data => { this.user = data; });
    this.userService.getUserStartups(userId).subscribe(s => { this.userStartups = s; });
    this.followService.getStatus(currentUserId, 'USER', userId).subscribe(data => {
      this.followCount = data.count;
      this.following = data.following;
    });
  }

  toggleFollow() {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !this.user) return;
    this.followService.toggleFollow(currentUser.id, 'USER', this.user.id).subscribe(() => {
      this.following = !this.following;
      this.followCount += this.following ? 1 : -1;
    });
  }

  goToMessages() {
    this.router.navigate(['/mensagens'], { queryParams: { partnerId: this.user.id } });
  }

  startEditing() {
    this.profileForm = this.createFormFromUser(this.user);
    this.errorMessage = '';
    this.successMessage = '';
    this.editing = true;
  }

  cancelEditing() { this.editing = false; this.errorMessage = ''; }

  saveProfile() {
    if (!this.profileForm.name?.trim()) { this.errorMessage = 'Nome obrigatório.'; return; }
    this.loading = true;
    this.errorMessage = '';

    this.userService.updateProfile(this.user.id, {
      ...this.profileForm,
      name: this.profileForm.name.trim(),
      profileTypes: this.splitValues(this.profileForm.profileTypesText)
    }).subscribe({
      next: (updated) => {
        this.user = updated;
        this.profileForm = this.createFormFromUser(updated);
        this.authService.updateCurrentUser({ name: updated.name, profileTypes: updated.profileTypes ?? [] });
        this.editing = false;
        this.loading = false;
        this.successMessage = 'Perfil salvo com sucesso.';
      },
      error: () => { this.errorMessage = 'Não foi possível salvar o perfil.'; this.loading = false; }
    });
  }

  splitValues(value?: string): string[] {
    return (value ?? '').split(',').map(item => item.trim()).filter(Boolean);
  }

  get founderScore(): number {
    let score = 40;
    if (this.user?.bio) score += 5;
    if (this.user?.description) score += 5;
    if (this.user?.mainSkills) score += 10;
    if (this.user?.pastExperiences) score += 10;
    if (this.userStartups.length > 0) score += 20;
    if (this.followCount > 5) score += 10;
    return Math.min(100, score);
  }

  get isInvestor(): boolean {
    return this.user?.profileTypes?.includes('Investidor') ?? false;
  }

  get investorScore(): number {
    let score = 40;
    if (this.user?.investorType) score += 10;
    if (this.user?.sectorsOfInterest) score += 10;
    if (this.user?.stagesOfInterest) score += 10;
    if (this.user?.averageTicket) score += 10;
    if (this.user?.investmentHistory) score += 10;
    if (this.user?.valueAdd) score += 10;
    if (this.followCount > 2) score += 5;
    if (this.sentProposals.length > 0) score += 5;
    return Math.min(100, score);
  }

  get acceptedProposals(): any[] {
    return this.sentProposals.filter(p => p.status === 'ACCEPTED');
  }

  private createFormFromUser(user: any) {
    return {
      name: user?.name ?? '',
      photo: user?.photo ?? '',
      bio: user?.bio ?? '',
      description: user?.description ?? '',
      location: user?.location ?? '',
      profileTypesText: (user?.profileTypes ?? []).join(', '),
      pastExperiences: user?.pastExperiences ?? '',
      mainSkills: user?.mainSkills ?? '',
      interests: user?.interests ?? '',
      externalLinks: user?.externalLinks ?? '',
      seekingCoFounder: user?.seekingCoFounder ?? false,
      desiredCoFounderType: user?.desiredCoFounderType ?? '',
      coFounderArea: user?.coFounderArea ?? '',
      coFounderDedication: user?.coFounderDedication ?? '',
      coFounderDescription: user?.coFounderDescription ?? '',
      // Investor fields
      investorType: user?.investorType ?? '',
      sectorsOfInterest: user?.sectorsOfInterest ?? '',
      stagesOfInterest: user?.stagesOfInterest ?? '',
      averageTicket: user?.averageTicket ?? '',
      investmentHistory: user?.investmentHistory ?? '',
      valueAdd: user?.valueAdd ?? '',
      operationStyle: user?.operationStyle ?? '',
      contactPreference: user?.contactPreference ?? ''
    };
  }
}
