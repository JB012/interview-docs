import { Component, signal } from '@angular/core';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-root',
  templateUrl: './routes/home.html',
  styleUrl: '../styles.css',
  imports: [ MatPaginatorModule, MatButtonModule],
})
export class App {
  protected readonly title = signal('client');
}
