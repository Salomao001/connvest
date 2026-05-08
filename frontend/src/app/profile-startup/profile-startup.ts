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
import { AppSelectComponent, SelectOption } from '../shared/app-select.component';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

type DashTab = 'overview' | 'market' | 'financial' | 'team' | 'captable' | 'round' | 'risks' | string;

@Component({
  selector: 'app-profile-startup',
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './profile-startup.html',
  styleUrl: './profile-startup.scss',
})
export class ProfileStartup implements OnInit {
  startup: any;
  startupId = 1;
  editing = false;
  loading = false;
  canEdit = false;
  currentStartupRole = '';
  errorMessage = '';
  successMessage = '';

  // Dashboard
  activeTab: DashTab = 'overview';
  followCount = 0;
  isFollowing = false;
  isSaved = false;
  recentPosts: any[] = [];
  showBoostModal = false;

  // Team
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

  // Cap table
  capTable: any[] = [];
  capTableForm: any[] = [];
  savingCapTable = false;

  // Riscos
  risks: any[] = [];
  risksForm: any[] = [];
  savingRisks = false;

  // Nichos
  availableNiches: any[] = [];
  nicheData: any[] = [];
  nicheDataMap: Record<string, Record<string, string>> = {};
  nicheFormsMap: Record<string, Record<string, string>> = {};

  // Edit form
  startupForm: any = {};
  editSection: 'identity' | 'market' | 'financial' | 'round' | 'niches' | null = null;

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

    forkJoin({
      startup: this.startupService.getStartup(this.startupId, user?.id),
      members: this.startupService.getStartupMembers(this.startupId),
      capTable: this.startupService.getCapTable(this.startupId).pipe(catchError(() => of([]))),
      risks: this.startupService.getRisks(this.startupId).pipe(catchError(() => of([]))),
      nicheData: this.startupService.getNicheData(this.startupId).pipe(catchError(() => of([]))),
      niches: this.startupService.getNiches().pipe(catchError(() => of([]))),
      posts: this.postService.getPosts().pipe(catchError(() => of([]))),
    }).subscribe(({ startup, members, capTable, risks, nicheData, niches, posts }) => {
      this.startup = startup;
      this.members = members;
      this.capTable = capTable;
      this.risks = risks;
      this.nicheData = nicheData;
      this.availableNiches = niches;
      this.recentPosts = (posts as any[]).filter((p: any) => p.startupId === this.startupId).slice(0, 5);
      this.buildNicheDataMap();
    });

