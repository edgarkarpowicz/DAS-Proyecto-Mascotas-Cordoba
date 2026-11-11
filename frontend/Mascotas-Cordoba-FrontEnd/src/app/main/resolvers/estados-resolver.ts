import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { PublicacionesResource } from '../api/resources/publicaciones-resource';
import { IEstadoPublicacion } from '../api/models/i-estado-publicacion';

export const estadosResolver: ResolveFn<IEstadoPublicacion[]> = () => {
  return inject(PublicacionesResource).getEstadosPublicacion();
};
