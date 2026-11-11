import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./main/main.routes').then(m => m.routes)
  },
  { path: '**', redirectTo: 'publicaciones' }
];
