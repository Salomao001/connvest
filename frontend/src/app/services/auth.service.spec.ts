import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { vi } from 'vitest';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerSpy: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    routerSpy = { navigate: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerSpy }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('register() deve salvar usuário no localStorage', () => {
    const mockUser = { id: 1, name: 'João', email: 'joao@email.com', profileType: null };

    service.register('João', 'joao@email.com', '123456').subscribe(user => {
      expect(user.name).toBe('João');
      expect(service.isLoggedIn()).toBe(true);
      expect(service.getCurrentUser()?.email).toBe('joao@email.com');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/users/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ name: 'João', email: 'joao@email.com', password: '123456' });
    req.flush(mockUser);
  });

  it('login() deve salvar usuário no localStorage', () => {
    const mockUser = { id: 2, name: 'Maria', email: 'maria@email.com', profileType: 'Founder' };

    service.login('maria@email.com', 'senha123').subscribe(() => {
      expect(service.isLoggedIn()).toBe(true);
      expect(service.getCurrentUser()?.name).toBe('Maria');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/users/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockUser);
  });

  it('logout() deve limpar localStorage e redirecionar para /login', () => {
    localStorage.setItem('conninvest_user', JSON.stringify({ id: 1, name: 'Test' }));
    service.logout();
    expect(service.isLoggedIn()).toBe(false);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('updateCurrentUser() deve atualizar dados do usuario autenticado', () => {
    localStorage.setItem('conninvest_user', JSON.stringify({
      id: 1,
      name: 'Nome Antigo',
      email: 'user@email.com',
      profileTypes: ['Founder']
    }));

    service.updateCurrentUser({ name: 'Nome Novo', profileTypes: ['Advisor'] });

    expect(service.getCurrentUser()).toEqual({
      id: 1,
      name: 'Nome Novo',
      email: 'user@email.com',
      profileTypes: ['Advisor']
    });
  });

  it('isLoggedIn() deve retornar false se não há usuário no localStorage', () => {
    expect(service.isLoggedIn()).toBe(false);
  });
});
