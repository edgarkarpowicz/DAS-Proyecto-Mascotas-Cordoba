import { Component, computed, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';
import { EstadoPublicacion, EstadoPublicacionService } from '../../services/estado-publicacion.service';
import { ExpandImageButtonComponent } from '../../../shared/components/expand-image-button/expand-image-button.component';
import { ImagenLightboxModalComponent } from '../../../shared/components/imagen-lightbox-modal/imagen-lightbox-modal.component';
import {
  applyFallbackFoto,
  formatFechaNacimiento,
  formatFechaPublicacion,
  formatFotoCaption,
  getDefaultFotoMascota,
  getEdadTexto,
  getFotoMascotaUrl,
  getSexoLabel
} from '../../../shared/utils/mascota-formatters';

export interface IDetallePublicacionData {
  publicacion: IPublicacionAdopcion;
}

/**
 * Detalle de una publicación. Se abre por referencia y, si el usuario cambia
 * el estado desde acá, el cambio se graba en este mismo componente. Al cerrar
 * devuelve la publicación actualizada para que la grilla la refleje.
 */
@Component({
  selector: 'app-detalle-publicacion-modal',
  imports: [ExpandImageButtonComponent],
  templateUrl: './detalle-publicacion-modal.component.html',
  styleUrls: ['./detalle-publicacion-modal.component.css']
})
export class DetallePublicacionModalComponent {

  private readonly _dialogRef = inject(MatDialogRef<DetallePublicacionModalComponent, IPublicacionAdopcion>);
  private readonly _dialog = inject(MatDialog);
  private readonly _estadoService = inject(EstadoPublicacionService);
  private readonly _data = inject<IDetallePublicacionData>(MAT_DIALOG_DATA);

  readonly publicacion = signal<IPublicacionAdopcion>(this._data.publicacion);
  readonly acciones = computed(() => this._estadoService.accionesDisponibles(this.publicacion().estadoPublicacion));
  readonly procesando = signal(false);

  constructor() {
    // Esc o clic en el fondo también devuelven la publicación vigente.
    this._dialogRef.backdropClick().subscribe(() => this.cerrar());
    this._dialogRef.keydownEvents().subscribe(event => {
      if (event.key === 'Escape') {
        this.cerrar();
      }
    });
  }

  cerrar(): void {
    this._dialogRef.close(this.publicacion());
  }

  cambiarEstado(nuevoEstado: EstadoPublicacion): void {
    if (this.procesando()) {
      return;
    }
    this.procesando.set(true);
    this._estadoService.cambiarEstado(this.publicacion(), nuevoEstado).subscribe({
      next: actualizada => this.publicacion.set(actualizada),
      complete: () => this.procesando.set(false)
    });
  }

  abrirImagenAmpliada(): void {
    const pub = this.publicacion();
    ImagenLightboxModalComponent.abrir(this._dialog, {
      imageUrl: this.getFotoUrl(pub),
      fallbackUrl: getDefaultFotoMascota(pub.especie),
      altText: pub.nombreMascota,
      caption: formatFotoCaption(pub.nombreMascota, pub.especie)
    });
  }

  getFotoUrl(pub: IPublicacionAdopcion): string {
    return getFotoMascotaUrl(pub.foto, pub.especie);
  }

  onImageError(event: Event, pub: IPublicacionAdopcion): void {
    applyFallbackFoto(event, pub.especie);
  }

  getEdad(pub: IPublicacionAdopcion): string {
    return getEdadTexto(pub.edadAproximada, pub.añoNacimiento, pub.fechaNacimiento);
  }

  getNacimientoLabel(pub: IPublicacionAdopcion): string | null {
    return formatFechaNacimiento(pub.fechaNacimiento) ?? (pub.añoNacimiento ? String(pub.añoNacimiento) : null);
  }

  getSexo(pub: IPublicacionAdopcion): 'Hembra' | 'Macho' {
    return getSexoLabel(pub.sexo);
  }

  getFecha(pub: IPublicacionAdopcion): string {
    return formatFechaPublicacion(pub.fechaPublicacion);
  }

}
