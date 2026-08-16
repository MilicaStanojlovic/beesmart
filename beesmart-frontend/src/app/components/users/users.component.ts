import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {

  users: any[] = [];

  form: any = { username: '', fullName: '', email: '', password: '', active: true };
  editingId: number | null = null;

  error = '';
  success = '';
  loading = false;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.api.getUsers().subscribe({
      next: (res) => {
        this.users = res;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message ?? 'Could not load users';
      }
    });
  }

  startCreate() {
    this.editingId = null;
    this.form = { username: '', fullName: '', email: '', password: '', active: true };
    this.clearMessages();
  }

  startEdit(user: any) {
    this.editingId = user.id;
    this.form = {
      username: user.username,
      fullName: user.fullName,
      email: user.email,
      password: '',
      active: user.active
    };
    this.clearMessages();
  }

  save() {
    this.clearMessages();

    if (!this.form.username?.trim() || !this.form.fullName?.trim()) {
      this.error = 'Username and full name are required';
      return;
    }
    if (this.editingId === null && (!this.form.password || this.form.password.length < 5)) {
      this.error = 'Password must be at least 5 characters';
      return;
    }

    const request = this.editingId === null
      ? this.api.createUser(this.form)
      : this.api.updateUser(this.editingId, this.form);

    request.subscribe({
      next: () => {
        this.success = this.editingId === null ? 'Beekeeper created' : 'Beekeeper updated';
        this.startCreate();
        this.load();
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Could not save the account';
      }
    });
  }

  toggleActive(user: any) {
    this.clearMessages();
    this.api.setUserActive(user.id, !user.active).subscribe({
      next: () => {
        this.success = user.active
          ? `${user.username} deactivated`
          : `${user.username} activated`;
        this.load();
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Could not change the account status';
      }
    });
  }

  remove(user: any) {
    if (!confirm(`Delete beekeeper "${user.username}"? This cannot be undone.`)) {
      return;
    }
    this.clearMessages();
    this.api.deleteUser(user.id).subscribe({
      next: () => {
        this.success = `${user.username} deleted`;
        if (this.editingId === user.id) {
          this.startCreate();
        }
        this.load();
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Could not delete the account';
      }
    });
  }

  cancelEdit() {
    this.startCreate();
  }

  private clearMessages() {
    this.error = '';
    this.success = '';
  }
}
