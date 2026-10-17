import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { catchError, of } from 'rxjs';
import { PublicacionesResource } from '../api/resources/publicaciones-resource';
import { AuthService } from '../services/auth.service';
import { IPublicacionAdopcion } from '../api/models/i-publicacion-adopcion';

export const publicacionesResolver: ResolveFn<IPublicacionAdopcion[]> = () => {
  const publicacionesResource = inject(PublicacionesResource);
  const authService = inject(AuthService);
  const idRefugio = authService.idRefugio();

  return publicacionesResource.getPublicacionesPorRefugio(idRefugio).pipe(
    catchError(() => of([]))
  );
};
