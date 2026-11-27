import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-mis-mascotas',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF20"
      titulo="Mis mascotas"
      descripcion="Consulta del ciudadano sobre sus mascotas registradas en el programa municipal y acceso a sus carnets sanitarios."
      endpointEsperado="GET /mascotas/mias"
      perfilRequerido="CIUDADANO"
    />
  `
})
export class MisMascotasComponent {}
