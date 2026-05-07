import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SearchService } from '../services/search.service';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';
import { SavedStartupService } from '../services/saved-startup.service';
import { FollowService } from '../services/follow.service';

@Component({
  selector: 'app-discover',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './discover.component.html',
  styleUrl: './discover.component.scss'
})
export class DiscoverComponent implements OnInit {
  activeTab: 'startups' | 'people' = 'startups';
  searchQuery = '';
  users: any[] = [];
  startups: any[] = [];
  coFounderUsers: any[] = [];
  coFounderArea = '';
  stageFilter = '';
  loading = false;
  loadingCoFounders = false;
  loadingStartups = false;
  currentUser: any;
  savedStartupIds = new Set<number>();
  followingIds = new Set<string>();

  startupStages = [
    { value: '', label: 'Todos os estágios' },
    { value: 'Ideia', label: 'Ideia' },
    { value: 'MVP', label: 'MVP' },
    { value: 'Primeiros usuários', label: 'Primeiros usuários' },
    { value: 'Receita inicial', label: 'Receita inicial' },
    { value: 'Tração', label: 'Tração' },
    { value: 'Escala', label: 'Escala' }
  ];

  coFounderAreas = [
    { value: '', label: 'Todas as áreas' },
    { value: 'tech', label: 'Tech' },
    { value: 'business', label: 'Business' },
    { value: 'produto', label: 'Produto' },
    { value: 'marketing', label: 'Marketing' },
    { value: 'vendas', label: 'Vendas' },
    { value: 'operacao', label: 'Operação' }
  ];

  constructor(
    private route: ActivatedRoute,
    private searchService: SearchService,
    private startupService: StartupService,
    private authService: AuthService,
    private savedService: SavedStartupService,
    private followService: FollowService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.loadSavedIds();
    }
    this.route.queryParams.subscribe(params => {
      this.searchQuery = params['q'] || '';
      if (this.searchQuery) {
        this.performSearch();
      } else {
        this.loadStartups();
        this.loadCoFounders();
      }
    });
  }

  loadSavedIds() {
    this.savedService.getSaved(this.currentUser.id).subscribe(items => {
      this.savedStartupIds = new Set(items.map((i: any) => i.savedStartup?.startupId));
    });
  }

  performSearch() {
    this.loading = true;
    this.searchService.search(this.searchQuery).subscribe({
      next: (data) => {
        this.users = data.users;
        this.startups = this.filterByStage(data.startups);
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  loadStartups() {
    this.loadingStartups = true;
    this.startupService.getStartups(this.stageFilter).subscribe({
      next: (startups) => { this.startups = startups; this.loadingStartups = false; },
      error: () => { this.loadingStartups = false; }
    });
  }

  loadCoFounders() {
    this.loadingCoFounders = true;
    this.searchService.searchCoFounders(this.coFounderArea).subscribe({
      next: (users) => { this.coFounderUsers = users; this.loadingCoFounders = false; },
      error: () => { this.loadingCoFounders = false; }
    });
  }

  onStageChange() { this.searchQuery ? this.performSearch() : this.loadStartups(); }
  onCoFounderAreaChange() { this.loadCoFounders(); }

  toggleSave(startup: any, event: Event) {
    event.preventDefault();
    event.stopPropagation();
    if (!this.currentUser) return;
    if (this.savedStartupIds.has(startup.id)) {
      this.savedService.unsave(this.currentUser.id, startup.id).subscribe(() => {
        this.savedStartupIds.delete(startup.id);
      });
    } else {
      this.savedService.save(this.currentUser.id, startup.id).subscribe(() => {
        this.savedStartupIds.add(startup.id);
      });
    }
  }

  isSaved(startupId: number): boolean { return this.savedStartupIds.has(startupId); }

  followUser(userId: number, event: Event) {
    event.preventDefault();
    if (!this.currentUser) return;
    this.followService.toggleFollow(this.currentUser.id, 'USER', userId).subscribe(result => {
      const key = `USER_${userId}`;
      if (result.following) this.followingIds.add(key);
      else this.followingIds.delete(key);
    });
  }

  isFollowing(type: string, id: number): boolean { return this.followingIds.has(`${type}_${id}`); }

  private filterByStage(startups: any[]) {
    return this.stageFilter ? startups.filter(s => s.stage === this.stageFilter) : startups;
  }

  logoColor(name: string): string {
    const colors = ['bg-blue-700', 'bg-emerald-600', 'bg-teal-600', 'bg-orange-500', 'bg-violet-600'];
    return colors[(name?.charCodeAt(0) || 0) % colors.length];
  }
}
