import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE = `${environment.apiUrl}/follows`;

@Injectable({ providedIn: 'root' })
export class FollowService {
  constructor(private http: HttpClient) {}

  toggleFollow(followerId: number, targetType: 'USER' | 'STARTUP', targetId: number): Observable<any> {
    return this.http.post<any>(`${BASE}/toggle?followerId=${followerId}&targetType=${targetType}&targetId=${targetId}`, {});
  }

  getStatus(followerId: number, targetType: string, targetId: number): Observable<{ following: boolean; count: number }> {
    return this.http.get<any>(`${BASE}/status?followerId=${followerId}&targetType=${targetType}&targetId=${targetId}`);
  }

  getFollowing(followerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/following?followerId=${followerId}`);
  }
}


