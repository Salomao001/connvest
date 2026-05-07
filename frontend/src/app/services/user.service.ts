import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getUser(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  searchUsers(query: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search?query=${encodeURIComponent(query)}`);
  }

  updateProfile(id: number, profile: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/profile`, profile);
  }

  getUserStartups(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/startups`);
  }
}
