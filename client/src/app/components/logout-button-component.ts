import { Component, inject } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { MatButtonModule } from '@angular/material/button';

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
    this.auth.logout({ 
      logoutParams: { 
        returnTo: window.location.origin 
      } 
    });
  }
}