import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE = `${environment.apiUrl}/proposals`;

@Injectable({ providedIn: 'root' })
export class ProposalService {
  constructor(private http: HttpClient) {}

  sendProposal(proposal: any): Observable<any> {
    return this.http.post<any>(BASE, proposal);
  }

  getReceived(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/received?userId=${userId}`);
  }

  getSent(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/sent?userId=${userId}`);
  }

  updateStatus(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${BASE}/${id}/status?status=${status}`, {});
  }
}


