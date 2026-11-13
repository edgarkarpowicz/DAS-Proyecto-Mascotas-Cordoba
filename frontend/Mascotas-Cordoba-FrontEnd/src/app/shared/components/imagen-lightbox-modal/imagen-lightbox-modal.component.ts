import { Component, HostListener, input, output } from '@angular/core';
import { applyFallbackFoto } from '../../utils/mascota-formatters';

@Component({
  selector: 'app-imagen-lightbox-modal',
  standalone: true,
  templateUrl: './imagen-lightbox-modal.component.html',
  styleUrls: ['./imagen-lightbox-modal.component.css']
})
export class ImagenLightboxModalComponent {

  imageUrl = input.required<string>();
  altText = input<string>('Fotografía de mascota');
  fallbackUrl = input<string>('');
  caption = input<string>('');

  close = output<void>();

  @HostListener('document:keydown.escape')
  onEscapeKey(): void {
    this.cerrar();
  }

  cerrar(): void {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    event.stopPropagation();
    if (event.target === event.currentTarget) {
      this.cerrar();
    }
  }

  onImageError(event: Event): void {
    applyFallbackFoto(event, undefined, this.fallbackUrl());
  }

}

