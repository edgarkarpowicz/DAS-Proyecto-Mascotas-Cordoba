import { Routes } from '@angular/router';
import { authGuard } from '../core/guards/auth-guard';
import { roleGuard } from '../core/guards/role-guard';
import { SanidadResource } from './api/sanidad-resource';

export const SANIDAD_ROUTES: Routes = [
  {
    path: '',
    providers: [SanidadResource],
    children: [
      {
        path: 'atenciones',
        canActivate: [roleGuard('VETERINARIA', 'MUNICIPALIDAD')],
        loadComponent: () => import('./pages/atenciones/atenciones.component').then(m => m.AtencionesComponent)
      },
      {
        path: 'carnet',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/carnet/carnet.component').then(m => m.CarnetComponent)
      },
      {
        path: 'validar-carnet',
        loadComponent: () => import('./pages/validar-carnet/validar-carnet.component').then(m => m.ValidarCarnetComponent)
      }
    ]
  }
];
