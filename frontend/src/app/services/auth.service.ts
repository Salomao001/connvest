import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export interface AuthUser {
  id: number;
  name: string;
  email: string;
  profileTypes?: string[];
  photo?: string;
}

const API = `${environment.apiUrl}/users`;
const STORAGE_KEY = 'conninvest_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(private http: HttpClient, private router: Router) {}

  register(name: string, email: string, password: string): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${API}/register`, { name, email, password }).pipe(
      tap(user => this.saveUser(user))
    );
  }

  login(email: string, password: string): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${API}/login`, { email, password }).pipe(
      tap(user => this.saveUser(user))
    );
  }

  updateProfileTypes(userId: number, types: string[]): Observable<any> {
    return this.http.post(`${API}/${userId}/profile-types`, types).pipe(
      tap(() => {
        const user = this.getCurrentUser();
        if (user && user.id === userId) {
          user.profileTypes = types;
          this.saveUser(user);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.router.navigate(['/login']);
  }

  getCurrentUser(): AuthUser | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  updateCurrentUser(partial: Partial<AuthUser>): void {
    const user = this.getCurrentUser();
    if (user) {
      this.saveUser({ ...user, ...partial });
    }
  }

  isLoggedIn(): boolean {
    return !!this.getCurrentUser();
  }

  private saveUser(user: AuthUser): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
  }
}


