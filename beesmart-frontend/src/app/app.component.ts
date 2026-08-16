import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'BeeSmart';

  // Public so the template can read it (strictTemplates forbids private access).
  constructor(public auth: AuthService) {}

  get initial(): string {
    const user = this.auth.currentUser();
    const source = user?.fullName || user?.username || '';
    return source.charAt(0).toUpperCase();
  }

  get roleLabel(): string {
    return this.auth.isAdmin() ? 'Administrator' : 'Beekeeper';
  }

  logout() {
    this.auth.logout();
  }
}
