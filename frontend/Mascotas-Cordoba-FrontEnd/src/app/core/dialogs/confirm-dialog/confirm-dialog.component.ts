import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface IConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}

/**
 * Diálogo de confirmación genérico. Se abre por referencia desde
 * ConfirmDialogService y se cierra devolviendo true (acepta) o false.
 */
@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent {

  private readonly _dialogRef = inject(MatDialogRef<ConfirmDialogComponent, boolean>);
  readonly data = inject<IConfirmDialogData>(MAT_DIALOG_DATA);

  cancel(): void {
    this._dialogRef.close(false);
  }

  confirm(): void {
    this._dialogRef.close(true);
  }

}
