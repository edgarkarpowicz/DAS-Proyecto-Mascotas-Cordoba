import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-atenciones',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF09"
      titulo="Información sanitaria"
      descripcion="Registro y consolidación de vacunaciones, castraciones y prácticas sanitarias del programa municipal."
      endpointEsperado="POST /sanidad/atenciones"
      perfilRequerido="VETERINARIA / MUNICIPALIDAD"
    />
  `
})
export class AtencionesComponent {}
