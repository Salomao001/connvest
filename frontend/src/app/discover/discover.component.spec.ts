import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { DiscoverComponent } from './discover.component';
import { SearchService } from '../services/search.service';
import { ConnectionService } from '../services/connection.service';
import { StartupService } from '../services/startup.service';

describe('DiscoverComponent - US-004/US-008', () => {
  let component: DiscoverComponent;
  let fixture: ComponentFixture<DiscoverComponent>;
  let mockSearchService: any;
  let mockStartupService: any;

  beforeEach(async () => {
    mockSearchService = {
      search: vi.fn().mockReturnValue(of({ users: [], startups: [] })),
      searchCoFounders: vi.fn().mockReturnValue(of([
        {
          id: 1,
          name: 'Founder Tech',
          bio: 'Construindo uma startup B2B.',
          seekingCoFounder: true,
          coFounderArea: 'tech',
          coFounderDedication: 'full-time',
          coFounderDescription: 'Busco CTO.'
        }
      ]))
    };

    mockStartupService = {
      getStartups: vi.fn().mockReturnValue(of([
        {
          id: 10,
          name: 'Startup MVP',
          sector: 'SaaS',
          stage: 'MVP',
          description: 'Startup em MVP.'
        }
      ]))
    };

    await TestBed.configureTestingModule({
      imports: [DiscoverComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } },
        { provide: SearchService, useValue: mockSearchService },
        { provide: StartupService, useValue: mockStartupService },
        { provide: ConnectionService, useValue: { sendRequest: vi.fn() } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DiscoverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve carregar founders buscando co-founder quando nao ha busca textual', () => {
    expect(mockSearchService.searchCoFounders).toHaveBeenCalledWith('');
    expect(mockStartupService.getStartups).toHaveBeenCalledWith('');
    expect(component.coFounderUsers[0].name).toBe('Founder Tech');
    expect(fixture.nativeElement.textContent).toContain('Busco CTO.');
  });

  it('deve recarregar lista ao filtrar por area desejada', () => {
    component.coFounderArea = 'tech';
    component.onCoFounderAreaChange();

    expect(mockSearchService.searchCoFounders).toHaveBeenLastCalledWith('tech');
  });

  it('deve carregar startups por estagio quando filtro muda sem busca textual', () => {
    component.stageFilter = 'MVP';
    component.onStageChange();

    expect(mockStartupService.getStartups).toHaveBeenLastCalledWith('MVP');
    expect(component.startups[0].stage).toBe('MVP');
    expect(fixture.nativeElement.textContent).toContain('Startup MVP');
  });

  it('deve filtrar startups de uma busca textual por estagio selecionado', () => {
    mockSearchService.search.mockReturnValue(of({
      users: [],
      startups: [
        { id: 1, name: 'Startup MVP', stage: 'MVP' },
        { id: 2, name: 'Startup Ideia', stage: 'Ideia' }
      ]
    }));
    component.searchQuery = 'Startup';
    component.stageFilter = 'MVP';

    component.performSearch();

    expect(component.startups.length).toBe(1);
    expect(component.startups[0].name).toBe('Startup MVP');
  });
});
