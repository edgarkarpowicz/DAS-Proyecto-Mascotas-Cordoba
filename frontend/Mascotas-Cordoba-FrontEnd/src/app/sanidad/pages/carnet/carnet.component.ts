import { Component } from '@angular/core';
import { RfPlaceholderPageComponent } from '../../../shared/components/rf-placeholder-page/rf-placeholder-page.component';

@Component({
  selector: 'app-carnet',
  imports: [RfPlaceholderPageComponent],
  template: `
    <app-rf-placeholder-page
      rfCodigo="RF10 / RF11"
      titulo="Carnet sanitario digital"
      descripcion="Generación y visualización del carnet sanitario digital vigente de una mascota por su NRM."
      endpointEsperado="POST|GET /sanidad/carnets/{nroRegMunicipal}"
      perfilRequerido="Usuario autenticado (CIUDADANO / VETERINARIA / REFUGIO / MUNICIPALIDAD)"
    />
  `
})
export class CarnetComponent {}
