import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE = 'http://localhost:8080/api/notifications';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private http: HttpClient) {}

  getNotifications(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}?userId=${userId}`);
  }

  getUnreadCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${BASE}/unread-count?userId=${userId}`);
  }

  markAsRead(id: number): Observable<any> {
    return this.http.put<any>(`${BASE}/${id}/read`, {});
  }

  markAllAsRead(userId: number): Observable<any> {
    return this.http.put<any>(`${BASE}/read-all?userId=${userId}`, {});
  }
}
