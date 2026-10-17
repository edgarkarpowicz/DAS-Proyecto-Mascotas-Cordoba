import { inject } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { ConfirmDialogService } from '../services/confirm-dialog.service';

export interface HasChangesComponent {
  hasChanges(): boolean;
}

export const controlarCambiosGuard: CanDeactivateFn<HasChangesComponent> = (component) => {
  if (!component || typeof component.hasChanges !== 'function' || !component.hasChanges()) {
    return true;
  }

  const confirmService = inject(ConfirmDialogService);
  return confirmService.confirm(
    'Descartar cambios',
    '¿Está seguro de descartar sus cambios? Se perderán las modificaciones no guardadas.'
  );
};
