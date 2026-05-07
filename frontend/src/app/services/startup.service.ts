import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StartupService {
  private apiUrl = 'http://localhost:8080/api/startups';

  constructor(private http: HttpClient) {}

  getStartups(stage?: string): Observable<any[]> {
    const suffix = stage ? `?stage=${encodeURIComponent(stage)}` : '';
    return this.http.get<any[]>(`${this.apiUrl}${suffix}`);
  }

  getStartup(id: number, viewerId?: number): Observable<any> {
    const suffix = viewerId !== undefined ? `?viewerId=${viewerId}` : '';
    return this.http.get(`${this.apiUrl}/${id}${suffix}`);
  }

  createStartup(creatorId: number, startup: any): Observable<any> {
    return this.http.post(`${this.apiUrl}?creatorId=${creatorId}`, startup);
  }

  updateStartup(id: number, userId: number, startup: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}?userId=${userId}`, startup);
  }

  getStartupMembers(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/members`);
  }

  inviteMember(startupId: number, senderId: number, receiverId: number, role: string, message: string): Observable<any> {
    return this.http.post('http://localhost:8080/api/startup-invitations?senderId=' + senderId, {
      startupId,
      receiverId,
      role,
      message
    });
  }

  getStartupInvitationsForUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/startup-invitations/receiver/${userId}`);
  }

  acceptStartupInvitation(invitationId: number, userId: number): Observable<any> {
    return this.http.put(`http://localhost:8080/api/startup-invitations/${invitationId}/accept?userId=${userId}`, {});
  }

  rejectStartupInvitation(invitationId: number, userId: number): Observable<any> {
    return this.http.put(`http://localhost:8080/api/startup-invitations/${invitationId}/reject?userId=${userId}`, {});
  }

  updateMemberRole(startupId: number, memberUserId: number, requesterId: number, role: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${startupId}/members/${memberUserId}/role?requesterId=${requesterId}&role=${encodeURIComponent(role)}`, {});
  }

  removeMember(startupId: number, memberUserId: number, requesterId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${startupId}/members/${memberUserId}?requesterId=${requesterId}`);
  }
}
