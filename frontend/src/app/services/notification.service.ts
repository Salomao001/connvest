import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

const BASE = `${environment.apiUrl}/notifications`;

@Injectable({ providedIn: 'root' })
export class NotificationService {
  unreadNotif$ = new BehaviorSubject<number>(0);
  unreadMsg$   = new BehaviorSubject<number>(0);
  incoming$    = new Subject<any>();

  constructor(private http: HttpClient) {}

  streamNotifications(userId: number): Observable<any> {
    return new Observable(observer => {
      let source: EventSource;

      const connect = () => {
        source = new EventSource(`${BASE}/stream?userId=${userId}`);
        source.addEventListener('notification', (e: MessageEvent) => {
          try { observer.next(JSON.parse(e.data)); } catch { /* ignore parse errors */ }
        });
        source.onerror = () => {
          source.close();
          setTimeout(connect, 5000); // reconnect after 5s on error
        };
      };

      connect();
      return () => source?.close();
    });
  }

  getNotifications(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}?userId=${userId}&excludeType=MESSAGE`);
  }

  getUnreadCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${BASE}/unread-count?userId=${userId}`);
  }

  getGeneralUnreadCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${BASE}/unread-count?userId=${userId}&excludeType=MESSAGE`);
  }

  getMessageUnreadCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${environment.apiUrl}/messages/unread-count?userId=${userId}`);
  }

  markAsRead(id: number): Observable<any> {
    return this.http.put<any>(`${BASE}/${id}/read`, {});
  }

  markAllAsRead(userId: number): Observable<any> {
    return this.http.put<any>(`${BASE}/read-all?userId=${userId}`, {});
  }
}


