import { Routes } from '@angular/router';
import { roleGuard } from '../core/guards/role-guard';
import { controlarCambiosGuard } from '../core/guards/controlar-cambios-guard';
import { publicacionesResolver } from './resolvers/publicaciones-resolver';
import { estadosResolver } from './resolvers/estados-resolver';
import { PublicacionesResource } from './api/publicaciones-resource';
import { EstadoPublicacionService } from './services/estado-publicacion.service';

export const PUBLICACIONES_ROUTES: Routes = [
  {
    path: '',
    providers: [PublicacionesResource, EstadoPublicacionService],
    canActivate: [roleGuard('REFUGIO')],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/publicaciones/publicaciones.component').then(m => m.PublicacionesComponent),
        canDeactivate: [controlarCambiosGuard],
        resolve: {
          publicaciones: publicacionesResolver,
          estados: estadosResolver
        }
      }
    ]
  }
];
