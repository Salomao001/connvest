import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OnboardingComponent } from './onboarding.component';
import { AuthService } from '../services/auth.service';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('OnboardingComponent', () => {
  let component: OnboardingComponent;
  let fixture: ComponentFixture<OnboardingComponent>;
  let mockAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockAuthService = {
      getCurrentUser: vi.fn().mockReturnValue({ id: 1, name: 'Test' }),
      updateProfileTypes: vi.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      imports: [OnboardingComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(OnboardingComponent);
    component = fixture.componentInstance;
    mockRouter = TestBed.inject(Router);
    vi.spyOn(mockRouter, 'navigate');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deve selecionar e desselecionar opções', () => {
    component.toggleSelection('Founder');
    expect(component.selectedIds).toContain('Founder');
    component.toggleSelection('Founder');
    expect(component.selectedIds).not.toContain('Founder');
  });

  it('deve chamar updateProfileTypes e navegar para home ao salvar', () => {
    component.selectedIds = ['Founder', 'Advisor'];
    component.save();
    expect(mockAuthService.updateProfileTypes).toHaveBeenCalledWith(1, ['Founder', 'Advisor']);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
});
