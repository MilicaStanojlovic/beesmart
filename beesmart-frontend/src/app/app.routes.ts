import { inject } from '@angular/core';
import { Routes } from '@angular/router';
import { AuthService } from './services/auth.service';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { DiagnosisComponent } from './components/diagnosis/diagnosis.component';
import { BackwardComponent } from './components/backward/backward.component';
import { CepMonitorComponent } from './components/cep-monitor/cep-monitor.component';
import { TemplatesComponent } from './components/templates/templates.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { UsersComponent } from './components/users/users.component';
import { authGuard, guestGuard, roleGuard } from './guards/auth.guard';

// Beekeeping pages are open to both roles: the administrator can look at everything
// a beekeeper can, on top of managing accounts.
const BOTH_ROLES = { roles: ['BEEKEEPER', 'ADMIN'] };
const ADMIN_ONLY = { roles: ['ADMIN'] };

export const routes: Routes = [
  // Sends each role to its own landing page (admin -> /users, beekeeper -> /dashboard).
  { path: '', pathMatch: 'full', redirectTo: () => inject(AuthService).homeRoute() },

  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },

  // Beekeeping features - everything that existed before authentication was added.
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard, roleGuard], data: BOTH_ROLES },
  { path: 'diagnosis', component: DiagnosisComponent, canActivate: [authGuard, roleGuard], data: BOTH_ROLES },
  { path: 'backward', component: BackwardComponent, canActivate: [authGuard, roleGuard], data: BOTH_ROLES },
  { path: 'cep', component: CepMonitorComponent, canActivate: [authGuard, roleGuard], data: BOTH_ROLES },
  { path: 'templates', component: TemplatesComponent, canActivate: [authGuard, roleGuard], data: BOTH_ROLES },

  // Administration
  { path: 'users', component: UsersComponent, canActivate: [authGuard, roleGuard], data: ADMIN_ONLY },

  { path: '**', redirectTo: '' }
];
