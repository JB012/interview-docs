import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, shareReplay, take } from 'rxjs';

export interface AuthResponse {
  authenticated : boolean,
  user?: {
    claims: {      
      sub: string;
      email?: string;
      name?: string;
    }
  };
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private user$?: Observable<AuthResponse | null>;

  constructor(private http: HttpClient) {}

  login() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/auth0';
  }

  logout() {
    window.location.href = 'http://localhost:8080/logout';
  }

  getCurrentUser() : Observable<AuthResponse | null> {
    if (this.user$) {
      return this.user$;
    }

    this.user$ = this.http.get<AuthResponse>('http://localhost:8080/auth/me', {
      withCredentials: true
    }).pipe(
      catchError(() => of(null)),
      shareReplay(1)
    );

    return this.user$;
  }
}