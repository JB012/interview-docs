import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../services/AuthService';

@Component({
  selector: 'app-logout-button',
  standalone: true,
  imports: [
    MatButtonModule
  ],
  template: `
    <button
    matButton="tonal"
    (click)="logout()"
    class="button logout"
    >
      Log Out
    </button>
  `
})
export class LogoutButtonComponent {
  private auth = inject(AuthService);

  logout(): void {
    this.auth.logout();
  }
}