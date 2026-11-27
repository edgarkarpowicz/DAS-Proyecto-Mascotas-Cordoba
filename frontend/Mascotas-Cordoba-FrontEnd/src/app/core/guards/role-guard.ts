import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PerfilUsuario } from '../api/models/i-usuario';
import { getHomeRouteForPerfil } from '../config/perfil-routes';
import { AuthService } from '../services/auth.service';

export function roleGuard(...perfilesPermitidos: PerfilUsuario[]): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.isAuthenticated()) {
      return router.createUrlTree(['/login']);
    }

    if (perfilesPermitidos.length > 0 && !authService.hasPerfil(...perfilesPermitidos)) {
      return router.createUrlTree([getHomeRouteForPerfil(authService.perfil())]);
    }

    return true;
  };
}
