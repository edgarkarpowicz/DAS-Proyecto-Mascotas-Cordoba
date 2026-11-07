import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PublicacionesResource } from '../api/resources/publicaciones-resource';
import { IEspecie } from '../api/models/i-especie';

export const especiesResolver: ResolveFn<IEspecie[]> = (): Observable<IEspecie[]> => {
  const pubResource = inject(PublicacionesResource);
  return pubResource.getEspecies().pipe(
    catchError(() => of([
      { nroValorDominio: 1, nomEspecie: 'Perro' },
      { nroValorDominio: 2, nomEspecie: 'Gato' },
      { nroValorDominio: 3, nomEspecie: 'Conejo' },
      { nroValorDominio: 4, nomEspecie: 'Otro' }
    ]))
  );
};

