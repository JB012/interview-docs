import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './routes/home.html',
  styleUrl: '../styles.css'
})
export class App {
  protected readonly title = signal('client');
}
