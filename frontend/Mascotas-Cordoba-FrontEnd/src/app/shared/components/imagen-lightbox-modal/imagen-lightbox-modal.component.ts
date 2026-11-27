import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { applyFallbackFoto } from '../../utils/mascota-formatters';

export interface IImagenLightboxData {
  imageUrl: string;
  altText?: string;
  fallbackUrl?: string;
  caption?: string;
}

/**
 * Vista ampliada de una foto. Se abre por referencia con
 * ImagenLightboxModalComponent.abrir(dialog, datos).
 */
@Component({
  selector: 'app-imagen-lightbox-modal',
  templateUrl: './imagen-lightbox-modal.component.html',
  styleUrls: ['./imagen-lightbox-modal.component.css']
})
export class ImagenLightboxModalComponent {

  private readonly _dialogRef = inject(MatDialogRef<ImagenLightboxModalComponent>);
  readonly data = inject<IImagenLightboxData>(MAT_DIALOG_DATA);

  static abrir(dialog: MatDialog, data: IImagenLightboxData): MatDialogRef<ImagenLightboxModalComponent> {
    return dialog.open(ImagenLightboxModalComponent, {
      data,
      maxWidth: '96vw',
      panelClass: 'dialogo-lightbox',
      backdropClass: 'fondo-lightbox',
      autoFocus: 'dialog'
    });
  }

  get altText(): string {
    return this.data.altText || 'Fotografía de mascota';
  }

  cerrar(): void {
    this._dialogRef.close();
  }

  onImageError(event: Event): void {
    applyFallbackFoto(event, undefined, this.data.fallbackUrl);
  }

}
