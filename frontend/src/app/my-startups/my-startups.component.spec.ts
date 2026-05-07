import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { MyStartupsComponent } from './my-startups.component';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';

describe('MyStartupsComponent', () => {
  let fixture: ComponentFixture<MyStartupsComponent>;
  let component: MyStartupsComponent;
  let mockAuthService: any;
  let mockUserService: any;
  let router: Router;

  beforeEach(async () => {
    mockAuthService = {
      getCurrentUser: vi.fn().mockReturnValue({ id: 4, name: 'Founder', email: 'founder@email.com' })
    };

    mockUserService = {
      getUserStartups: vi.fn().mockReturnValue(of([
        {
          startupId: 12,
          name: 'Startup Criada',
          sector: 'SaaS',
          stage: 'MVP',
          description: 'Startup do usuario atual.',
          role: 'Owner'
        }
      ]))
    };

    await TestBed.configureTestingModule({
      imports: [MyStartupsComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService },
        { provide: UserService, useValue: mockUserService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MyStartupsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('deve listar apenas startups vinculadas ao usuario atual', () => {
    const text = fixture.nativeElement.textContent;
    const startupLink = fixture.nativeElement.querySelector('a[href="/startup/12"]');

    expect(mockUserService.getUserStartups).toHaveBeenCalledWith(4);
    expect(component.startups.length).toBe(1);
    expect(text).toContain('Startup Criada');
    expect(text).toContain('Owner');
    expect(startupLink).toBeTruthy();
  });

  it('deve exibir estado vazio sem abrir startup padrao', async () => {
    mockUserService.getUserStartups.mockReturnValue(of([]));

    const emptyFixture = TestBed.createComponent(MyStartupsComponent);
    emptyFixture.detectChanges();

    expect(emptyFixture.nativeElement.textContent).toContain('Nenhuma startup vinculada');
    expect(mockUserService.getUserStartups).toHaveBeenLastCalledWith(4);
  });

  it('deve redirecionar para login quando nao ha usuario autenticado', async () => {
    mockAuthService.getCurrentUser.mockReturnValue(null);
    mockUserService.getUserStartups.mockReturnValue(throwError(() => ({ status: 401 })));

    const unauthFixture = TestBed.createComponent(MyStartupsComponent);
    unauthFixture.detectChanges();

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(mockUserService.getUserStartups).toHaveBeenCalledTimes(1);
  });
});
