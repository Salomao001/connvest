import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { CreateStartupComponent } from './create-startup.component';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';

describe('CreateStartupComponent - US-006', () => {
  let component: CreateStartupComponent;
  let fixture: ComponentFixture<CreateStartupComponent>;
  let mockStartupService: any;
  let mockAuthService: any;
  let router: Router;

  beforeEach(async () => {
    mockStartupService = {
      createStartup: vi.fn()
    };

    mockAuthService = {
      getCurrentUser: vi.fn().mockReturnValue({ id: 4, name: 'Founder', email: 'founder@email.com' })
    };

    await TestBed.configureTestingModule({
      imports: [CreateStartupComponent],
      providers: [
        provideRouter([]),
        { provide: StartupService, useValue: mockStartupService },
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateStartupComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('deve criar startup com campos completos e navegar para perfil criado', () => {
    mockStartupService.createStartup.mockReturnValue(of({ id: 12, name: 'Nova Startup' }));

    component.startupForm = {
      name: ' Nova Startup ',
      logo: 'https://example.com/logo.png',
      shortDescription: 'Resumo',
      description: 'Descricao curta',
      fullDescription: 'Descricao completa',
      sector: 'SaaS',
      stage: 'MVP',
      location: 'Sao Paulo, SP',
      websiteUrl: 'https://nova.example.com',
      currentObjective: 'Validar ICP',
      mainMetrics: '10 pilotos',
      monthlyRevenue: 'R$ 40k',
      usersCount: 800,
      growthPercent: '+15%',
      clientsCount: 20,
      mrr: 'R$ 40k',
      churn: '3%',
      metricsPublic: true
    };

    component.createStartup();

    expect(mockStartupService.createStartup).toHaveBeenCalledWith(4, {
      name: 'Nova Startup',
      logo: 'https://example.com/logo.png',
      shortDescription: 'Resumo',
      description: 'Descricao curta',
      fullDescription: 'Descricao completa',
      sector: 'SaaS',
      stage: 'MVP',
      location: 'Sao Paulo, SP',
      websiteUrl: 'https://nova.example.com',
      currentObjective: 'Validar ICP',
      mainMetrics: '10 pilotos',
      monthlyRevenue: 'R$ 40k',
      usersCount: 800,
      growthPercent: '+15%',
      clientsCount: 20,
      mrr: 'R$ 40k',
      churn: '3%',
      metricsPublic: true
    });
    expect(router.navigate).toHaveBeenCalledWith(['/startup', 12]);
  });

  it('deve permitir criar somente com nome', () => {
    mockStartupService.createStartup.mockReturnValue(of({ id: 13, name: 'Startup Minima' }));
    component.startupForm.name = 'Startup Minima';

    component.createStartup();

    expect(mockStartupService.createStartup).toHaveBeenCalledWith(4, expect.objectContaining({
      name: 'Startup Minima'
    }));
  });

  it('nao deve criar startup sem nome', () => {
    component.startupForm.name = '   ';

    component.createStartup();

    expect(component.errorMessage).toBe('Nome da startup obrigatorio.');
    expect(mockStartupService.createStartup).not.toHaveBeenCalled();
  });

  it('deve exibir erro quando backend falha', () => {
    mockStartupService.createStartup.mockReturnValue(throwError(() => ({ status: 500 })));
    component.startupForm.name = 'Startup com erro';

    component.createStartup();

    expect(component.errorMessage).toBe('Nao foi possivel criar a startup.');
  });
});
