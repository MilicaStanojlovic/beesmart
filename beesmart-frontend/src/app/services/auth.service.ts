import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, AuthUser, RegisterPayload, Role } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api';

  private readonly TOKEN_KEY = 'beesmart_token';
  private readonly USER_KEY = 'beesmart_user';

  private userSubject = new BehaviorSubject<AuthUser | null>(this.readStoredUser());
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, { username, password })
      .pipe(tap(res => this.storeSession(res)));
  }

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/register`, payload)
      .pipe(tap(res => this.storeSession(res)));
  }

  /** Clears the session without navigating - used by the interceptor. */
  clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.userSubject.next(null);
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  currentUser(): AuthUser | null {
    return this.userSubject.value;
  }

  /** True only when a token exists and has not expired. */
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token || !this.currentUser()) {
      return false;
    }
    return !this.isExpired(token);
  }

  hasRole(role: Role): boolean {
    return this.isLoggedIn() && this.currentUser()?.role === role;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isBeekeeper(): boolean {
    return this.hasRole('BEEKEEPER');
  }

  /** Landing route for the signed-in role. */
  homeRoute(): string {
    if (this.isAdmin()) {
      return '/users';
    }
    if (this.isBeekeeper()) {
      return '/dashboard';
    }
    return '/login';
  }

  private storeSession(res: AuthResponse): void {
    const user: AuthUser = {
      id: res.id,
      username: res.username,
      fullName: res.fullName,
      role: res.role
    };
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.userSubject.next(user);
  }

  private readStoredUser(): AuthUser | null {
    const raw = localStorage.getItem(this.USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      // Corrupt storage must not brick the app.
      localStorage.removeItem(this.USER_KEY);
      localStorage.removeItem(this.TOKEN_KEY);
      return null;
    }
  }

  /** Reads the exp claim from the JWT payload without pulling in a library. */
  private isExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return typeof payload.exp !== 'number' || payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
