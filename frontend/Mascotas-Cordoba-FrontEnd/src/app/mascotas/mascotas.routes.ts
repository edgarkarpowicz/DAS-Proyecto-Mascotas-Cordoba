import { Routes } from '@angular/router';
import { roleGuard } from '../core/guards/role-guard';
import { MascotasResource } from './api/mascotas-resource';

export const MASCOTAS_ROUTES: Routes = [
  {
    path: '',
    providers: [MascotasResource],
    children: [
      {
        path: 'mias',
        canActivate: [roleGuard('CIUDADANO')],
        loadComponent: () => import('./pages/mis-mascotas/mis-mascotas.component').then(m => m.MisMascotasComponent)
      },
      {
        path: 'registro',
        canActivate: [roleGuard('VETERINARIA', 'MUNICIPALIDAD', 'REFUGIO')],
        loadComponent: () => import('./pages/registro-mascotas/registro-mascotas.component').then(m => m.RegistroMascotasComponent)
      }
    ]
  }
];
