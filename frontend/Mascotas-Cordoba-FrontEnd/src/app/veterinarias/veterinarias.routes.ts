import { Routes } from '@angular/router';
import { VeterinariasResource } from './api/veterinarias-resource';

export const VETERINARIAS_ROUTES: Routes = [
  {
    path: '',
    providers: [VeterinariasResource],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/veterinarias/veterinarias.component').then(m => m.VeterinariasComponent)
      }
    ]
  }
];
