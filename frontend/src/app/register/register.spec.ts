import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../services/auth.service';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockAuthService = {
      register: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    mockRouter = TestBed.inject(Router);
    vi.spyOn(mockRouter, 'navigate');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deve validar e-mail inválido', () => {
    component.email = 'nao-e-email';
    expect(component.emailValid).toBe(false);
  });

  it('deve validar senha curta', () => {
    component.password = '123';
    expect(component.passwordValid).toBe(false);
  });

  it('deve exibir erro ao tentar registrar sem nome', () => {
    component.name = '';
    component.email = 'ok@email.com';
    component.password = '123456';
    component.onRegister();
    expect(component.errorMessage).toBe('Nome é obrigatório.');
    expect(mockAuthService.register).not.toHaveBeenCalled();
  });

  it('deve redirecionar para / em cadastro bem-sucedido', () => {
    mockAuthService.register.mockReturnValue(of({ id: 1, name: 'Novo', email: 'novo@email.com' }));
    component.name = 'Novo Usuário';
    component.email = 'novo@email.com';
    component.password = 'senha123';
    component.onRegister();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/onboarding']);
  });

  it('deve exibir erro em e-mail duplicado (409)', () => {
    mockAuthService.register.mockReturnValue(throwError(() => ({ status: 409 })));
    component.name = 'Duplicado';
    component.email = 'dup@email.com';
    component.password = 'senha123';
    component.onRegister();
    expect(component.errorMessage).toBe('Este e-mail já está cadastrado.');
  });
});
