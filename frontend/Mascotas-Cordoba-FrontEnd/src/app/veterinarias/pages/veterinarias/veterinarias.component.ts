import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-veterinarias',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF17"
      titulo="Veterinarias adheridas"
      descripcion="Directorio público de clínicas veterinarias habilitadas, horarios y prestaciones del programa."
      endpointEsperado="GET /publico/veterinarias"
      perfilRequerido="Público"
    />
  `
})
export class VeterinariasComponent {}
