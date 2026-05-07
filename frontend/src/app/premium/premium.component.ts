import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-premium',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './premium.component.html'
})
export class PremiumComponent {
  plans = [
    {
      name: 'Starter',
      price: 'Grátis',
      features: ['5 conexões/mês', 'Perfil básico', 'Feed público', 'Propostas recebidas'],
      cta: 'Plano atual',
      disabled: true
    },
    {
      name: 'Pro Founder',
      price: 'R$ 49/mês',
      features: ['Propostas ilimitadas', 'Destaque nos rankings', 'Analytics da startup', 'Impulsionar posts', 'Filtros avançados de busca', 'Badge verificado'],
      cta: 'Começar Pro',
      disabled: false,
      highlight: true
    },
    {
      name: 'Pro Investor',
      price: 'R$ 79/mês',
      features: ['Tudo do Pro Founder', 'Pipeline ilimitado', 'Analytics de investidor', 'Notificações de novas startups', 'Acesso a dados avançados', 'Suporte prioritário'],
      cta: 'Começar Investor',
      disabled: false
    }
  ];

  showComingSoon = false;

  subscribe(plan: any) {
    if (!plan.disabled) {
      this.showComingSoon = true;
    }
  }
}
