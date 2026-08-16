import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  username = '';
  fullName = '';
  email = '';
  password = '';
  confirmPassword = '';

  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  submit() {
    this.error = '';

    if (!this.username.trim() || !this.fullName.trim() || !this.password) {
      this.error = 'Username, full name and password are required';
      return;
    }
    if (this.username.trim().length < 3) {
      this.error = 'Username must be at least 3 characters';
      return;
    }
    if (this.password.length < 5) {
      this.error = 'Password must be at least 5 characters';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    this.loading = true;

    this.auth.register({
      username: this.username.trim(),
      fullName: this.fullName.trim(),
      email: this.email.trim(),
      password: this.password
    }).subscribe({
      next: () => {
        this.loading = false;
        // Registration signs the new beekeeper straight in.
        this.router.navigateByUrl(this.auth.homeRoute());
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message ?? 'Registration failed. Is the server running?';
      }
    });
  }
}
