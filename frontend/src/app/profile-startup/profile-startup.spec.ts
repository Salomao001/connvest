import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { ProfileStartup } from './profile-startup';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';

describe('ProfileStartup - US-005/US-006', () => {
  let fixture: ComponentFixture<ProfileStartup>;
  let component: ProfileStartup;
  let mockStartupService: any;
  let mockUserService: any;
  let mockAuthService: any;

  beforeEach(async () => {
    mockStartupService = {
      getStartup: vi.fn().mockReturnValue(of({
        id: 11,
        name: 'FinPulse',
        logo: 'https://example.com/logo.png',
        shortDescription: 'Resumo da FinPulse.',
        sector: 'Fintech',
        stage: 'Seed',
        description: 'Tesouraria B2B com IA.',
        fullDescription: 'Descricao completa da FinPulse.',
        location: 'Sao Paulo, SP',
        websiteUrl: 'https://finpulse.example.com',
        currentObjective: 'Captar Seed',
        mainMetrics: 'R$ 180k MRR',
        monthlyRevenue: 'R$ 180k',
        usersCount: 1200,
        growthPercent: '+32%',
        clientsCount: 42,
        mrr: 'R$ 180k',
        churn: '2.1%',
        metricsPublic: true,
        updatedAt: '2026-05-06T10:00:00',
        badges: 'Top Growth'
      })),
      updateStartup: vi.fn(),
      getStartupMembers: vi.fn().mockReturnValue(of([
        { userId: 4, name: 'Founder', email: 'founder@email.com', role: 'Owner' },
        { userId: 20, name: 'Ana Advisor', email: 'ana@email.com', role: 'Editor' }
      ])),
      inviteMember: vi.fn(),
      updateMemberRole: vi.fn(),
      removeMember: vi.fn()
    };

    mockAuthService = {
      getCurrentUser: vi.fn().mockReturnValue({ id: 4, name: 'Founder', email: 'founder@email.com' })
    };

    mockUserService = {
      getUserStartups: vi.fn().mockReturnValue(of([
        { startupId: 11, name: 'FinPulse', role: 'Owner' }
      ])),
      searchUsers: vi.fn().mockReturnValue(of([
        { id: 20, name: 'Ana Advisor', email: 'ana@email.com' }
      ]))
    };

    await TestBed.configureTestingModule({
      imports: [ProfileStartup],
      providers: [
        { provide: StartupService, useValue: mockStartupService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: UserService, useValue: mockUserService },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: vi.fn().mockReturnValue('11') } } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileStartup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve carregar a startup pelo id da rota', () => {
    expect(mockStartupService.getStartup).toHaveBeenCalledWith(11, 4);
    expect(fixture.nativeElement.textContent).toContain('FinPulse');
    expect(fixture.nativeElement.textContent).toContain('Resumo da FinPulse.');
    expect(fixture.nativeElement.textContent).toContain('Descricao completa da FinPulse.');
    expect(fixture.nativeElement.textContent).toContain('R$ 180k MRR');
    expect(fixture.nativeElement.textContent).toContain('Receita mensal');
    expect(fixture.nativeElement.textContent).toContain('R$ 180k');
    expect(fixture.nativeElement.textContent).toContain('1200');
    expect(fixture.nativeElement.textContent).toContain('+32%');
    expect(fixture.nativeElement.textContent).toContain('Ultima atualizacao');
    expect(component.canEdit).toBe(true);
    expect(fixture.nativeElement.querySelector('[data-testid="edit-startup"]')).toBeTruthy();
    expect(mockStartupService.getStartupMembers).toHaveBeenCalledWith(11);
    expect(fixture.nativeElement.textContent).toContain('Founder');
    expect(fixture.nativeElement.textContent).toContain('Owner');
    expect(fixture.nativeElement.querySelector('[data-testid="member-role-select"]')).toBeTruthy();
  });

  it('deve salvar edicao quando usuario e Owner', () => {
    mockStartupService.updateStartup.mockReturnValue(of({
      id: 11,
      name: 'FinPulse Editada',
      sector: 'Healthtech',
      stage: 'Tracao',
      description: 'Descricao editada',
      updatedAt: '2026-05-06T11:00:00'
    }));

    component.startEditing();
    component.startupForm = {
      name: ' FinPulse Editada ',
      logo: '',
      shortDescription: 'Resumo editado',
      description: 'Descricao editada',
      fullDescription: 'Descricao completa editada',
      sector: 'Healthtech',
      stage: 'Tracao',
      location: 'Rio de Janeiro, RJ',
      websiteUrl: 'https://editada.example.com',
      currentObjective: 'Escalar vendas',
      mainMetrics: 'R$ 250k MRR',
      monthlyRevenue: 'R$ 250k',
      usersCount: 2400,
      growthPercent: '+21%',
      clientsCount: 70,
      mrr: 'R$ 250k',
      churn: '1.9%',
      metricsPublic: false
    };

    component.saveStartup();

    expect(mockStartupService.updateStartup).toHaveBeenCalledWith(11, 4, {
      name: 'FinPulse Editada',
      logo: '',
      shortDescription: 'Resumo editado',
      description: 'Descricao editada',
      fullDescription: 'Descricao completa editada',
      sector: 'Healthtech',
      stage: 'Tracao',
      location: 'Rio de Janeiro, RJ',
      websiteUrl: 'https://editada.example.com',
      currentObjective: 'Escalar vendas',
      mainMetrics: 'R$ 250k MRR',
      monthlyRevenue: 'R$ 250k',
      usersCount: 2400,
      growthPercent: '+21%',
      clientsCount: 70,
      mrr: 'R$ 250k',
      churn: '1.9%',
      metricsPublic: false
    });
    expect(component.editing).toBe(false);
    expect(component.startup.name).toBe('FinPulse Editada');
  });

  it('nao deve salvar startup sem nome', () => {
    component.startEditing();
    component.startupForm.name = '   ';

    component.saveStartup();

    expect(component.errorMessage).toBe('Nome da startup obrigatorio.');
    expect(mockStartupService.updateStartup).not.toHaveBeenCalled();
  });

  it('nao deve exibir botao de edicao quando usuario nao tem permissao', () => {
    component.canEdit = false;
    component.currentStartupRole = 'Viewer';
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="edit-startup"]')).toBeFalsy();
    expect(fixture.nativeElement.querySelector('[data-testid="invite-member-panel"]')).toBeFalsy();
    expect(fixture.nativeElement.querySelector('[data-testid="member-role-select"]')).toBeFalsy();
  });

  it('deve informar quando metricas privadas nao vierem para visitante', () => {
    component.canEdit = false;
    component.startup = {
      ...component.startup,
      mainMetrics: null,
      monthlyRevenue: null,
      usersCount: null,
      growthPercent: null,
      clientsCount: null,
      mrr: null,
      churn: null,
      metricsPublic: false
    };

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Metricas privadas.');
    expect(fixture.nativeElement.textContent).not.toContain('Receita mensal');
  });

  it('deve buscar usuario e enviar convite de membro como Owner', () => {
    mockStartupService.inviteMember.mockReturnValue(of({
      id: 30,
      status: 'PENDING'
    }));

    component.memberSearchQuery = 'ana@email.com';
    component.inviteRole = 'Advisor';
    component.inviteMessage = 'Vamos colaborar na startup.';

    component.searchInviteUsers();
    component.inviteMember(component.inviteResults[0]);

    expect(mockUserService.searchUsers).toHaveBeenCalledWith('ana@email.com');
    expect(mockStartupService.inviteMember).toHaveBeenCalledWith(
      11,
      4,
      20,
      'Advisor',
      'Vamos colaborar na startup.'
    );
    expect(component.inviteSuccess).toBe('Convite enviado com sucesso.');
    expect(component.inviteResults.length).toBe(0);
  });

  it('deve permitir Owner alterar papel de membro', () => {
    mockStartupService.updateMemberRole.mockReturnValue(of({
      userId: 20,
      role: 'Viewer'
    }));

    const member = component.members.find(item => item.userId === 20);
    component.updateMemberRole(member, 'Viewer');

    expect(mockStartupService.updateMemberRole).toHaveBeenCalledWith(11, 20, 4, 'Viewer');
    expect(member.role).toBe('Viewer');
  });

  it('deve permitir Owner remover membro', () => {
    mockStartupService.removeMember.mockReturnValue(of({ message: 'Membro removido com sucesso.' }));

    const member = component.members.find(item => item.userId === 20);
    component.removeMember(member);

    expect(mockStartupService.removeMember).toHaveBeenCalledWith(11, 20, 4);
    expect(component.members.some(item => item.userId === 20)).toBe(false);
  });
});
