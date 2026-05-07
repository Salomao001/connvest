import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SavedStartupService } from '../services/saved-startup.service';
import { AuthService } from '../services/auth.service';

const STAGES = [
  { key: 'INTERESTING', label: 'Interessante', color: 'border-slate-300' },
  { key: 'IN_CONVERSATION', label: 'Em conversa', color: 'border-blue-500' },
  { key: 'EVALUATING', label: 'Avaliando', color: 'border-amber-500' },
  { key: 'ARCHIVED', label: 'Arquivada', color: 'border-slate-400' }
];

@Component({
  selector: 'app-pipeline',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pipeline.component.html'
})
export class PipelineComponent implements OnInit {
  stages = STAGES;
  savedItems: any[] = [];
  loading = true;
  currentUser: any;

  constructor(
    private savedService: SavedStartupService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) this.load();
  }

  load() {
    this.loading = true;
    this.savedService.getSaved(this.currentUser.id).subscribe(data => {
      this.savedItems = data;
      this.loading = false;
    });
  }

  getItemsForStage(stageKey: string): any[] {
    return this.savedItems.filter(item => item.savedStartup?.pipelineStage === stageKey);
  }

  moveToStage(item: any, stage: string) {
    this.savedService.updatePipeline(item.savedStartup.id, stage).subscribe(() => {
      item.savedStartup.pipelineStage = stage;
    });
  }

  remove(item: any) {
    const startupId = item.savedStartup?.startupId;
    if (!startupId) return;
    this.savedService.unsave(this.currentUser.id, startupId).subscribe(() => {
      this.savedItems = this.savedItems.filter(i => i.savedStartup?.id !== item.savedStartup?.id);
    });
  }

  logoColor(name: string): string {
    const colors = ['bg-blue-700', 'bg-emerald-600', 'bg-teal-600', 'bg-orange-500', 'bg-violet-600'];
    return colors[(name?.charCodeAt(0) || 0) % colors.length];
  }
}
