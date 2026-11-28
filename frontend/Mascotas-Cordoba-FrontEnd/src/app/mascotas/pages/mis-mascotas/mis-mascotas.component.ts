import { Component, OnInit, inject, signal } from '@angular/core';
import { LowerCasePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { IPageResponse } from '../../../shared/models/pagination';
import { getEdadTexto, getSexoLabel } from '../../../shared/utils/mascota-formatters';
import { IMascota } from '../../api/models/i-mascota';
import { MascotasResource } from '../../api/mascotas-resource';

@Component({
  selector: 'app-mis-mascotas',
  imports: [LowerCasePipe, RouterLink, NavbarComponent],
  templateUrl: './mis-mascotas.component.html',
  styleUrls: ['./mis-mascotas.component.css']
})
export class MisMascotasComponent implements OnInit {

  private readonly _resource = inject(MascotasResource);
  private readonly _authService = inject(AuthService);

  readonly usuario = this._authService.usuario;
  readonly page = signal<IPageResponse<IMascota>>({
    items: [],
    pagina: 1,
    tamanioPagina: 12,
    totalElementos: 0,
    totalPaginas: 0
  });
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar(1);
  }

  cargar(pagina = 1): void {
    this.cargando.set(true);
    this.error.set(null);
    this._resource.getMisMascotas({ pagina, tamanioPagina: 12 })
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: res => this.page.set(res),
        error: () => this.error.set('No se pudieron cargar tus mascotas registradas.')
      });
  }

  irAPagina(pagina: number): void {
    if (pagina < 1 || (this.page().totalPaginas > 0 && pagina > this.page().totalPaginas)) {
      return;
    }
    this.cargar(pagina);
  }

  getEdad(mascota: IMascota): string {
    return getEdadTexto(undefined, mascota.añoNacimiento);
  }

  getSexo(mascota: IMascota): 'Hembra' | 'Macho' {
    return getSexoLabel(mascota.sexo);
  }

  getAnioNacimiento(mascota: IMascota): number {
    return mascota.añoNacimiento;
  }
}
