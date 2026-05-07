import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE = `${environment.apiUrl}/saved-startups`;

@Injectable({ providedIn: 'root' })
export class SavedStartupService {
  constructor(private http: HttpClient) {}

  getSaved(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}?userId=${userId}`);
  }

  save(userId: number, startupId: number): Observable<any> {
    return this.http.post<any>(`${BASE}?userId=${userId}&startupId=${startupId}`, {});
  }

  unsave(userId: number, startupId: number): Observable<any> {
    return this.http.delete<any>(`${BASE}?userId=${userId}&startupId=${startupId}`);
  }

  checkSaved(userId: number, startupId: number): Observable<{ saved: boolean }> {
    return this.http.get<any>(`${BASE}/status?userId=${userId}&startupId=${startupId}`);
  }

  updatePipeline(savedId: number, stage: string): Observable<any> {
    return this.http.put<any>(`${BASE}/${savedId}/pipeline?stage=${stage}`, {});
  }
}