    if (user) {
      this.userService.getUserStartups(user.id).subscribe(startups => {
        const m = startups.find(s => Number(s.startupId) === this.startupId);
        this.currentStartupRole = m?.role ?? '';
        this.canEdit = m?.role === 'Owner' || m?.role === 'Admin';
      });
      this.followService.getStatus(user.id, 'STARTUP', this.startupId).subscribe(d => {
        this.isFollowing = d.following;
        this.followCount = d.count;
      });
      this.savedService.checkSaved(user.id, this.startupId).subscribe(d => {
        this.isSaved = d.saved;
      });
    }
  }

  // --- Navegação ---

  setTab(tab: DashTab) { this.activeTab = tab; }

  get nicheTabKeys(): string[] {
    if (!this.startup?.selectedNiches?.length) return [];
    return this.startup.selectedNiches;
  }

  nicheLabel(key: string): string {
    return this.availableNiches.find(n => n.key === key)?.label ?? key;
  }

  nicheFields(key: string): any[] {
    return this.availableNiches.find(n => n.key === key)?.fields ?? [];
  }

  // --- Readiness ---

  private readonly READINESS_TOTAL = 21;

  get readinessItems(): Array<{
    label: string;
    detail: string;
    section: string;
    editSection?: 'identity' | 'market' | 'financial' | 'round' | 'niches';
    tab?: DashTab;
  }> {
    if (!this.startup) return [];
    type Item = { label: string; detail: string; section: string; editSection?: 'identity' | 'market' | 'financial' | 'round' | 'niches'; tab?: DashTab };
    const items: Item[] = [];
    const s = this.startup;

    if (!(s.pitch || s.shortDescription))
      items.push({ label: 'Pitch', detail: 'Tagline curto que resume o que a startup faz', section: 'Identidade', editSection: 'identity' });
    if (!s.problemDescription)
      items.push({ label: 'Problema', detail: 'Qual dor ou problema concreto a startup resolve?', section: 'Identidade', editSection: 'identity' });
    if (!s.solutionDescription)
      items.push({ label: 'Solução', detail: 'Como a startup resolve o problema?', section: 'Identidade', editSection: 'identity' });
    if (!s.competitiveDifferential)
      items.push({ label: 'Diferencial competitivo', detail: 'O que torna a startup difícil de copiar?', section: 'Identidade', editSection: 'identity' });
    if (!s.websiteUrl)
      items.push({ label: 'Site', detail: 'URL do site oficial da startup', section: 'Identidade', editSection: 'identity' });
    if (!s.pitchDeckUrl)
      items.push({ label: 'Pitch deck', detail: 'Link do deck para investidores (Drive, Notion, etc.)', section: 'Identidade', editSection: 'identity' });

    if (!s.tam)
      items.push({ label: 'TAM', detail: 'Tamanho do mercado total endereçável', section: 'Mercado', editSection: 'market' });
    if (!s.sam)
      items.push({ label: 'SAM', detail: 'Parte do mercado que a startup pode atingir agora', section: 'Mercado', editSection: 'market' });
    if (!s.som)
      items.push({ label: 'SOM', detail: 'Fatia realista que a startup pretende capturar', section: 'Mercado', editSection: 'market' });
    if (!s.marketTiming)
      items.push({ label: 'Timing de mercado', detail: 'Por que este é o momento certo para essa solução?', section: 'Mercado', editSection: 'market' });

    if (!(s.mrr || s.monthlyRevenue))
      items.push({ label: 'MRR / Receita', detail: 'Receita recorrente mensal ou receita atual', section: 'Financeiro', editSection: 'financial' });
    if (!s.cac)
      items.push({ label: 'CAC', detail: 'Custo médio para adquirir um novo cliente', section: 'Financeiro', editSection: 'financial' });
    if (!s.ltv)
      items.push({ label: 'LTV', detail: 'Valor total gerado por um cliente ao longo do tempo', section: 'Financeiro', editSection: 'financial' });
    if (!s.grossMargin)
      items.push({ label: 'Margem bruta', detail: 'Percentual de margem bruta do produto/serviço', section: 'Financeiro', editSection: 'financial' });
    if (!s.burnRate)
      items.push({ label: 'Burn rate', detail: 'Quanto a startup consome de caixa por mês', section: 'Financeiro', editSection: 'financial' });
    if (!s.runway)
      items.push({ label: 'Runway', detail: 'Quantos meses de caixa restam com o burn atual', section: 'Financeiro', editSection: 'financial' });

    if (!s.roundStatus)
      items.push({ label: 'Status da rodada', detail: 'Está captando agora? Rodada aberta, fechada ou planejada?', section: 'Rodada', editSection: 'round' });
    if (!s.roundValuation)
      items.push({ label: 'Valuation', detail: 'Valuation pre-money da rodada atual', section: 'Rodada', editSection: 'round' });
    if (!s.roundCapitalUse)
      items.push({ label: 'Uso do capital', detail: 'Como o capital captado será alocado?', section: 'Rodada', editSection: 'round' });

    if (this.capTable.length === 0)
      items.push({ label: 'Cap table', detail: 'Estrutura societária com percentuais de cada sócio/investidor', section: 'Cap Table', tab: 'captable' });
    if (this.risks.length === 0)
      items.push({ label: 'Riscos', detail: 'Principais riscos do negócio e como estão sendo mitigados', section: 'Riscos', tab: 'risks' });

    return items;
  }

  get readinessScore(): number {
    if (!this.startup) return 0;
    const missing = this.readinessItems.length;
    return Math.round(((this.READINESS_TOTAL - missing) / this.READINESS_TOTAL) * 100);
  }

  get readinessMissing(): string[] {
    return this.readinessItems.map(i => i.label);
  }

  // --- Visibilidade ---

  get fieldVisibility(): Record<string, boolean> {
    try { return JSON.parse(this.startup?.fieldVisibilityJson || '{}'); } catch { return {}; }
  }

  isFieldVisible(key: string): boolean {
    const vis = this.fieldVisibility;
    return vis[key] !== false;
  }

  toggleFieldVisibility(key: string) {
    const vis = this.fieldVisibility;
    vis[key] = !this.isFieldVisible(key);
    this.startup.fieldVisibilityJson = JSON.stringify(vis);
  }

  // --- Follow / Save ---

  toggleFollow() {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.followService.toggleFollow(user.id, 'STARTUP', this.startupId).subscribe(d => {
      this.isFollowing = d.following;
      this.followCount = d.count;
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

  // --- Edit sections ---

  startEdit(section: typeof this.editSection) {
    this.editSection = section;
    this.startupForm = this.buildForm(this.startup);
    this.errorMessage = '';
    this.successMessage = '';
    this.editing = true;
  }

  cancelEdit() {
    this.editing = false;
    this.editSection = null;
    this.errorMessage = '';
  }

  saveStartup() {
    if (!this.startupForm.name?.trim()) { this.errorMessage = 'Nome obrigatório.'; return; }
    const user = this.authService.getCurrentUser();
    if (!user) { this.errorMessage = 'Não autenticado.'; return; }

    this.loading = true;
    this.errorMessage = '';

    const payload = { ...this.startup, ...this.startupForm };

    this.startupService.updateStartup(this.startup.id, user.id, payload).subscribe({
      next: (updated) => {
        this.startup = updated;
        this.editing = false;
        this.editSection = null;
        this.loading = false;
        this.successMessage = 'Salvo com sucesso.';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.status === 403 ? 'Sem permissão para editar.' : 'Erro ao salvar.';
      }
    });
  }

  // --- Cap Table ---

  openCapTableEdit() {
    this.capTableForm = this.capTable.map(e => ({ ...e }));
    if (this.capTableForm.length === 0) this.addCapEntry();
    this.activeTab = 'captable';
  }

  addCapEntry() {
    this.capTableForm.push({ name: '', type: 'Founder', percentage: null });
  }

  removeCapEntry(i: number) {
    this.capTableForm.splice(i, 1);
  }

  saveCapTable() {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.savingCapTable = true;
    this.startupService.saveCapTable(this.startupId, user.id, this.capTableForm).subscribe({
      next: (saved) => { this.capTable = saved; this.capTableForm = []; this.savingCapTable = false; },
      error: () => { this.savingCapTable = false; }
    });
  }

  get capTableTotal(): number {
    return this.capTableForm.reduce((sum, e) => sum + (Number(e.percentage) || 0), 0);
  }

  // --- Riscos ---

  openRisksEdit() {
    this.risksForm = this.risks.map(r => ({ ...r }));
    if (this.risksForm.length === 0) this.addRisk();
    this.activeTab = 'risks';
  }

  addRisk() {
    this.risksForm.push({ title: '', level: 'medio', mitigation: '' });
  }

  removeRisk(i: number) {
    this.risksForm.splice(i, 1);
  }

  saveRisks() {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.savingRisks = true;
    this.startupService.saveRisks(this.startupId, user.id, this.risksForm).subscribe({
      next: (saved) => { this.risks = saved; this.risksForm = []; this.savingRisks = false; },
      error: () => { this.savingRisks = false; }
    });
  }

  // --- Nichos ---

  buildNicheDataMap() {
    this.nicheDataMap = {};
    this.nicheFormsMap = {};
    for (const nd of this.nicheData) {
      try {
        this.nicheDataMap[nd.nicheKey] = JSON.parse(nd.fieldValuesJson || '{}');
      } catch {
        this.nicheDataMap[nd.nicheKey] = {};
      }
    }
    // Garante que todos os nichos selecionados tenham um mapa inicializado
    for (const nk of (this.startup?.selectedNiches || [])) {
      if (!this.nicheDataMap[nk]) this.nicheDataMap[nk] = {};
      this.nicheFormsMap[nk] = { ...this.nicheDataMap[nk] };
    }
  }

  get capTableViewTotal(): number {
    return this.capTable.reduce((sum, e) => sum + (Number(e.percentage) || 0), 0);
  }

  getNicheFieldsConfig(nicheKey: string): any[] {
    const niche = this.availableNiches.find(n => n.key === nicheKey);
    if (!niche) return [];
    try { return JSON.parse(niche.fieldsJson || '[]'); } catch { return []; }
  }

  getNicheValue(nicheKey: string, fieldKey: string): string {
    return this.nicheDataMap[nicheKey]?.[fieldKey] ?? '';
  }

  saveNicheData(nicheKey: string) {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    const values = this.nicheFormsMap[nicheKey] || {};
    this.startupService.saveNicheData(this.startupId, nicheKey, user.id, values).subscribe(saved => {
      this.nicheDataMap[nicheKey] = JSON.parse(saved.fieldValuesJson || '{}');
      this.nicheFormsMap[nicheKey] = { ...this.nicheDataMap[nicheKey] };
    });
  }

  isNicheSelected(key: string): boolean {
    return (this.startupForm.selectedNiches || this.startup?.selectedNiches || []).includes(key);
  }

  toggleNiche(key: string) {
    if (!this.startupForm.selectedNiches) {
      this.startupForm.selectedNiches = [...(this.startup?.selectedNiches || [])];
    }
    const idx = this.startupForm.selectedNiches.indexOf(key);
    if (idx >= 0) this.startupForm.selectedNiches.splice(idx, 1);
    else this.startupForm.selectedNiches.push(key);
  }

  // --- Membros ---

  searchInviteUsers() {
    const query = this.memberSearchQuery.trim();
    this.inviteError = ''; this.inviteSuccess = '';
    if (!query) { this.inviteResults = []; return; }
    const currentUser = this.authService.getCurrentUser();
    this.userService.searchUsers(query).subscribe(users => {
      this.inviteResults = users.filter(u => u.id !== currentUser?.id);
    });
  }

  inviteMember(user: any) {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) { this.inviteError = 'Não autenticado.'; return; }
    this.inviteLoading = true; this.inviteError = ''; this.inviteSuccess = '';
    this.startupService.inviteMember(this.startupId, currentUser.id, user.id, this.inviteRole, this.inviteMessage).subscribe({
      next: () => {
        this.inviteLoading = false; this.inviteSuccess = 'Convite enviado.';
        this.memberSearchQuery = ''; this.inviteMessage = ''; this.inviteResults = [];
      },
      error: (err) => {
        this.inviteLoading = false;
        this.inviteError = err?.error?.error || 'Erro ao enviar convite.';
      }
    });
  }

  canManageMembers(): boolean { return this.currentStartupRole === 'Owner'; }

  updateMemberRole(member: any, role: string) {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.startupService.updateMemberRole(this.startupId, member.userId, user.id, role).subscribe(u => { member.role = u.role; });
  }

  removeMember(member: any) {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.startupService.removeMember(this.startupId, member.userId, user.id).subscribe(() => {
      this.members = this.members.filter(m => m.userId !== member.userId);
    });
  }

  // --- Score ---

  get startupScore(): number {
    if (!this.startup) return 0;
    let s = 30;
    if (this.startup.pitch || this.startup.shortDescription) s += 5;
    if (this.startup.problemDescription) s += 5;
    if (this.startup.solutionDescription) s += 5;
    if (this.startup.mrr || this.startup.monthlyRevenue) s += 10;
    if (this.startup.arr || this.startup.annualRevenue) s += 5;
    if (this.startup.cac && this.startup.ltv) s += 5;
    if (this.startup.runway) s += 5;
    if (this.startup.tam && this.startup.sam) s += 5;
    if (this.startup.roundStatus) s += 5;
    if (this.followCount > 2) s += 5;
    if (this.recentPosts.length > 0) s += 5;
    if (this.capTable.length > 0) s += 3;
    if (this.risks.length > 0) s += 2;
    return Math.min(100, s);
  }

  riskColor(level: string): string {
    if (level === 'alto') return 'text-red-600 bg-red-50 border-red-200';
    if (level === 'medio') return 'text-amber-600 bg-amber-50 border-amber-200';
    return 'text-emerald-600 bg-emerald-50 border-emerald-200';
  }

  riskLabel(level: string): string {
    if (level === 'alto') return 'Alto';
    if (level === 'medio') return 'Médio';
    return 'Baixo';
  }

  // --- Helpers ---

  private buildForm(s: any) {
    return {
      name: s?.name ?? '',
      logo: s?.logo ?? '',
      pitch: s?.pitch ?? s?.shortDescription ?? '',
      shortDescription: s?.shortDescription ?? '',
      description: s?.description ?? '',
      fullDescription: s?.fullDescription ?? '',
      problemDescription: s?.problemDescription ?? '',
      solutionDescription: s?.solutionDescription ?? '',
      competitiveDifferential: s?.competitiveDifferential ?? '',
      sector: s?.sector ?? '',
      stage: s?.stage ?? '',
      location: s?.location ?? '',
      websiteUrl: s?.websiteUrl ?? '',
      pitchDeckUrl: s?.pitchDeckUrl ?? '',
      foundingYear: s?.foundingYear ?? null,
      tam: s?.tam ?? '',
      sam: s?.sam ?? '',
      som: s?.som ?? '',
      marketSource: s?.marketSource ?? '',
      marketTiming: s?.marketTiming ?? '',
      monthlyRevenue: s?.monthlyRevenue ?? '',
      annualRevenue: s?.annualRevenue ?? '',
      mrr: s?.mrr ?? '',
      arr: s?.arr ?? '',
      cac: s?.cac ?? '',
      ltv: s?.ltv ?? '',
      grossMargin: s?.grossMargin ?? '',
      burnRate: s?.burnRate ?? '',
      runway: s?.runway ?? '',
      usersCount: s?.usersCount ?? null,
      growthPercent: s?.growthPercent ?? '',
      clientsCount: s?.clientsCount ?? null,
      churn: s?.churn ?? '',
      metricsPublic: s?.metricsPublic ?? true,
      roundStatus: s?.roundStatus ?? '',
      roundAmountRaised: s?.roundAmountRaised ?? '',
      roundValuation: s?.roundValuation ?? '',
      roundStructure: s?.roundStructure ?? '',
      roundPercentCommitted: s?.roundPercentCommitted ?? '',
      roundCapitalUse: s?.roundCapitalUse ?? '',
      currentObjective: s?.currentObjective ?? '',
      selectedNiches: [...(s?.selectedNiches ?? [])],
      fieldVisibilityJson: s?.fieldVisibilityJson ?? '{}',
    };
  }
}
