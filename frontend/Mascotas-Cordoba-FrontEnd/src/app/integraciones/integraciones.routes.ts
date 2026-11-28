import { Routes } from '@angular/router';
import { roleGuard } from '../core/guards/role-guard';
import { IntegracionesResource } from './api/integraciones-resource';

export const INTEGRACIONES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [roleGuard('MUNICIPALIDAD')],
    providers: [IntegracionesResource],
    loadComponent: () => import('./pages/integraciones/integraciones.component').then(m => m.IntegracionesComponent)
  }
];
