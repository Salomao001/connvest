import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConnectionService {
  private apiUrl = `${environment.apiUrl}/connections`;

  constructor(private http: HttpClient) {}

  sendRequest(receiverId: number, message: string): Observable<any> {
    const payload = {
      senderId: 1, // Mock current user Mariana
      senderName: 'Mariana Silva',
      senderPhotoUrl: 'https://i.pravatar.cc/150?img=5',
      senderRole: 'Founder',
      receiverId: receiverId,
      message: message,
      status: 'PENDING'
    };
    return this.http.post<any>(this.apiUrl, payload);
  }

  getProposals(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/receiver/${userId}`);
  }

  updateStatus(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/status?status=${status}`, {});
  }
}


