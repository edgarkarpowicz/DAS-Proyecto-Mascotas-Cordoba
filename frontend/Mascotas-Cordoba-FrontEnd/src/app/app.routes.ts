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
  { path: '', redirectTo: 'publicaciones', pathMatch: 'full' },
  { path: '**', redirectTo: 'publicaciones' }
];
