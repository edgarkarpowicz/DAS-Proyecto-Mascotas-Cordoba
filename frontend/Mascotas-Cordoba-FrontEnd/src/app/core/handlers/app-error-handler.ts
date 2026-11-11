import { ErrorHandler, Injectable, NgZone, inject, isDevMode } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AppMessageService, IMessage } from '../services/app-message-service';

@Injectable()
export class AppErrorHandler implements ErrorHandler {

  private readonly _service = inject(AppMessageService);
  private readonly _zone = inject(NgZone);

  handleError(error: unknown): void {
    error = this.unwrapError(error);

    if (isDevMode()) {
      console.error('[AppErrorHandler]', error);
    }

    const message = this.getMessage(error);

    this._zone.run(() => {
      this._service.showMessage(message);
    });
  }

  private unwrapError(error: unknown): unknown {
    if (typeof error === 'object' && error !== null && 'rejection' in error) {
      return (error as { rejection: unknown }).rejection;
    }
    return error;
  }

  private getMessage(error: unknown): IMessage {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return {
          title: 'Error de Comunicación',
          text: 'No fue posible conectarse al servidor. Verifique que el backend de Mascotas Córdoba esté en ejecución (puerto 8080).',
          num: error.status,
          type: 'error'
        };
      }

      if (error.status === 401) {
        return {
          title: 'Acceso No Autorizado',
          text: this.getHttpErrorMessage(error) || 'Credenciales inválidas o API Key no autorizada.',
          num: error.status,
          type: 'warning'
        };
      }

      return {
        title: 'Error en la Operación',
        text: this.getHttpErrorMessage(error),
        num: error.status,
        type: 'error'
      };
    }

    if (error instanceof Error) {
      return {
        title: 'Error Inesperado',
        text: error.message,
        type: 'error'
      };
    }

    return {
      title: 'Aviso',
      text: String(error),
      type: 'info'
    };
  }

  private getHttpErrorMessage(error: HttpErrorResponse): string {
    if (typeof error.error === 'string') {
      return error.error;
    }

    if (error.error?.mensajeRespuesta) {
      return error.error.mensajeRespuesta;
    }

    if (error.error?.message) {
      return error.error.message;
    }

    if (error.error?.error) {
      return error.error.error;
    }

    return error.message;
  }

}
