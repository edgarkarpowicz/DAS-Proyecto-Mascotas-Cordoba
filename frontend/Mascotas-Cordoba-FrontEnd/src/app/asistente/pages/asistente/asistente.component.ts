import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-asistente',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF21"
      titulo="Asistente virtual"
      descripcion="Consultas al asistente virtual con contexto público del programa o personalizado por sesión vía RAG."
      endpointEsperado="POST /publico/asistente/consultar"
      perfilRequerido="Público (personalizado si hay sesión)"
    />
  `
})
export class AsistenteComponent {}
