export type Role = 'ADMIN' | 'BEEKEEPER';

export interface AuthUser {
  id: number;
  username: string;
  fullName: string;
  role: Role;
}

export interface AuthResponse {
  token: string;
  id: number;
  username: string;
  fullName: string;
  role: Role;
}

export interface RegisterPayload {
  username: string;
  password: string;
  fullName: string;
  email: string;
}

export interface AppUser {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: Role;
  active: boolean;
  createdAt: string;
}
