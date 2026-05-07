import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { PostService } from './post.service';

describe('PostService - US-011', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PostService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('createPost() deve enviar userId quando publicar como startup', () => {
    const post = {
      startupId: 11,
      authorType: 'Startup',
      content: 'Update da startup.'
    };

    service.createPost(post, 4).subscribe(response => {
      expect(response.startupId).toBe(11);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/posts?userId=4');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(post);
    req.flush({ id: 30, ...post });
  });

  it('createPost() deve manter post pessoal sem userId', () => {
    const post = {
      authorType: 'Founder',
      content: 'Post pessoal.'
    };

    service.createPost(post).subscribe(response => {
      expect(response.authorType).toBe('Founder');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/posts');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 31, ...post });
  });
});
