import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { ConnectionService } from '../services/connection.service';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';
import { ProposalService } from '../services/proposal.service';

@Component({
  selector: 'app-proposals',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './proposals.component.html',
  styleUrl: './proposals.component.scss'
})
export class ProposalsComponent implements OnInit {
  activeTab: 'received' | 'sent' = 'received';

  proposals: any[] = [];
  sentProposals: any[] = [];
  startupInvitations: any[] = [];
  loading = true;

  constructor(
    private connectionService: ConnectionService,
    private startupService: StartupService,
    private authService: AuthService,
    private proposalService: ProposalService
  ) {}

  ngOnInit() { this.loadAll(); }

  loadAll() {
    const user = this.authService.getCurrentUser();
    if (!user) { this.loading = false; return; }

    forkJoin({
      legacy: this.connectionService.getProposals(user.id),
      invitations: this.startupService.getStartupInvitationsForUser(user.id),
      received: this.proposalService.getReceived(user.id),
      sent: this.proposalService.getSent(user.id)
    }).subscribe(data => {
      this.proposals = [...data.received, ...data.legacy];
      this.sentProposals = data.sent;
      this.startupInvitations = data.invitations;
      this.loading = false;
    });
  }

  get receivedCount() { return this.proposals.length + this.startupInvitations.length; }
  get pendingCount() { return this.proposals.filter(p => p.status === 'PENDING').length + this.startupInvitations.filter(i => i.status === 'PENDING').length; }

  updateStatus(id: number, status: string, isLegacy = false) {
    if (isLegacy) {
      this.connectionService.updateStatus(id, status).subscribe(() => this.loadAll());
    } else {
      this.proposalService.updateStatus(id, status).subscribe(() => this.loadAll());
    }
  }

  acceptInvitation(id: number) {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.startupService.acceptStartupInvitation(id, user.id).subscribe(() => this.loadAll());
  }

  rejectInvitation(id: number) {
    const user = this.authService.getCurrentUser();
    if (!user) return;
    this.startupService.rejectStartupInvitation(id, user.id).subscribe(() => this.loadAll());
  }

  typeLabel(type: string): string {
    if (type === 'INVESTMENT') return 'Investimento';
    if (type === 'CO_FOUNDER') return 'Co-founder';
    if (type === 'ADVISOR') return 'Advisor';
    return 'Proposta';
  }

  typeColor(type: string): string {
    if (type === 'INVESTMENT') return 'bg-emerald-700';
    if (type === 'CO_FOUNDER') return 'bg-sky-700';
    if (type === 'ADVISOR') return 'bg-violet-700';
    return 'bg-blue-700';
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = { PENDING: 'Pendente', ACCEPTED: 'Aceito', REJECTED: 'Recusado', ARCHIVED: 'Arquivado', VIEWED: 'Visualizado' };
    return map[status] || status;
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
}
