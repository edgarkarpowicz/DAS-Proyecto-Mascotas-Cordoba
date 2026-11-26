import { Component, Injector, computed, inject, input, linkedSignal, ChangeDetectionStrategy } from '@angular/core';
import { LowerCasePipe } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { NuevaPublicacionModalComponent } from '../../components/nueva-publicacion-modal/nueva-publicacion-modal.component';
import {
  DetallePublicacionModalComponent,
  IDetallePublicacionData
} from '../../components/detalle-publicacion-modal/detalle-publicacion-modal.component';
import { ExpandImageButtonComponent } from '../../../shared/components/expand-image-button/expand-image-button.component';
import { ImagenLightboxModalComponent } from '../../../shared/components/imagen-lightbox-modal/imagen-lightbox-modal.component';
import { PublicacionesResource } from '../../api/publicaciones-resource';
import { EstadoPublicacion, EstadoPublicacionService } from '../../services/estado-publicacion.service';
import { AuthService } from '../../../core/services/auth.service';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
import { FiltrarPorPipe } from '../../../shared/pipes/filtrar-por-pipe';
import {
  applyFallbackFoto,
  formatFotoCaption,
  getDefaultFotoMascota,
  formatFechaPublicacion,
  getEdadTexto,
  getFotoMascotaUrl,
  getSexoLabel
} from '../../../shared/utils/mascota-formatters';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';
import { IEstadoPublicacion } from '../../api/models/i-estado-publicacion';
import { HasChangesComponent } from '../../../core/guards/controlar-cambios-guard';

const ETIQUETA_PESTANIA: Record<string, string> = {
  Activa: 'Activas',
  Pausada: 'Pausadas',
  Finalizada: 'Finalizadas'
};

@Component({
  selector: 'app-publicaciones',
  imports: [
    LowerCasePipe,
    ReactiveFormsModule,
    NavbarComponent,
    ExpandImageButtonComponent,
    AutofocusDirective,
    FiltrarPorPipe
  ],
  templateUrl: './publicaciones.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./publicaciones.component.css']
})
export class PublicacionesComponent implements HasChangesComponent {

  private readonly _dialog = inject(MatDialog);
  private readonly _injector = inject(Injector);
  private readonly _pubResource = inject(PublicacionesResource);
  private readonly _authService = inject(AuthService);
  readonly usuario = this._authService.usuario;
  private readonly _estadoService = inject(EstadoPublicacionService);

  private _nuevaRef: MatDialogRef<NuevaPublicacionModalComponent, boolean> | null = null;

  // Datos de la pantalla resueltos al navegar (withComponentInputBinding)
  readonly publicacionesResueltas = input<IPublicacionAdopcion[]>([], { alias: 'publicaciones' });
  readonly estados = input<IEstadoPublicacion[]>([]);

  // Copia local editable que se reinicia si el resolver vuelve a ejecutarse
  readonly publicaciones = linkedSignal(() => this.publicacionesResueltas() ?? []);

  // Filtros
  readonly busqueda = new FormControl('', { nonNullable: true });
  readonly estadoFiltro = new FormControl('Todos', { nonNullable: true });
  readonly searchQuery = toSignal(this.busqueda.valueChanges, { initialValue: '' });
  private readonly _estadoSeleccionado = toSignal(this.estadoFiltro.valueChanges, { initialValue: 'Todos' });

  readonly estadoSeleccionado = this._estadoSeleccionado;

  readonly publicacionesPorEstado = computed(() => {
    const estado = this._estadoSeleccionado();
    const lista = this.publicaciones();
    return estado === 'Todos' ? lista : lista.filter(p => p.estadoPublicacion === estado);
  });

  /** Pestañas de filtro con la cantidad de publicaciones en cada estado. */
  readonly pestanias = computed(() => {
    const lista = this.publicaciones();
    const estados = (this.estados() ?? []).map(e => e.codEstado);
    return [
      { valor: 'Todos', etiqueta: 'Todas', cantidad: lista.length },
      ...estados.map(codigo => ({
        valor: codigo,
        etiqueta: ETIQUETA_PESTANIA[codigo] ?? codigo,
        cantidad: lista.filter(p => p.estadoPublicacion === codigo).length
      }))
    ];
  });

