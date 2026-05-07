import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './onboarding.component.html',
  styleUrls: ['./onboarding.component.scss']
})
export class OnboardingComponent {
  profileOptions = [
    { id: 'Founder', label: 'Founder', icon: '🚀', description: 'Tenho uma startup ou um projeto inovador.' },
    { id: 'Investidor', label: 'Investidor', icon: '💰', description: 'Busco oportunidades para investir capital.' },
    { id: 'Advisor', label: 'Advisor', icon: '🧠', description: 'Quero mentorar startups e compartilhar minha experiência.' },
    { id: 'Buscando co-founder', label: 'Buscando co-founder', icon: '🤝', description: 'Procuro um parceiro para tirar uma ideia do papel.' },
    { id: 'Outro', label: 'Outro', icon: '✨', description: 'Explorando novas conexões no ecossistema.' }
  ];

  selectedIds: string[] = [];
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  toggleSelection(id: string) {
    const index = this.selectedIds.indexOf(id);
    if (index > -1) {
      this.selectedIds.splice(index, 1);
    } else {
      this.selectedIds.push(id);
    }
  }

  isSelected(id: string): boolean {
    return this.selectedIds.includes(id);
  }

  save() {
    if (this.selectedIds.length === 0) return;

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.authService.updateProfileTypes(user.id, this.selectedIds).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Erro ao salvar perfis:', err);
        this.loading = false;
      }
    });
  }
}
