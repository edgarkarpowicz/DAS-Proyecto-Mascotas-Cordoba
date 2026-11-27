import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { of } from 'rxjs';
import { PublicacionesResource } from '../api/publicaciones-resource';
import { AuthService } from '../../core/services/auth.service';
import { IPublicacionAdopcion } from '../api/models/i-publicacion-adopcion';

export const publicacionesResolver: ResolveFn<IPublicacionAdopcion[]> = () => {
  const idRefugio = inject(AuthService).idRefugio();
  if (idRefugio == null) {
    return of([]);
  }
  return inject(PublicacionesResource).getPublicacionesPorRefugio(idRefugio);
};
