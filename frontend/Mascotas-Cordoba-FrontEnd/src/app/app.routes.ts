import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { PublicacionesComponent } from './pages/publicaciones/publicaciones.component';
import { authGuard } from './guards/auth-guard';
import { controlarCambiosGuard } from './guards/controlar-cambios-guard';
import { publicacionesResolver } from './resolvers/publicaciones-resolver';
import { estadosResolver } from './resolvers/estados-resolver';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'publicaciones',
    component: PublicacionesComponent,
    canActivate: [authGuard],
    canDeactivate: [controlarCambiosGuard],
    resolve: {
      publicaciones: publicacionesResolver,
      estados: estadosResolver
    }
  },
  { path: '', redirectTo: 'publicaciones', pathMatch: 'full' },
  { path: '**', redirectTo: 'publicaciones' }
];

