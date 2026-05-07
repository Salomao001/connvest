import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE = 'http://localhost:8080/api/rankings';

@Injectable({ providedIn: 'root' })
export class RankingService {
  constructor(private http: HttpClient) {}

  getPerformance(): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/performance`);
  }

  getPopularity(): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/popularity`);
  }

  getTrending(): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/trending`);
  }
}
