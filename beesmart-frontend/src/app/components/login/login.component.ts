import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  username = '';
  password = '';
  error = '';
  notice = '';
  loading = false;

  private returnUrl: string | null = null;

  constructor(
    private auth: AuthService,
    private router: Router,
    route: ActivatedRoute
  ) {
    const params = route.snapshot.queryParams;
    this.returnUrl = params['returnUrl'] ?? null;

    if (params['expired']) {
      this.notice = 'Your session has expired. Please sign in again.';
    }
  }

  submit() {
    if (!this.username.trim() || !this.password) {
      this.error = 'Enter your username and password';
      return;
    }

    this.loading = true;
    this.error = '';
    this.notice = '';

    this.auth.login(this.username.trim(), this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(this.returnUrl ?? this.auth.homeRoute());
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message ?? 'Unable to sign in. Is the server running?';
      }
    });
  }
}
