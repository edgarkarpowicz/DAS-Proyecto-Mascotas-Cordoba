import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'publicaciones',
    loadChildren: () => import('./publicaciones/publicaciones.routes').then(m => m.PUBLICACIONES_ROUTES)
  },
  {
    path: 'mascotas',
    loadChildren: () => import('./mascotas/mascotas.routes').then(m => m.MASCOTAS_ROUTES)
  },
  {
    path: 'sanidad',
    loadChildren: () => import('./sanidad/sanidad.routes').then(m => m.SANIDAD_ROUTES)
  },
  {
    path: 'veterinarias',
    loadChildren: () => import('./veterinarias/veterinarias.routes').then(m => m.VETERINARIAS_ROUTES)
  },
  {
    path: 'refugios',
    loadChildren: () => import('./refugios/refugios.routes').then(m => m.REFUGIOS_ROUTES)
  },
  {
    path: 'asistente',
    loadChildren: () => import('./asistente/asistente.routes').then(m => m.ASISTENTE_ROUTES)
  },
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () => import('./portal/portal.routes').then(m => m.PORTAL_ROUTES)
  },
  { path: '**', redirectTo: '' }
];
