import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

/**
 * Pantalla base para módulos RF aún no implementados: conserva el marco
 * institucional y documenta contrato esperado, permisos y siguiente paso.
 */
@Component({
  selector: 'app-rf-placeholder-page',
  imports: [RouterLink, NavbarComponent],
  templateUrl: './rf-placeholder-page.component.html',
  styleUrls: ['./rf-placeholder-page.component.css']
})
export class RfPlaceholderPageComponent {

  readonly rfCodigo = input.required<string>();
  readonly titulo = input.required<string>();
  readonly descripcion = input.required<string>();
  readonly endpointEsperado = input.required<string>();
  readonly perfilRequerido = input<string>('Público');

}
