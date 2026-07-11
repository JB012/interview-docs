import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, shareReplay, take } from 'rxjs';
import { environment } from '../environments/environment';

export interface AuthResponse {
  authenticated : boolean,
  user?: string
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private user$?: Observable<AuthResponse | null>;

  constructor(private http: HttpClient) {}
  apiHost = environment.url;

  login() {
    window.location.href = `${this.apiHost}/oauth/login/auth0`;
  }

  logout() {
    window.location.href = environment.logoutURL;
  }

  getCurrentUser() : Observable<AuthResponse | null> {
    if (this.user$) {
      return this.user$;
    }

    this.user$ = this.http.get<AuthResponse>(`${this.apiHost}/auth/me`, {
      withCredentials: true
    }).pipe(
      catchError(() => of(null)),
      shareReplay(1)
    );

    return this.user$;
  }
}