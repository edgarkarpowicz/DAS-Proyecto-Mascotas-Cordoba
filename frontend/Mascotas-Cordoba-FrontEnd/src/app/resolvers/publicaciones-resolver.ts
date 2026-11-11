import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { PublicacionesResource } from '../api/resources/publicaciones-resource';
import { AuthService } from '../services/auth.service';
import { IPublicacionAdopcion } from '../api/models/i-publicacion-adopcion';

export const publicacionesResolver: ResolveFn<IPublicacionAdopcion[]> = () => {
  const idRefugio = inject(AuthService).idRefugio();
  return inject(PublicacionesResource).getPublicacionesPorRefugio(idRefugio);
};
