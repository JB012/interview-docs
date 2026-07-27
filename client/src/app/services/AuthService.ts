import { Injectable, isDevMode } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, shareReplay, take } from 'rxjs';
import { environment as prodEnvironment} from '../environments/environment';
import { environment as devEnvironment} from '../environments/environment.development';
export interface AuthResponse {
  authenticated : boolean,
  user?: string
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private user$?: Observable<AuthResponse | null>;

  constructor(private http: HttpClient) {
    console.log(isDevMode())
  }

  apiHost = isDevMode() ? devEnvironment.url : prodEnvironment.url;

  login() {
    window.location.href = `http://localhost:8080/oauth/login/auth0`;
  }

  logout() {
    window.location.href = isDevMode() ? devEnvironment.logoutURL : prodEnvironment.logoutURL;
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