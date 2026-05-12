import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Header } from './components/Header';
import { MatSidenavModule } from '@angular/material/sidenav';

@Component({
  selector: 'app-root',
  templateUrl: 'app.html',
  styleUrl: '../styles.css',
  imports: [RouterOutlet, Header, MatSidenavModule],
})
export class App {
  opened = false;
  protected readonly title = signal('client');

  constructor(private router : Router) {}

  isLandingPage() : boolean {
    return this.router.url === '/';
  }
}
