import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SearchService } from '../services/search.service';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';
import { ProposalService } from '../services/proposal.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-co-founder-match',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './co-founder-match.component.html'
})
export class CoFounderMatchComponent implements OnInit {
  activeTab: 'founders' | 'startups' = 'founders';
  founders: any[] = [];
  startups: any[] = [];
  loadingFounders = false;
  loadingStartups = false;
  areaFilter = '';
  currentUser: any;
  userStartups: any[] = [];

  showProposalModal = false;
  proposalTarget: any = null;
  proposalType: 'founder' | 'startup' = 'founder';
  proposal: any = {};
  sendingProposal = false;
  proposalSent = false;

  areas = [
    { value: '', label: 'Todas as áreas' },
    { value: 'tech', label: 'Tech' },
    { value: 'business', label: 'Business' },
    { value: 'produto', label: 'Produto' },
    { value: 'marketing', label: 'Marketing' },
    { value: 'vendas', label: 'Vendas' },
    { value: 'operacao', label: 'Operação' }
  ];

  constructor(
    private searchService: SearchService,
    private startupService: StartupService,
    private authService: AuthService,
    private proposalService: ProposalService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.userService.getUserStartups(this.currentUser.id).subscribe(s => { this.userStartups = s; });
    }
    this.loadFounders();
    this.loadStartups();
  }

  loadFounders() {
    this.loadingFounders = true;
    this.searchService.searchCoFounders(this.areaFilter).subscribe({
      next: data => { this.founders = data.filter(u => u.id !== this.currentUser?.id); this.loadingFounders = false; },
      error: () => { this.loadingFounders = false; }
    });
  }

  loadStartups() {
    this.loadingStartups = true;
    this.startupService.getStartups().subscribe({
      next: data => {
        // Filter to startups that have a co-founder description or are seeking
        this.startups = data.slice(0, 6);
        this.loadingStartups = false;
      },
      error: () => { this.loadingStartups = false; }
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

  openProposalModal(target: any, type: 'founder' | 'startup') {
    this.proposalTarget = target;
    this.proposalType = type;
    this.proposal = { startupId: '', expectedRole: '', desiredProfile: '', expectedDedication: '', equityOffer: '', pitch: '' };
    this.proposalSent = false;
    this.showProposalModal = true;
  }

  closeModal() { this.showProposalModal = false; }

  sendProposal() {
    if (!this.proposal.pitch?.trim() || !this.currentUser) return;
    const selectedStartup = this.userStartups.find(s => String(s.startupId) === String(this.proposal.startupId));
    this.sendingProposal = true;

    const receiverId = this.proposalType === 'founder'
      ? this.proposalTarget.id
      : this.proposalTarget.ownerId || this.proposalTarget.id;

    this.proposalService.sendProposal({
      senderId: this.currentUser.id,
      receiverId,
      type: 'CO_FOUNDER',
      startupId: selectedStartup?.startupId,
      startupName: selectedStartup?.name,
      pitch: this.proposal.pitch,
      expectedRole: this.proposal.expectedRole,
      desiredProfile: this.proposal.desiredProfile,
      expectedDedication: this.proposal.expectedDedication,
      equityOffer: this.proposal.equityOffer
    }).subscribe({
      next: () => { this.proposalSent = true; this.sendingProposal = false; },
      error: () => { this.sendingProposal = false; }
    });
  }

  getInitial(name: string): string { return (name || 'U').charAt(0).toUpperCase(); }

  splitTags(value: string): string[] {
    if (!value) return [];
    return value.split(',').map(t => t.trim()).filter(Boolean);
  }
}
