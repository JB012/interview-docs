import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}

  login() {
    window.location.href = '/oauth2/authorization/auth0';
  }

  logout() {
    window.location.href = '/logout';
  }

  me() {
    return this.http.get('/api/me', {
      withCredentials: true
    });
  }
}