  filtrarPorEstado(estado: string): void {
    this.estadoFiltro.setValue(estado);
  }

  limpiarFiltros(): void {
    this.busqueda.setValue('');
    this.estadoFiltro.setValue('Todos');
  }

  hasChanges(): boolean {
    return this._nuevaRef?.componentInstance?.hasChanges() ?? false;
  }

  // ------------------------------------------------------------- diálogos

  abrirNuevaModal(): void {
    this._nuevaRef = this._dialog.open<NuevaPublicacionModalComponent, void, boolean>(NuevaPublicacionModalComponent, {
      injector: this._injector,
      width: '600px',
      maxWidth: 'calc(100vw - 1rem)',
      panelClass: 'dialogo-app',
      backdropClass: 'fondo-dialogo',
      disableClose: true,
      autoFocus: false
    });
    this._nuevaRef.afterClosed().subscribe(creada => {
      this._nuevaRef = null;
      if (creada) {
        this.recargarPublicaciones();
      }
    });
  }

  abrirDetalleModal(pub: IPublicacionAdopcion): void {
    this._dialog.open<DetallePublicacionModalComponent, IDetallePublicacionData, IPublicacionAdopcion>(
      DetallePublicacionModalComponent,
      {
        injector: this._injector,
        data: { publicacion: pub },
        width: '680px',
        maxWidth: 'calc(100vw - 1rem)',
        panelClass: 'dialogo-app',
        backdropClass: 'fondo-dialogo',
        disableClose: true,
        autoFocus: 'dialog'
      }
    ).afterClosed().subscribe(actualizada => {
      if (actualizada) {
        this.reemplazar(actualizada);
      }
    });
  }

  abrirImagenAmpliada(pub: IPublicacionAdopcion): void {
    ImagenLightboxModalComponent.abrir(this._dialog, {
      imageUrl: this.getFotoMascota(pub),
      fallbackUrl: getDefaultFotoMascota(pub.especie),
      altText: pub.nombreMascota,
      caption: formatFotoCaption(pub.nombreMascota, pub.especie)
    });
  }

  // ------------------------------------------------------- estado (grilla)

  acciones(pub: IPublicacionAdopcion) {
    return this._estadoService.accionesDisponibles(pub.estadoPublicacion);
  }

  cambiarEstado(pub: IPublicacionAdopcion, nuevoEstado: EstadoPublicacion): void {
    this._estadoService.cambiarEstado(pub, nuevoEstado).subscribe(actualizada => this.reemplazar(actualizada));
  }

  private reemplazar(actualizada: IPublicacionAdopcion): void {
    this.publicaciones.update(lista =>
      lista.map(p => p.nroPublicacion === actualizada.nroPublicacion ? actualizada : p)
    );
  }

  private recargarPublicaciones(): void {
    this._pubResource.getPublicacionesPorRefugio(this._authService.idRefugio())
      .subscribe(actualizadas => this.publicaciones.set(actualizadas));
  }

  // ------------------------------------------------------------ formato

  getEdadTexto(pub: IPublicacionAdopcion): string {
    return getEdadTexto(pub.edadAproximada, pub.añoNacimiento, pub.fechaNacimiento);
  }

  getSexo(pub: IPublicacionAdopcion): 'Hembra' | 'Macho' {
    return getSexoLabel(pub.sexo);
  }

  getFechaPublicacion(pub: IPublicacionAdopcion): string {
    return formatFechaPublicacion(pub.fechaPublicacion);
  }

  getFotoMascota(pub: IPublicacionAdopcion): string {
    return getFotoMascotaUrl(pub.foto, pub.especie);
  }

  onImageError(event: Event, pub: IPublicacionAdopcion): void {
    applyFallbackFoto(event, pub.especie);
  }

}
