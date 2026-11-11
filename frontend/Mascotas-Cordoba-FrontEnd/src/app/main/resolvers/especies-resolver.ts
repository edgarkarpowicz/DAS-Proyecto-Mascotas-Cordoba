import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { PublicacionesResource } from '../api/resources/publicaciones-resource';
import { IEspecie } from '../api/models/i-especie';

export const especiesResolver: ResolveFn<IEspecie[]> = () => {
  return inject(PublicacionesResource).getEspecies();
};

