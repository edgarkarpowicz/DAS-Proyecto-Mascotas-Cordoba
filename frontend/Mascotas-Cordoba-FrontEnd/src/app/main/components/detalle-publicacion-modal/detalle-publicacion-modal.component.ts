import { Component, HostListener, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';
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

@Component({
  selector: 'app-detalle-publicacion-modal',
  standalone: true,
  imports: [CommonModule, ExpandImageButtonComponent, ImagenLightboxModalComponent],
  templateUrl: './detalle-publicacion-modal.component.html',
  styleUrls: ['./detalle-publicacion-modal.component.css']
})
export class DetallePublicacionModalComponent {

  publicacion = input.required<IPublicacionAdopcion>();

  close = output<void>();
  toggleEstado = output<IPublicacionAdopcion>();
  finalizar = output<IPublicacionAdopcion>();

  showImageLightbox = signal<boolean>(false);

  @HostListener('document:keydown.escape')
  onEscapeKey(): void {
    if (this.showImageLightbox()) {
      return;
    }
    this.cerrar();
  }

  cerrar(): void {
    this.showImageLightbox.set(false);
    this.close.emit();
  }

  abrirImagenAmpliada(): void {
    this.showImageLightbox.set(true);
  }

  cerrarImagenAmpliada(): void {
    this.showImageLightbox.set(false);
  }

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.cerrar();
    }
  }

  getFotoUrl(pub: IPublicacionAdopcion): string {
    return getFotoMascotaUrl(pub.foto, pub.especie);
  }

  getDefaultFotoUrl(pub: IPublicacionAdopcion): string {
    return getDefaultFotoMascota(pub.especie);
  }

  getLightboxCaption(pub: IPublicacionAdopcion): string {
    return formatFotoCaption(pub.nombreMascota, pub.especie);
  }

  onImageError(event: Event, pub: IPublicacionAdopcion): void {
    applyFallbackFoto(event, pub.especie);
  }

  getEdad(pub: IPublicacionAdopcion): string {
    return getEdadTexto(pub.edadAproximada, pub.añoNacimiento, pub.fechaNacimiento);
  }

  getFechaNacimiento(pub: IPublicacionAdopcion): string | null {
    return formatFechaNacimiento(pub.fechaNacimiento);
  }

  getNacimientoLabel(pub: IPublicacionAdopcion): string | null {
    return this.getFechaNacimiento(pub) ?? (pub.añoNacimiento ? String(pub.añoNacimiento) : null);
  }

  getAnoNacimiento(pub: IPublicacionAdopcion): number | undefined {
    return pub.añoNacimiento;
  }

  getSexo(pub: IPublicacionAdopcion): 'Hembra' | 'Macho' {
    return getSexoLabel(pub.sexo);
  }

  getFecha(pub: IPublicacionAdopcion): string {
    return formatFechaPublicacion(pub.fechaPublicacion);
  }

  onToggleEstado(): void {
    this.toggleEstado.emit(this.publicacion());
  }

  onFinalizar(): void {
    this.finalizar.emit(this.publicacion());
  }

}
