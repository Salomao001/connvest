import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { SearchService } from '../services/search.service';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';
import { SavedStartupService } from '../services/saved-startup.service';
import { FollowService } from '../services/follow.service';
import { ProposalService } from '../services/proposal.service';
import { UserService } from '../services/user.service';
import { environment } from '../../environments/environment';
import { AppSelectComponent, SelectOption } from '../shared/app-select.component';

type DiscoverTab = 'startups' | 'pessoas' | 'investidores' | 'cofounders';

@Component({
  selector: 'app-discover',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, AppSelectComponent],
  templateUrl: './discover.component.html',
  styleUrl: './discover.component.scss'
})
export class DiscoverComponent implements OnInit {
  activeTab: DiscoverTab = 'startups';
  searchQuery = '';
  loading = false;

  startups: any[] = [];
  loadingStartups = false;
  stageFilter = '';
  sectorFilter = '';

  users: any[] = [];
  profileTypeFilter = '';

  investors: any[] = [];
  loadingInvestors = false;
  investorTypeFilter = '';
  investorSectorFilter = '';
  investorStageFilter = '';

  coFounderUsers: any[] = [];
  loadingCoFounders = false;
  coFounderArea = '';
  coFounderDedication = '';

  currentUser: any;
  userStartups: any[] = [];
  savedStartupIds = new Set<number>();
  followingIds = new Set<string>();

  showInvestorModal = false;
  selectedInvestor: any = null;
  investorProposal: any = {};
  sendingInvestorProposal = false;
  investorProposalSent = false;

  showCoFounderModal = false;
  proposalTarget: any = null;
  coFounderProposal: any = {};
  sendingCoFounderProposal = false;
  coFounderProposalSent = false;

  startupStages = [
    { value: '', label: 'Todos os estágios' },
    { value: 'Ideia', label: 'Ideia' },
    { value: 'MVP', label: 'MVP' },
    { value: 'Primeiros usuários', label: 'Primeiros usuários' },
    { value: 'Receita inicial', label: 'Receita inicial' },
    { value: 'Tração', label: 'Tração' },
    { value: 'Escala', label: 'Escala' }
  ];

  startupSectors = [
    '', 'Fintech', 'Healthtech', 'AgriTech', 'Edtech', 'B2B SaaS',
    'DeepTech', 'PropTech', 'E-commerce', 'Marketplace', 'HR Tech', 'Outro'
  ];

  profileTypes = [
    { value: '', label: 'Todos os perfis' },
    { value: 'Founder', label: 'Founder' },
    { value: 'Advisor', label: 'Advisor' },
    { value: 'Investidor', label: 'Investidor' }
  ];

  investorTypes = [
    { value: '', label: 'Todos os tipos' },
    { value: 'angel', label: 'Angel' },
    { value: 'VC', label: 'VC' },
    { value: 'scout', label: 'Scout' },
    { value: 'advisor', label: 'Advisor' }
  ];

  investorSectors = [
    '', 'Fintech', 'Healthtech', 'AgriTech', 'Edtech', 'B2B SaaS', 'DeepTech', 'PropTech', 'E-commerce'
  ];

  investorStages = ['', 'Pre-seed', 'Seed', 'Series A', 'Series B'];

  coFounderAreas = [
    { value: '', label: 'Todas as áreas' },
    { value: 'tech', label: 'Tech' },
    { value: 'business', label: 'Business' },
    { value: 'produto', label: 'Produto' },
    { value: 'marketing', label: 'Marketing' },
    { value: 'vendas', label: 'Vendas' },
    { value: 'operacao', label: 'Operação' }
  ];

  coFounderDedications = [
    { value: '', label: 'Qualquer dedicação' },
    { value: 'full-time', label: 'Full-time' },
    { value: 'part-time', label: 'Part-time' },
    { value: 'advisor', label: 'Advisor' }
  ];

