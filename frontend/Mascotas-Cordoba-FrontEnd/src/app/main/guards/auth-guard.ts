import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  const perfilesPermitidos = route?.data?.['perfilesPermitidos'] as string[] | undefined;
  if (perfilesPermitidos && perfilesPermitidos.length > 0 && !authService.hasPerfil(...perfilesPermitidos)) {
    return router.createUrlTree(['/login']);
  }

  return true;
};
