import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { StartupService } from './startup.service';

describe('StartupService - US-006', () => {
  let service: StartupService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StartupService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(StartupService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getStartups() deve listar startups sem filtro', () => {
    service.getStartups().subscribe(startups => {
      expect(startups.length).toBe(1);
      expect(startups[0].name).toBe('FinPulse');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups');
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1, name: 'FinPulse', stage: 'MVP' }]);
  });

  it('getStartups() deve enviar filtro de estagio quando informado', () => {
    service.getStartups('MVP').subscribe(startups => {
      expect(startups[0].stage).toBe('MVP');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups?stage=MVP');
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 2, name: 'Startup MVP', stage: 'MVP' }]);
  });

  it('getStartup() deve enviar viewerId quando informado', () => {
    service.getStartup(8, 4).subscribe(startup => {
      expect(startup.name).toBe('FinPulse');
      expect(startup.monthlyRevenue).toBe('R$ 180k');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups/8?viewerId=4');
    expect(req.request.method).toBe('GET');
    req.flush({ id: 8, name: 'FinPulse', monthlyRevenue: 'R$ 180k' });
  });

  it('createStartup() deve chamar endpoint de criacao com creatorId', () => {
    const startup = {
      name: 'Nova Startup',
      sector: 'SaaS'
    };

    service.createStartup(4, startup).subscribe(response => {
      expect(response.name).toBe('Nova Startup');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups?creatorId=4');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(startup);
    req.flush({ id: 8, ...startup });
  });

  it('updateStartup() deve chamar endpoint de edicao com userId', () => {
    const startup = {
      name: 'Startup Editada',
      sector: 'Healthtech'
    };

    service.updateStartup(8, 4, startup).subscribe(response => {
      expect(response.name).toBe('Startup Editada');
      expect(response.updatedAt).toBeTruthy();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups/8?userId=4');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(startup);
    req.flush({ id: 8, ...startup, updatedAt: '2026-05-06T10:00:00' });
  });

  it('getStartupMembers() deve buscar membros da startup', () => {
    service.getStartupMembers(8).subscribe(members => {
      expect(members[0].role).toBe('Owner');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups/8/members');
    expect(req.request.method).toBe('GET');
    req.flush([{ userId: 4, name: 'Founder', role: 'Owner' }]);
  });

  it('inviteMember() deve enviar convite com papel selecionado', () => {
    service.inviteMember(8, 4, 9, 'Advisor', 'Vamos conversar').subscribe(invitation => {
      expect(invitation.status).toBe('PENDING');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startup-invitations?senderId=4');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      startupId: 8,
      receiverId: 9,
      role: 'Advisor',
      message: 'Vamos conversar'
    });
    req.flush({ id: 2, startupId: 8, receiverId: 9, role: 'Advisor', status: 'PENDING' });
  });

  it('acceptStartupInvitation() deve aceitar convite recebido', () => {
    service.acceptStartupInvitation(2, 9).subscribe(invitation => {
      expect(invitation.status).toBe('ACCEPTED');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startup-invitations/2/accept?userId=9');
    expect(req.request.method).toBe('PUT');
    req.flush({ id: 2, status: 'ACCEPTED' });
  });

  it('updateMemberRole() deve alterar papel de membro com requesterId', () => {
    service.updateMemberRole(8, 9, 4, 'Viewer').subscribe(member => {
      expect(member.role).toBe('Viewer');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups/8/members/9/role?requesterId=4&role=Viewer');
    expect(req.request.method).toBe('PUT');
    req.flush({ userId: 9, role: 'Viewer' });
  });

  it('removeMember() deve remover membro com requesterId', () => {
    service.removeMember(8, 9, 4).subscribe(response => {
      expect(response.message).toBe('Membro removido com sucesso.');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/startups/8/members/9?requesterId=4');
    expect(req.request.method).toBe('DELETE');
    req.flush({ message: 'Membro removido com sucesso.' });
  });
});
