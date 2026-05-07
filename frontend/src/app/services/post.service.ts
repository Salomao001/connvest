import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const BASE = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private http: HttpClient) {}

  getPosts(): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/posts`);
  }

  createPost(post: any, userId?: number): Observable<any> {
    const suffix = userId !== undefined ? `?userId=${userId}` : '';
    return this.http.post<any>(`${BASE}/posts${suffix}`, post);
  }

  toggleLike(postId: number, userId: number): Observable<{ liked: boolean; count: number }> {
    return this.http.post<any>(`${BASE}/posts/${postId}/likes?userId=${userId}`, {});
  }

  getComments(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${BASE}/posts/${postId}/comments`);
  }

  addComment(postId: number, comment: any): Observable<any> {
    return this.http.post<any>(`${BASE}/posts/${postId}/comments`, comment);
  }
}
