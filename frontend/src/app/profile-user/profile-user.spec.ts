import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { ProfileUser } from './profile-user';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';

describe('ProfileUser - US-003/US-004', () => {
  let component: ProfileUser;
  let fixture: ComponentFixture<ProfileUser>;
  let mockUserService: any;
  let mockAuthService: any;

  const user = {
    id: 7,
    name: 'Mariana Silva',
    photo: '',
    bio: 'Founder de fintech.',
    location: 'Sao Paulo, SP',
    profileTypes: ['Founder'],
    pastExperiences: 'PM na FinCorp',
    mainSkills: 'Produto, Growth',
    interests: 'Seed, IA',
    externalLinks: 'https://linkedin.com/in/mariana',
    seekingCoFounder: false,
    desiredCoFounderType: '',
    coFounderArea: '',
    coFounderDedication: '',
    coFounderDescription: ''
  };

  beforeEach(async () => {
    mockUserService = {
      getUser: vi.fn().mockReturnValue(of(user)),
      getUserStartups: vi.fn().mockReturnValue(of([
        {
          startupId: 11,
          name: 'FinPulse',
          sector: 'Fintech',
          stage: 'Seed',
          description: 'Tesouraria B2B com IA.',
          role: 'Founder'
        }
      ])),
      updateProfile: vi.fn()
    };

    mockAuthService = {
      getCurrentUser: vi.fn().mockReturnValue({ id: 7, name: 'Mariana Silva', email: 'mariana@email.com' }),
      updateCurrentUser: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ProfileUser],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: mockUserService },
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileUser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve carregar o perfil do usuario autenticado', () => {
    expect(mockUserService.getUser).toHaveBeenCalledWith(7);
    expect(component.user.name).toBe('Mariana Silva');
    expect(component.profileForm.profileTypesText).toBe('Founder');
  });

  it('deve exibir skills cadastradas na visualizacao do perfil', () => {
    const text = fixture.nativeElement.textContent;
    expect(text).toContain('Produto');
    expect(text).toContain('Growth');
  });

  it('deve listar startups vinculadas com papel e link para o perfil da startup', () => {
    const text = fixture.nativeElement.textContent;
    const startupLink = fixture.nativeElement.querySelector('a[href="/startup/11"]');

    expect(mockUserService.getUserStartups).toHaveBeenCalledWith(7);
    expect(text).toContain('FinPulse');
    expect(text).toContain('Founder');
    expect(startupLink).toBeTruthy();
  });

  it('deve salvar perfil completo e atualizar usuario local', () => {
    const updated = {
      ...user,
      name: 'Mariana Editada',
      profileTypes: ['Founder', 'Advisor'],
      mainSkills: 'Produto, Growth, Captacao'
    };
    mockUserService.updateProfile.mockReturnValue(of(updated));

    component.startEditing();
    component.profileForm = {
      name: ' Mariana Editada ',
      photo: 'https://example.com/avatar.png',
      bio: 'Bio editada',
      location: 'Rio de Janeiro, RJ',
      profileTypesText: 'Founder, Advisor',
      pastExperiences: 'Founder na TechNova',
      mainSkills: 'Produto, Growth, Captacao',
      interests: 'Seed',
      externalLinks: 'https://linkedin.com/in/editada',
      seekingCoFounder: false,
      desiredCoFounderType: '',
      coFounderArea: '',
      coFounderDedication: '',
      coFounderDescription: ''
    };

    component.saveProfile();

    expect(mockUserService.updateProfile).toHaveBeenCalledWith(7, {
      name: 'Mariana Editada',
      photo: 'https://example.com/avatar.png',
      bio: 'Bio editada',
      location: 'Rio de Janeiro, RJ',
      profileTypesText: 'Founder, Advisor',
      pastExperiences: 'Founder na TechNova',
      mainSkills: 'Produto, Growth, Captacao',
      interests: 'Seed',
      externalLinks: 'https://linkedin.com/in/editada',
      seekingCoFounder: false,
      desiredCoFounderType: '',
      coFounderArea: '',
      coFounderDedication: '',
      coFounderDescription: '',
      profileTypes: ['Founder', 'Advisor']
    });
    expect(component.editing).toBe(false);
    expect(component.user.mainSkills).toBe('Produto, Growth, Captacao');
    expect(mockAuthService.updateCurrentUser).toHaveBeenCalledWith({
      name: 'Mariana Editada',
      profileTypes: ['Founder', 'Advisor']
    });
  });

  it('deve permitir salvar somente o nome com opcionais em branco', () => {
    mockUserService.updateProfile.mockReturnValue(of({ ...user, name: 'Somente Nome', mainSkills: '' }));

    component.profileForm = {
      name: 'Somente Nome',
      photo: '',
      bio: '',
      location: '',
      profileTypesText: '',
      pastExperiences: '',
      mainSkills: '',
      interests: '',
      externalLinks: '',
      seekingCoFounder: false,
      desiredCoFounderType: '',
      coFounderArea: '',
      coFounderDedication: '',
      coFounderDescription: ''
    };

    component.saveProfile();

    expect(mockUserService.updateProfile).toHaveBeenCalledWith(7, {
      name: 'Somente Nome',
      photo: '',
      bio: '',
      location: '',
      profileTypesText: '',
      pastExperiences: '',
      mainSkills: '',
      interests: '',
      externalLinks: '',
      seekingCoFounder: false,
      desiredCoFounderType: '',
      coFounderArea: '',
      coFounderDedication: '',
      coFounderDescription: '',
      profileTypes: []
    });
  });

  it('deve salvar interesse em buscar co-founder com campos opcionais', () => {
    mockUserService.updateProfile.mockReturnValue(of({
      ...user,
      seekingCoFounder: true,
      desiredCoFounderType: 'CTO',
      coFounderArea: 'tech',
      coFounderDedication: 'full-time',
      coFounderDescription: 'Procuro alguem para liderar tecnologia.'
    }));

    component.profileForm = {
      ...component.profileForm,
      name: 'Mariana Silva',
      seekingCoFounder: true,
      desiredCoFounderType: 'CTO',
      coFounderArea: 'tech',
      coFounderDedication: 'full-time',
      coFounderDescription: 'Procuro alguem para liderar tecnologia.'
    };

    component.saveProfile();

    expect(mockUserService.updateProfile).toHaveBeenCalledWith(7, expect.objectContaining({
      seekingCoFounder: true,
      desiredCoFounderType: 'CTO',
      coFounderArea: 'tech',
      coFounderDedication: 'full-time',
      coFounderDescription: 'Procuro alguem para liderar tecnologia.'
    }));
  });

  it('nao deve salvar perfil sem nome', () => {
    component.profileForm.name = '   ';

    component.saveProfile();

    expect(component.errorMessage).toBe('Nome obrigatorio.');
    expect(mockUserService.updateProfile).not.toHaveBeenCalled();
  });
});
