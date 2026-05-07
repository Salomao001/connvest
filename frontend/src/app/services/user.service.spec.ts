import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UserService } from './user.service';

describe('UserService - US-003', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('updateProfile() deve chamar endpoint de edicao do perfil pessoal', () => {
    const profile = {
      name: 'Mariana',
      bio: '',
      profileTypes: [],
      mainSkills: 'Produto'
    };

    service.updateProfile(3, profile).subscribe(response => {
      expect(response.name).toBe('Mariana');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/users/3/profile');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(profile);
    req.flush({ id: 3, ...profile });
  });

  it('getUserStartups() deve buscar startups vinculadas ao usuario', () => {
    service.getUserStartups(3).subscribe(startups => {
      expect(startups[0].name).toBe('FinPulse');
      expect(startups[0].role).toBe('Founder');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/users/3/startups');
    expect(req.request.method).toBe('GET');
    req.flush([{ startupId: 9, name: 'FinPulse', role: 'Founder' }]);
  });

  it('searchUsers() deve buscar usuarios por nome ou email', () => {
    service.searchUsers('ana@email.com').subscribe(users => {
      expect(users[0].email).toBe('ana@email.com');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/users/search?query=ana%40email.com');
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 7, name: 'Ana', email: 'ana@email.com' }]);
  });
});
