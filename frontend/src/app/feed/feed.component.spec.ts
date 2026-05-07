import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { FeedComponent } from './feed.component';
import { PostService } from '../services/post.service';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';

describe('FeedComponent - US-011', () => {
  let fixture: ComponentFixture<FeedComponent>;
  let component: FeedComponent;
  let mockPostService: any;
  let mockAuthService: any;
  let mockUserService: any;

  beforeEach(async () => {
    mockPostService = {
      getPosts: vi.fn().mockReturnValue(of([])),
      createPost: vi.fn().mockReturnValue(of({
        id: 99,
        startupId: 11,
        authorName: 'FinPulse',
        authorType: 'Startup',
        content: 'Update pela startup.'
      }))
    };

    mockAuthService = {
      getCurrentUser: vi.fn().mockReturnValue({ id: 4, name: 'Mariana Silva', email: 'mariana@email.com' })
    };

    mockUserService = {
      getUserStartups: vi.fn().mockReturnValue(of([
        { startupId: 11, name: 'FinPulse', role: 'Admin' },
        { startupId: 12, name: 'Startup Viewer', role: 'Viewer' }
      ]))
    };

    await TestBed.configureTestingModule({
      imports: [FeedComponent],
      providers: [
        { provide: PostService, useValue: mockPostService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: UserService, useValue: mockUserService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FeedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve oferecer publicacao por startup apenas para papeis permitidos', () => {
    expect(mockUserService.getUserStartups).toHaveBeenCalledWith(4);
    expect(component.startupPostOptions.length).toBe(1);
    expect(component.startupPostOptions[0].name).toBe('FinPulse');
    expect(fixture.nativeElement.textContent).toContain('Publicar como FinPulse');
    expect(fixture.nativeElement.textContent).not.toContain('Startup Viewer');
  });

  it('deve publicar em nome da startup com userId do usuario atual', () => {
    component.selectedAuthor = '11';
    component.newPostContent = 'Update pela startup.';

    component.publish();

    expect(mockPostService.createPost).toHaveBeenCalledWith(expect.objectContaining({
      startupId: 11,
      authorId: 11,
      authorName: 'FinPulse',
      authorType: 'Startup',
      content: 'Update pela startup.'
    }), 4);
  });
});
