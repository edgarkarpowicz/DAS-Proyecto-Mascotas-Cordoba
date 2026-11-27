import { Routes } from '@angular/router';
import { RefugiosResource } from './api/refugios-resource';

export const REFUGIOS_ROUTES: Routes = [
  {
    path: '',
    providers: [RefugiosResource],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/refugios/refugios.component').then(m => m.RefugiosComponent)
      }
    ]
  }
];
