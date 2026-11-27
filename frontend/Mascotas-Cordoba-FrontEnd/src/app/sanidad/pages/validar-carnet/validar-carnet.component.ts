import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-validar-carnet',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF11"
      titulo="Validación pública de carnet"
      descripcion="Verificación de autenticidad y vigencia de un carnet sanitario digital por código o QR."
      endpointEsperado="GET /publico/carnets/{codigoValidacion}/validar"
      perfilRequerido="Público / Terceros autorizados"
    />
  `
})
export class ValidarCarnetComponent {}
