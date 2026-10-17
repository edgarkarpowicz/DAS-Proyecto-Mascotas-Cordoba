import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { catchError, of } from 'rxjs';
import { PublicacionesResource } from '../api/resources/publicaciones-resource';
import { AuthService } from '../services/auth.service';
import { IMascotaDisponible } from '../api/models/i-mascota-disponible';

export const mascotasDisponiblesResolver: ResolveFn<IMascotaDisponible[]> = () => {
  const resource = inject(PublicacionesResource);
  const authService = inject(AuthService);
  const idRefugio = authService.idRefugio();

  return resource.getMascotasDisponibles(idRefugio).pipe(
    catchError(() => of([]))
  );
};
