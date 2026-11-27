import { Injectable, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageDialogComponent } from '../dialogs/message-dialog/message-dialog.component';

export interface IMessage {
  text: string;
  num?: number;
  title?: string;
  type?: 'error' | 'info' | 'warning' | 'success';
}

/**
 * Punto único para avisar al usuario:
 * - showMessage: diálogo para errores y avisos que hay que leer.
 * - notify: snackbar breve para confirmar que una acción salió bien,
 *   así una operación exitosa no abre un modal extra.
 */
@Injectable({
  providedIn: 'root'
})
export class AppMessageService {

  private readonly _dialog = inject(MatDialog);
  private readonly _snackBar = inject(MatSnackBar);

  showMessage(message: IMessage): void {
    this._dialog.open(MessageDialogComponent, {
      data: message,
      width: '440px'
    });
  }

  notify(text: string): void {
    this._snackBar.open(text, 'Cerrar', {
      duration: 3500,
      panelClass: 'snackbar-exito',
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }

}
