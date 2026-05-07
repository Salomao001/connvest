import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SearchService } from './search.service';

describe('SearchService - US-004', () => {
  let service: SearchService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SearchService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(SearchService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('searchCoFounders() deve listar founders buscando co-founder', () => {
    service.searchCoFounders().subscribe(users => {
      expect(users.length).toBe(1);
      expect(users[0].seekingCoFounder).toBe(true);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/search/cofounders');
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1, name: 'Founder', seekingCoFounder: true }]);
  });

  it('searchCoFounders() deve enviar filtro de area quando informado', () => {
    service.searchCoFounders('tech').subscribe();

    const req = httpMock.expectOne(request =>
      request.url === 'http://localhost:8080/api/search/cofounders' &&
      request.params.get('area') === 'tech'
    );
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
