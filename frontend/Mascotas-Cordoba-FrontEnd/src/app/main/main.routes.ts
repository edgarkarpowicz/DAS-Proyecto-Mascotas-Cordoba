import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard';
import { controlarCambiosGuard } from './guards/controlar-cambios-guard';
import { publicacionesResolver } from './resolvers/publicaciones-resolver';
import { estadosResolver } from './resolvers/estados-resolver';
import { especiesResolver } from './resolvers/especies-resolver';
import { PublicacionesResource } from './api/resources/publicaciones-resource';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'publicaciones',
    loadComponent: () => import('./pages/publicaciones/publicaciones.component').then(m => m.PublicacionesComponent),
    canActivate: [authGuard],
    canDeactivate: [controlarCambiosGuard],
    resolve: {
      publicaciones: publicacionesResolver,
      estados: estadosResolver,
      especies: especiesResolver
    },
    providers: [PublicacionesResource]
  },
  { path: '', redirectTo: 'publicaciones', pathMatch: 'full' }
];