  constructor(
    private route: ActivatedRoute,
    private searchService: SearchService,
    private startupService: StartupService,
    private authService: AuthService,
    private savedService: SavedStartupService,
    private followService: FollowService,
    private proposalService: ProposalService,
    private userService: UserService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.loadSavedIds();
      this.userService.getUserStartups(this.currentUser.id).subscribe(s => { this.userStartups = s; });
    }
    this.route.queryParams.subscribe(params => {
      this.searchQuery = params['q'] || '';
      if (params['tab']) this.activeTab = params['tab'] as DiscoverTab;
      this.loadActiveTab();
    });
  }

  setTab(tab: DiscoverTab) {
    this.activeTab = tab;
    this.searchQuery = '';
    this.loadActiveTab();
  }

  loadActiveTab() {
    switch (this.activeTab) {
      case 'startups': this.loadStartups(); break;
      case 'pessoas': this.loadPeople(); break;
      case 'investidores': this.loadInvestors(); break;
      case 'cofounders': this.loadCoFounders(); break;
    }
  }

  onSearch() { this.loadActiveTab(); }

  loadSavedIds() {
    this.savedService.getSaved(this.currentUser.id).subscribe(items => {
      this.savedStartupIds = new Set(items.map((i: any) => i.savedStartup?.startupId));
    });
  }

  loadStartups() {
    if (this.searchQuery) {
      this.loading = true;
      this.searchService.search(this.searchQuery).subscribe({
        next: (data) => {
          let result = data.startups as any[];
          if (this.stageFilter) result = result.filter(s => s.stage === this.stageFilter);
          if (this.sectorFilter) result = result.filter(s => s.sector === this.sectorFilter);
          this.startups = result;
          this.loading = false;
        },
        error: () => { this.loading = false; }
      });
    } else {
      this.loadingStartups = true;
      this.startupService.getStartups(this.stageFilter).subscribe({
        next: (startups: any[]) => {
          this.startups = this.sectorFilter ? startups.filter(s => s.sector === this.sectorFilter) : startups;
          this.loadingStartups = false;
        },
        error: () => { this.loadingStartups = false; }
      });
    }
  }

  loadPeople() {
    this.loading = true;
    this.searchService.search(this.searchQuery || ' ').subscribe({
      next: (data) => {
        let result = data.users as any[];
        if (this.profileTypeFilter) result = result.filter(u => u.profileTypes?.includes(this.profileTypeFilter));
        this.users = result;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  loadInvestors() {
    this.loadingInvestors = true;
    const url = `${environment.apiUrl}/search/investors?q=${encodeURIComponent(this.searchQuery)}&type=${this.investorTypeFilter}&sector=${encodeURIComponent(this.investorSectorFilter)}&stage=${encodeURIComponent(this.investorStageFilter)}`;
    this.http.get<any[]>(url).subscribe({
      next: data => { this.investors = data; this.loadingInvestors = false; },
      error: () => { this.loadingInvestors = false; }
    });
  }

  loadCoFounders() {
    this.loadingCoFounders = true;
    this.searchService.searchCoFounders(this.coFounderArea).subscribe({
      next: (users: any[]) => {
        let result = users.filter(u => u.id !== this.currentUser?.id);
        if (this.coFounderDedication) result = result.filter(u => u.coFounderDedication === this.coFounderDedication);
        if (this.searchQuery) result = result.filter(u => u.name?.toLowerCase().includes(this.searchQuery.toLowerCase()));
        this.coFounderUsers = result;
        this.loadingCoFounders = false;
      },
      error: () => { this.loadingCoFounders = false; }
    });
  }

  toggleSave(startup: any, event: Event) {
    event.preventDefault(); event.stopPropagation();
    if (!this.currentUser) return;
    if (this.savedStartupIds.has(startup.id)) {
      this.savedService.unsave(this.currentUser.id, startup.id).subscribe(() => this.savedStartupIds.delete(startup.id));
    } else {
      this.savedService.save(this.currentUser.id, startup.id).subscribe(() => this.savedStartupIds.add(startup.id));
    }
  }

  isSaved(id: number): boolean { return this.savedStartupIds.has(id); }

  followUser(userId: number, event: Event) {
    event.preventDefault();
    if (!this.currentUser) return;
    this.followService.toggleFollow(this.currentUser.id, 'USER', userId).subscribe(result => {
      const key = `USER_${userId}`;
      if (result.following) this.followingIds.add(key); else this.followingIds.delete(key);
    });
  }

  isFollowing(type: string, id: number): boolean { return this.followingIds.has(`${type}_${id}`); }

  // Investor proposal
  isAdvisor(investor: any): boolean { return investor?.investorType?.toLowerCase() === 'advisor'; }

  openInvestorProposal(investor: any) {
    this.selectedInvestor = investor;
    this.investorProposal = { startupId: '', pitch: '', seekedValue: '', capitalUse: '', whyThisInvestor: '', supportArea: '' };
    this.investorProposalSent = false;
    this.showInvestorModal = true;
  }

  closeInvestorModal() { this.showInvestorModal = false; this.selectedInvestor = null; }

  sendInvestorProposal() {
    if (!this.investorProposal.pitch?.trim() || !this.currentUser) return;
    const startup = this.userStartups.find(s => String(s.startupId) === String(this.investorProposal.startupId));
    this.sendingInvestorProposal = true;
    this.proposalService.sendProposal({
      senderId: this.currentUser.id,
      receiverId: this.selectedInvestor.id,
      type: this.isAdvisor(this.selectedInvestor) ? 'ADVISOR' : 'INVESTMENT',
      startupId: startup?.startupId,
      startupName: startup?.name,
      pitch: this.investorProposal.pitch,
      seekedValue: this.investorProposal.seekedValue,
      capitalUse: this.investorProposal.capitalUse,
      whyThisInvestor: this.investorProposal.whyThisInvestor,
      supportArea: this.investorProposal.supportArea
    }).subscribe({
      next: () => { this.investorProposalSent = true; this.sendingInvestorProposal = false; },
      error: () => { this.sendingInvestorProposal = false; }
    });
  }

  get profileCompleteness(): number {
    if (!this.currentUser) return 0;
    const fields = ['bio', 'location', 'mainSkills', 'photo'];
    return Math.round(fields.filter(f => !!this.currentUser[f]).length / fields.length * 100);
  }

  investorScore(investor: any): number {
    let score = 50;
    if (investor.sectorsOfInterest) score += 15;
    if (investor.stagesOfInterest) score += 10;
    if (investor.averageTicket) score += 10;
    if (investor.investmentHistory) score += 10;
    if (investor.valueAdd) score += 5;
    return Math.min(100, score);
  }

  // Co-founder proposal
  openCoFounderProposal(target: any) {
    this.proposalTarget = target;
    this.coFounderProposal = { startupId: '', pitch: '', expectedRole: '', desiredProfile: '', expectedDedication: '', equityOffer: '' };
    this.coFounderProposalSent = false;
    this.showCoFounderModal = true;
  }

  closeCoFounderModal() { this.showCoFounderModal = false; }

  sendCoFounderProposal() {
    if (!this.coFounderProposal.pitch?.trim() || !this.currentUser) return;
    const startup = this.userStartups.find(s => String(s.startupId) === String(this.coFounderProposal.startupId));
    this.sendingCoFounderProposal = true;
    this.proposalService.sendProposal({
      senderId: this.currentUser.id,
      receiverId: this.proposalTarget.id,
      type: 'CO_FOUNDER',
      startupId: startup?.startupId,
      startupName: startup?.name,
      pitch: this.coFounderProposal.pitch,
      expectedRole: this.coFounderProposal.expectedRole,
      desiredProfile: this.coFounderProposal.desiredProfile,
      expectedDedication: this.coFounderProposal.expectedDedication,
      equityOffer: this.coFounderProposal.equityOffer
    }).subscribe({
      next: () => { this.coFounderProposalSent = true; this.sendingCoFounderProposal = false; },
      error: () => { this.sendingCoFounderProposal = false; }
    });
  }

  compatibilityScore(target: any): number {
    if (!this.currentUser) return 60;
    let score = 50;
    const mySkills = (this.currentUser.mainSkills || '').toLowerCase();
    const targetArea = (target.coFounderArea || '').toLowerCase();
    if (mySkills && targetArea && !mySkills.includes(targetArea)) score += 25;
    if (target.seekingCoFounder) score += 15;
    if (target.location && this.currentUser.location && target.location === this.currentUser.location) score += 10;
    return Math.min(99, score);
  }

  scoreColor(score: number): string {
    if (score >= 80) return 'text-emerald-600';
    if (score >= 60) return 'text-blue-600';
    return 'text-slate-500';
  }

  logoColor(name: string): string {
    const colors = ['bg-blue-700', 'bg-emerald-600', 'bg-teal-600', 'bg-orange-500', 'bg-violet-600'];
    return colors[(name?.charCodeAt(0) || 0) % colors.length];
  }

  get startupOptions(): SelectOption[] {
    return [
      { value: '', label: 'Selecionar startup' },
      ...this.userStartups.map(s => ({ value: String(s.startupId), label: s.name }))
    ];
  }

  get dedicationOptions(): SelectOption[] {
    return [
      { value: '', label: 'Selecionar' },
      { value: 'full-time', label: 'Full-time' },
      { value: 'part-time', label: 'Part-time' },
      { value: 'advisor', label: 'Advisor' }
    ];
  }

  getInitial(name: string): string { return (name || 'U').charAt(0).toUpperCase(); }

  splitTags(value: string): string[] {
    if (!value) return [];
    return value.split(',').map(t => t.trim()).filter(Boolean);
  }
}
