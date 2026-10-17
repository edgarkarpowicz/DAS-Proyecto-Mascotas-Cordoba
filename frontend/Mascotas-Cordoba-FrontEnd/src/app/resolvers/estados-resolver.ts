import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { catchError, of } from 'rxjs';
import { PublicacionesResource } from '../api/resources/publicaciones-resource';
import { IEstadoPublicacion } from '../api/models/i-estado-publicacion';

export const estadosResolver: ResolveFn<IEstadoPublicacion[]> = () => {
  const resource = inject(PublicacionesResource);

  return resource.getEstadosPublicacion().pipe(
    catchError(() => of([
      { codEstado: 'Activa', descripcion: 'Publicación activa para adopción' },
      { codEstado: 'Pausada', descripcion: 'Publicación temporalmente en pausa' },
      { codEstado: 'Finalizada', descripcion: 'Adopción concretada o retirada' }
    ]))
  );
};
