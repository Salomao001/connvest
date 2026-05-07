import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login';
import { AuthService } from '../services/auth.service';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockAuthService = {
      login: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    mockRouter = TestBed.inject(Router);
    vi.spyOn(mockRouter, 'navigate');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deve exibir erro se campos vazios', () => {
    component.email = '';
    component.password = '';
    component.onLogin();
    expect(component.errorMessage).toBe('Preencha e-mail e senha.');
    expect(mockAuthService.login).not.toHaveBeenCalled();
  });

  it('deve redirecionar para / em login bem-sucedido', () => {
    mockAuthService.login.mockReturnValue(of({ id: 1, name: 'User', email: 'u@email.com' }));
    component.email = 'u@email.com';
    component.password = 'senha123';
    component.onLogin();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('deve exibir mensagem de erro em 401', () => {
    mockAuthService.login.mockReturnValue(throwError(() => ({ status: 401 })));
    component.email = 'wrong@email.com';
    component.password = 'errada';
    component.onLogin();
    expect(component.errorMessage).toBe('E-mail ou senha inválidos.');
  });
});
