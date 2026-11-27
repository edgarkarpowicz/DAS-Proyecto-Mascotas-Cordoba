import { Routes } from '@angular/router';
import { AsistenteResource } from './api/asistente-resource';

export const ASISTENTE_ROUTES: Routes = [
  {
    path: '',
    providers: [AsistenteResource],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/asistente/asistente.component').then(m => m.AsistenteComponent)
      }
    ]
  }
];
