import { Routes } from '@angular/router';
import { PortalResource } from './api/portal-resource';
import { portalResolver } from './resolvers/portal-resolver';

export const PORTAL_ROUTES: Routes = [
  {
    path: '',
    providers: [PortalResource],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/portal/portal.component').then(m => m.PortalComponent),
        resolve: {
          portal: portalResolver
        }
      }
    ]
  }
];
