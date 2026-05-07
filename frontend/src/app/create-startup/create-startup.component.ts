import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { StartupService } from '../services/startup.service';

@Component({
  selector: 'app-create-startup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-startup.component.html',
  styleUrl: './create-startup.component.scss'
})
export class CreateStartupComponent {
  startupForm: any = {
    name: '',
    logo: '',
    shortDescription: '',
    description: '',
    sector: '',
    stage: '',
    location: '',
    websiteUrl: '',
    currentObjective: '',
    mainMetrics: '',
    monthlyRevenue: '',
    usersCount: null,
    growthPercent: '',
    clientsCount: null,
    mrr: '',
    churn: '',
    metricsPublic: true
  };

  loading = false;
  errorMessage = '';

  constructor(
    private startupService: StartupService,
    private authService: AuthService,
    private router: Router
  ) {}

  createStartup() {
    if (!this.startupForm.name?.trim()) {
      this.errorMessage = 'Nome da startup obrigatorio.';
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.startupService.createStartup(user.id, {
      ...this.startupForm,
      name: this.startupForm.name.trim()
    }).subscribe({
      next: (created) => {
        this.loading = false;
        this.router.navigate(['/startup', created.id]);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Nao foi possivel criar a startup.';
      }
    });
  }
}
