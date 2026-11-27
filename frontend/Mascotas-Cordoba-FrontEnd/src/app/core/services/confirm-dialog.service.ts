import { Injectable, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, map } from 'rxjs';
import { ConfirmDialogComponent, IConfirmDialogData } from '../dialogs/confirm-dialog/confirm-dialog.component';

export type { IConfirmDialogData };

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {

  private readonly _dialog = inject(MatDialog);

  /**
   * Abre el diálogo de confirmación por referencia y emite true solo si el
   * usuario acepta (cerrar con Esc o con el fondo cuenta como cancelar).
   */
  confirm(
    title: string = 'Descartar cambios',
    message: string = '¿Está seguro de descartar sus cambios?',
    confirmText: string = 'Aceptar'
  ): Observable<boolean> {
    const ref = this._dialog.open<ConfirmDialogComponent, IConfirmDialogData, boolean>(ConfirmDialogComponent, {
      data: { title, message, confirmText, cancelText: 'Cancelar' },
      width: '420px'
    });
    return ref.afterClosed().pipe(map(result => result === true));
  }

}
