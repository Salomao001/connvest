import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { RankingService } from '../services/ranking.service';

@Component({
  selector: 'app-rankings',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './rankings.component.html'
})
export class RankingsComponent implements OnInit {
  activeTab: 'performance' | 'popularity' | 'trending' = 'performance';
  startups: any[] = [];
  loading = false;

  constructor(private rankingService: RankingService) {}

  ngOnInit() { this.loadTab('performance'); }

  loadTab(tab: 'performance' | 'popularity' | 'trending') {
    this.activeTab = tab;
    this.loading = true;
    const obs = tab === 'performance'
      ? this.rankingService.getPerformance()
      : tab === 'popularity'
      ? this.rankingService.getPopularity()
      : this.rankingService.getTrending();

    obs.subscribe({
      next: data => { this.startups = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  badgeClass(badge: string): string {
    if (!badge) return 'bg-slate-100 text-slate-500';
    if (badge === 'Top Growth') return 'bg-emerald-100 text-emerald-700';
    if (badge === 'Most Consistent') return 'bg-blue-100 text-blue-700';
    if (badge === 'Trending') return 'bg-orange-100 text-orange-700';
    if (badge === 'Popular') return 'bg-rose-100 text-rose-700';
    return 'bg-slate-100 text-slate-600';
  }

  rankColor(rank: number): string {
    if (rank === 1) return 'bg-blue-700 text-white';
    if (rank === 2) return 'bg-slate-200 text-slate-700';
    if (rank === 3) return 'bg-amber-100 text-amber-700';
    return 'bg-slate-100 text-slate-500';
  }

  logoColor(name: string): string {
    const colors = ['bg-blue-700', 'bg-emerald-600', 'bg-teal-600', 'bg-orange-500', 'bg-violet-600', 'bg-rose-500'];
    return colors[(name?.charCodeAt(0) || 0) % colors.length];
  }
}
