import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { ProposalService } from '../services/proposal.service';
import { UserService } from '../services/user.service';

const SEARCH_BASE = 'http://localhost:8080/api/search';

@Component({
  selector: 'app-investors',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './investors.component.html'
})
export class InvestorsComponent implements OnInit {
  investors: any[] = [];
  loading = false;
  query = '';
  filterType = '';
  filterSector = '';
  filterStage = '';
  currentUser: any;
  userStartups: any[] = [];

  showProposalModal = false;
  selectedInvestor: any = null;
  proposal: any = {};
  sendingProposal = false;
  proposalSent = false;

  investorTypes = ['angel', 'VC', 'scout', 'advisor'];
  sectors = ['Fintech', 'Healthtech', 'AgriTech', 'Edtech', 'B2B SaaS', 'DeepTech', 'PropTech', 'E-commerce'];
  stages = ['Pre-seed', 'Seed', 'Series A', 'Series B'];

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private proposalService: ProposalService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.userService.getUserStartups(this.currentUser.id).subscribe(s => { this.userStartups = s; });
    }
    this.search();
  }

  search() {
    this.loading = true;
    let url = `${SEARCH_BASE}/investors?q=${this.query}&type=${this.filterType}&sector=${this.filterSector}&stage=${this.filterStage}`;
    this.http.get<any[]>(url).subscribe({
      next: data => { this.investors = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  get profileCompleteness(): number {
    if (!this.currentUser) return 0;
    const fields = ['bio', 'location', 'mainSkills', 'photo'];
    const filled = fields.filter(f => !!this.currentUser[f]).length;
    return Math.round((filled / fields.length) * 100);
  }

  isAdvisor(investor: any): boolean {
    return investor?.investorType?.toLowerCase() === 'advisor';
  }

  openProposalModal(investor: any) {
    this.selectedInvestor = investor;
    this.proposal = { startupId: '', pitch: '', seekedValue: '', capitalUse: '', whyThisInvestor: '', supportArea: '' };
    this.proposalSent = false;
    this.showProposalModal = true;
  }

  closeModal() { this.showProposalModal = false; this.selectedInvestor = null; }

  sendProposal() {
    if (!this.proposal.pitch?.trim() || !this.currentUser) return;
    const selectedStartup = this.userStartups.find(s => String(s.startupId) === String(this.proposal.startupId));
    this.sendingProposal = true;
    const proposalType = this.isAdvisor(this.selectedInvestor) ? 'ADVISOR' : 'INVESTMENT';
    this.proposalService.sendProposal({
      senderId: this.currentUser.id,
      receiverId: this.selectedInvestor.id,
      type: proposalType,
      startupId: selectedStartup?.startupId,
      startupName: selectedStartup?.name,
      pitch: this.proposal.pitch,
      seekedValue: this.proposal.seekedValue,
      capitalUse: this.proposal.capitalUse,
      whyThisInvestor: this.proposal.whyThisInvestor,
      supportArea: this.proposal.supportArea
    }).subscribe({
      next: () => { this.proposalSent = true; this.sendingProposal = false; },
      error: () => { this.sendingProposal = false; }
    });
  }

  getInitial(name: string): string { return (name || 'I').charAt(0).toUpperCase(); }

  splitTags(value: string): string[] {
    if (!value) return [];
    return value.split(',').map(t => t.trim()).filter(Boolean);
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
}
