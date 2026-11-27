import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-registro-mascotas',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF06"
      titulo="Registro único de mascotas"
      descripcion="Alta manual o por sincronización veterinaria con verificación de duplicados y asignación de NRM."
      endpointEsperado="POST /mascotas"
      perfilRequerido="VETERINARIA / MUNICIPALIDAD / REFUGIO"
    />
  `
})
export class RegistroMascotasComponent {}
