import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { PublicacionesComponent } from './pages/publicaciones/publicaciones.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'publicaciones', component: PublicacionesComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
