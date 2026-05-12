import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../services/AuthService';
@Component({
  selector: 'app-login-button',
  standalone: true,
  imports: [
    MatButtonModule
  ],
  template: `
    <button
    id="login-button"
    matButton="filled"
    (click)="loginWithRedirect()" 
    class="button login"
    >
      Log In
    </button>
  `
})
export class LoginButtonComponent {
  private auth = inject(AuthService);

  loginWithRedirect(): void {
    this.auth.login();
  }
}