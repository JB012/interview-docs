import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, shareReplay, take } from 'rxjs';

export interface AuthResponse {
  authenticated : boolean,
  user?: {
    sub: string;
    email?: string;
    name?: string;
  };
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private user$?: Observable<AuthResponse | null>;

  constructor(private http: HttpClient) {}

  login() {
    window.location.href = '/oauth2/authorization/auth0';
  }

  logout() {
    window.location.href = '/logout';
  }

  getCurrentUser() : Observable<AuthResponse | null> {
    if (this.user$) {
      return this.user$;
    }

    this.user$ = this.http.get<AuthResponse>('/auth/me', {
      withCredentials: true
    }).pipe(
      catchError(() => of(null)),
      shareReplay(1)
    );

    return this.user$;
  }
}