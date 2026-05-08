import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { ProposalsComponent } from './proposals.component';
import { ConnectionService } from '../services/connection.service';
import { StartupService } from '../services/startup.service';
import { AuthService } from '../services/auth.service';

describe('ProposalsComponent - US-010', () => {
  let fixture: ComponentFixture<ProposalsComponent>;
  let component: ProposalsComponent;
  let mockConnectionService: any;
  let mockStartupService: any;
  let mockAuthService: any;

  beforeEach(async () => {
    mockConnectionService = {
      getProposals: vi.fn().mockReturnValue(of([])),
      updateStatus: vi.fn()
    };

    mockStartupService = {
      getStartupInvitationsForUser: vi.fn().mockReturnValue(of([
        {
          id: 5,
          startupName: 'FinPulse',
          senderName: 'Mariana',
          role: 'Advisor',
          message: 'Vamos colaborar.',
          status: 'PENDING'
        }
      ])),
      acceptStartupInvitation: vi.fn().mockReturnValue(of({ id: 5, status: 'ACCEPTED' })),
      rejectStartupInvitation: vi.fn().mockReturnValue(of({ id: 5, status: 'REJECTED' }))
    };

    mockAuthService = {
      getCurrentUser: vi.fn().mockReturnValue({ id: 9, name: 'Ana', email: 'ana@email.com' })
    };

    await TestBed.configureTestingModule({
      imports: [ProposalsComponent],
      providers: [
        { provide: ConnectionService, useValue: mockConnectionService },
        { provide: StartupService, useValue: mockStartupService },
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve listar convites de startup recebidos pelo usuario atual', () => {
    expect(mockConnectionService.getProposals).toHaveBeenCalledWith(9);
    expect(mockStartupService.getStartupInvitationsForUser).toHaveBeenCalledWith(9);
    expect(fixture.nativeElement.textContent).toContain('Convites de startup');
    expect(fixture.nativeElement.textContent).toContain('FinPulse');
    expect(fixture.nativeElement.textContent).toContain('Advisor');
  });

  it('deve aceitar convite de startup usando o usuario atual', () => {
    component.acceptInvitation(5);

    expect(mockStartupService.acceptStartupInvitation).toHaveBeenCalledWith(5, 9);
  });

  it('deve recusar convite de startup usando o usuario atual', () => {
    component.rejectInvitation(5);

    expect(mockStartupService.rejectStartupInvitation).toHaveBeenCalledWith(5, 9);
  });
});
