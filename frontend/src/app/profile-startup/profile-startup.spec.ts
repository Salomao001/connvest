import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { ProfileStartup } from './profile-startup';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { FollowService } from '../services/follow.service';
import { SavedStartupService } from '../services/saved-startup.service';
import { PostService } from '../services/post.service';

const MOCK_STARTUP = {
  id: 11,
  name: 'FinPulse',
  logo: 'https://example.com/logo.png',
  shortDescription: 'Resumo da FinPulse.',
  pitch: 'Resumo da FinPulse.',
  sector: 'Fintech',
  stage: 'Seed',
  description: 'Tesouraria B2B com IA.',
  fullDescription: 'Descricao completa da FinPulse.',
  problemDescription: null,
  solutionDescription: null,
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
  badges: 'Top Growth',
  selectedNiches: [],
  fieldVisibilityJson: '{}'
};

describe('ProfileStartup - US-005/US-006', () => {
  let fixture: ComponentFixture<ProfileStartup>;
  let component: ProfileStartup;
  let mockStartupService: any;
  let mockUserService: any;
  let mockAuthService: any;
  let mockFollowService: any;
  let mockSavedService: any;
  let mockPostService: any;

  beforeEach(async () => {
    mockStartupService = {
      getStartup: vi.fn().mockReturnValue(of(MOCK_STARTUP)),
      updateStartup: vi.fn().mockReturnValue(of({ ...MOCK_STARTUP })),
      getStartupMembers: vi.fn().mockReturnValue(of([
        { userId: 4, name: 'Founder', email: 'founder@email.com', role: 'Owner' },
        { userId: 20, name: 'Ana Advisor', email: 'ana@email.com', role: 'Editor' }
      ])),
      getCapTable: vi.fn().mockReturnValue(of([])),
      getRisks: vi.fn().mockReturnValue(of([])),
      getNicheData: vi.fn().mockReturnValue(of([])),
      getNiches: vi.fn().mockReturnValue(of([])),
      inviteMember: vi.fn().mockReturnValue(of({ id: 30, status: 'PENDING' })),
      updateMemberRole: vi.fn().mockReturnValue(of({ userId: 20, role: 'Viewer' })),
      removeMember: vi.fn().mockReturnValue(of({ message: 'Membro removido.' }))
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

    mockFollowService = {
      getStatus: vi.fn().mockReturnValue(of({ following: false, count: 5 })),
      toggleFollow: vi.fn().mockReturnValue(of({ following: true, count: 6 }))
    };

    mockSavedService = {
      checkSaved: vi.fn().mockReturnValue(of({ saved: false })),
      save: vi.fn().mockReturnValue(of({})),
      unsave: vi.fn().mockReturnValue(of({}))
    };

    mockPostService = {
      getPosts: vi.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      imports: [ProfileStartup],
      providers: [
        { provide: StartupService, useValue: mockStartupService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: UserService, useValue: mockUserService },
        { provide: FollowService, useValue: mockFollowService },
        { provide: SavedStartupService, useValue: mockSavedService },
        { provide: PostService, useValue: mockPostService },
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
    expect(fixture.nativeElement.textContent).toContain('R$ 180k');
    expect(fixture.nativeElement.textContent).toContain('+32%');
    expect(component.canEdit).toBe(true);
    expect(fixture.nativeElement.querySelector('[data-testid="edit-startup"]')).toBeTruthy();
    expect(mockStartupService.getStartupMembers).toHaveBeenCalledWith(11);
    expect(component.members.length).toBe(2);
    expect(component.members[0].name).toBe('Founder');
    expect(component.members[0].role).toBe('Owner');
  });

  it('deve exibir membro com select de papel quando usuario e Owner', async () => {
    component.activeTab = 'team';
    fixture.detectChanges();
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('[data-testid="member-role-select"]')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Founder');
    expect(fixture.nativeElement.textContent).toContain('Owner');
  });

  it('deve salvar edicao quando usuario e Owner', () => {
    const updatedStartup = { ...MOCK_STARTUP, name: 'FinPulse Editada' };
    mockStartupService.updateStartup.mockReturnValue(of(updatedStartup));

    component.startEdit('identity');
    component.startupForm.name = ' FinPulse Editada ';
    component.saveStartup();

    expect(mockStartupService.updateStartup).toHaveBeenCalled();
    const [callId, callUserId] = mockStartupService.updateStartup.mock.calls[0];
    expect(callId).toBe(11);
    expect(callUserId).toBe(4);
    expect(component.editing).toBe(false);
    expect(component.startup.name).toBe('FinPulse Editada');
  });

  it('nao deve salvar startup sem nome', () => {
    component.startEdit('identity');
    component.startupForm.name = '   ';

    component.saveStartup();

    expect(component.errorMessage).toBe('Nome obrigatório.');
    expect(mockStartupService.updateStartup).not.toHaveBeenCalled();
  });

  it('nao deve exibir botao de edicao quando usuario nao tem permissao', () => {
    component.canEdit = false;
    component.currentStartupRole = 'Viewer';
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="edit-startup"]')).toBeFalsy();
  });

  it('deve informar quando metricas privadas nao vierem para visitante', () => {
    component.canEdit = false;
    component.startup = {
      ...component.startup,
      mainMetrics: null, monthlyRevenue: null, usersCount: null,
      growthPercent: null, clientsCount: null, mrr: null, churn: null,
      metricsPublic: false
    };
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('privadas');
  });

  it('deve buscar usuario e enviar convite de membro como Owner', () => {
    component.activeTab = 'team';
    fixture.detectChanges();

    component.memberSearchQuery = 'ana@email.com';
    component.inviteRole = 'Advisor';
    component.inviteMessage = 'Vamos colaborar na startup.';

    component.searchInviteUsers();
    component.inviteMember(component.inviteResults[0]);

    expect(mockUserService.searchUsers).toHaveBeenCalledWith('ana@email.com');
    expect(mockStartupService.inviteMember).toHaveBeenCalledWith(11, 4, 20, 'Advisor', 'Vamos colaborar na startup.');
    expect(component.inviteSuccess).toBe('Convite enviado.');
    expect(component.inviteResults.length).toBe(0);
  });

  it('deve permitir Owner alterar papel de membro', () => {
    const member = component.members.find(item => item.userId === 20)!;
    component.updateMemberRole(member, 'Viewer');

    expect(mockStartupService.updateMemberRole).toHaveBeenCalledWith(11, 20, 4, 'Viewer');
    expect(member.role).toBe('Viewer');
  });

  it('deve permitir Owner remover membro', () => {
    const member = component.members.find(item => item.userId === 20)!;
    component.removeMember(member);

    expect(mockStartupService.removeMember).toHaveBeenCalledWith(11, 20, 4);
    expect(component.members.some(item => item.userId === 20)).toBe(false);
  });
});
