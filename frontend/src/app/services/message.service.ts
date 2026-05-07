import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE = `${environment.apiUrl}/messages`;

@Injectable({ providedIn: 'root' })
export class MessageService {
  constructor(private http: HttpClient) {}

  getConversations(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/conversations?userId=${userId}`);
  }

  getThread(userId: number, partnerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/thread?userId=${userId}&partnerId=${partnerId}`);
  }

  sendMessage(senderId: number, receiverId: number, content: string): Observable<any> {
    return this.http.post<any>(BASE, { senderId, receiverId, content });
  }

  getUnreadCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${BASE}/unread-count?userId=${userId}`);
  }
}


