import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { IMessage } from '../../services/app-message-service';

/**
 * Diálogo para errores y avisos que el usuario tiene que leer.
 * Se abre por referencia desde AppMessageService.
 */
@Component({
  selector: 'app-message-dialog',
  templateUrl: './message-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./message-dialog.component.css']
})
export class MessageDialogComponent {

  private readonly _dialogRef = inject(MatDialogRef<MessageDialogComponent>);
  readonly msg = inject<IMessage>(MAT_DIALOG_DATA);

  close(): void {
    this._dialogRef.close();
  }

}
