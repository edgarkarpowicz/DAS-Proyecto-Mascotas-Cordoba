import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-refugios',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF18"
      titulo="Refugios adheridos y adopciones"
      descripcion="Directorio público de refugios habilitados y mascotas con publicación de adopción Activa."
      endpointEsperado="GET /publico/refugios"
      perfilRequerido="Público"
    />
  `
})
export class RefugiosComponent {}
