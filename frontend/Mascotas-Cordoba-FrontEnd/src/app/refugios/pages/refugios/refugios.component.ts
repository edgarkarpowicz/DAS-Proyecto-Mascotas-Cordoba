import { Component, OnInit, inject, signal } from '@angular/core';
import { LowerCasePipe } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { finalize } from 'rxjs';
import { IPublicacionAdopcion } from '../../../publicaciones/api/models/i-publicacion-adopcion';
import { ExpandImageButtonComponent } from '../../../shared/components/expand-image-button/expand-image-button.component';
import { ImagenLightboxModalComponent } from '../../../shared/components/imagen-lightbox-modal/imagen-lightbox-modal.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
import { IPageResponse } from '../../../shared/models/pagination';
import {
  applyFallbackFoto,
  formatFotoCaption,
  getDefaultFotoMascota,
  getEdadTexto,
  getFotoMascotaUrl,
  getSexoLabel
} from '../../../shared/utils/mascota-formatters';
import { IRefugioPublico } from '../../api/models/i-refugio-publico';
import { RefugiosResource } from '../../api/refugios-resource';

@Component({
  selector: 'app-refugios',
  imports: [
    LowerCasePipe,
    ReactiveFormsModule,
    NavbarComponent,
    AutofocusDirective,
    ExpandImageButtonComponent
  ],
  templateUrl: './refugios.component.html',
  styleUrls: ['./refugios.component.css']
})
export class RefugiosComponent implements OnInit {

  private readonly _resource = inject(RefugiosResource);
  private readonly _dialog = inject(MatDialog);

  readonly filtroControl = new FormControl('', { nonNullable: true });
  readonly page = signal<IPageResponse<IRefugioPublico>>({
    items: [],
    pagina: 1,
    tamanioPagina: 12,
    totalElementos: 0,
    totalPaginas: 0
  });
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly refugioExpandido = signal<number | null>(null);
  readonly publicacionesPorRefugio = signal<Record<number, IPublicacionAdopcion[]>>({});
  readonly cargandoPublicaciones = signal<number | null>(null);

  ngOnInit(): void {
    this.buscar(1);
  }

  buscar(pagina = 1): void {
    this.cargando.set(true);
    this.error.set(null);
    this._resource.getRefugios(this.filtroControl.value, { pagina, tamanioPagina: 12 })
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: res => this.page.set(res),
        error: () => this.error.set('No se pudieron cargar los refugios adheridos.')
      });
  }

  limpiar(): void {
    this.filtroControl.setValue('');
    this.buscar(1);
  }

  irAPagina(pagina: number): void {
    if (pagina < 1 || (this.page().totalPaginas > 0 && pagina > this.page().totalPaginas)) {
      return;
    }
    this.buscar(pagina);
  }

  togglePublicaciones(idRefugio: number): void {
    if (this.refugioExpandido() === idRefugio) {
      this.refugioExpandido.set(null);
      return;
    }
    this.refugioExpandido.set(idRefugio);
    if (this.publicacionesPorRefugio()[idRefugio]) {
      return;
    }
    this.cargandoPublicaciones.set(idRefugio);
    this._resource.getPublicacionesActivas(idRefugio)
      .pipe(finalize(() => this.cargandoPublicaciones.set(null)))
      .subscribe({
        next: pubs => this.publicacionesPorRefugio.update(prev => ({ ...prev, [idRefugio]: pubs || [] })),
        error: () => this.publicacionesPorRefugio.update(prev => ({ ...prev, [idRefugio]: [] }))
      });
  }

  abrirImagen(pub: IPublicacionAdopcion): void {
    ImagenLightboxModalComponent.abrir(this._dialog, {
      imageUrl: this.getFoto(pub),
      fallbackUrl: getDefaultFotoMascota(pub.especie),
      altText: pub.nombreMascota,
      caption: formatFotoCaption(pub.nombreMascota, pub.especie)
    });
  }

  getFoto(pub: IPublicacionAdopcion): string {
    return getFotoMascotaUrl(pub.foto, pub.especie);
  }

  onImageError(event: Event, pub: IPublicacionAdopcion): void {
    applyFallbackFoto(event, pub.especie);
  }

  getEdad(pub: IPublicacionAdopcion): string {
    return getEdadTexto(pub.edadAproximada, pub.añoNacimiento);
  }

  getSexo(pub: IPublicacionAdopcion): 'Hembra' | 'Macho' {
    return getSexoLabel(pub.sexo);
  }
}
