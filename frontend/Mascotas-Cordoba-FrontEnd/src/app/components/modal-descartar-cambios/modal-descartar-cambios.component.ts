import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmDialogService } from '../../services/confirm-dialog.service';

@Component({
  selector: 'app-modal-descartar-cambios',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-descartar-cambios.component.html',
  styleUrls: ['./modal-descartar-cambios.component.css']
})
export class ModalDescartarCambiosComponent {

  private readonly _confirmService = inject(ConfirmDialogService);
  readonly data = this._confirmService.dialogData;

  cancel(): void {
    this._confirmService.resolve(false);
  }

  confirm(): void {
    this._confirmService.resolve(true);
  }

}
