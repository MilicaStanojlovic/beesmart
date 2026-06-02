import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { DiagnosisComponent } from './components/diagnosis/diagnosis.component';
import { BackwardComponent } from './components/backward/backward.component';
import { CepMonitorComponent } from './components/cep-monitor/cep-monitor.component';
import { TemplatesComponent } from './components/templates/templates.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'diagnosis', component: DiagnosisComponent },
  { path: 'backward', component: BackwardComponent },
  { path: 'cep', component: CepMonitorComponent },
  { path: 'templates', component: TemplatesComponent }
];
