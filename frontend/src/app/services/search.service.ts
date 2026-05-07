import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private apiUrl = `${environment.apiUrl}/search`;

  constructor(private http: HttpClient) {}

  search(query: string): Observable<any> {
    const params = new HttpParams().set('q', query);
    return this.http.get<any>(this.apiUrl, { params });
  }

  searchCoFounders(area?: string): Observable<any[]> {
    let params = new HttpParams();
    if (area) {
      params = params.set('area', area);
    }

    return this.http.get<any[]>(`${this.apiUrl}/cofounders`, { params });
  }
}


