import { Injectable, inject } from '@angular/core';
import { Observable, EMPTY, catchError, map, of, switchMap } from 'rxjs';
import { PublicacionesResource } from '../api/publicaciones-resource';
import { IPublicacionAdopcion } from '../api/models/i-publicacion-adopcion';
import { AuthService } from '../../core/services/auth.service';
import { AppMessageService } from '../../core/services/app-message-service';
import { ConfirmDialogService } from '../../core/services/confirm-dialog.service';

export type EstadoPublicacion = 'Activa' | 'Pausada' | 'Finalizada';

export interface IAccionEstado {
  nuevoEstado: EstadoPublicacion;
  etiqueta: string;
}

/**
 * Transiciones permitidas. Finalizada es un estado final: no se reactiva.
 * El back valida las mismas reglas; acá solo se usan para mostrar los botones.
 */
const TRANSICIONES: Record<string, IAccionEstado[]> = {
  Activa: [
    { nuevoEstado: 'Pausada', etiqueta: 'Pausar' },
    { nuevoEstado: 'Finalizada', etiqueta: 'Finalizar' }
  ],
  Pausada: [
    { nuevoEstado: 'Activa', etiqueta: 'Reanudar' },
    { nuevoEstado: 'Finalizada', etiqueta: 'Finalizar' }
  ],
  Finalizada: []
};

/** Acciones que admite una publicación según su estado actual. */
export function accionesPermitidas(estadoActual: string): IAccionEstado[] {
  return TRANSICIONES[estadoActual] ?? [];
}

const AVISO_EXITO: Record<EstadoPublicacion, string> = {
  Activa: 'reanudada',
  Pausada: 'pausada',
  Finalizada: 'finalizada'
};

/**
 * Cambia el estado de una publicación. Lo usan la grilla y el detalle,
 * que son los componentes donde el usuario pide la acción.
 */
@Injectable()
export class EstadoPublicacionService {

  private readonly _resource = inject(PublicacionesResource);
  private readonly _auth = inject(AuthService);
  private readonly _messages = inject(AppMessageService);
  private readonly _confirm = inject(ConfirmDialogService);

  accionesDisponibles(estadoActual: string): IAccionEstado[] {
    return accionesPermitidas(estadoActual);
  }

  /**
   * Emite la publicación con el estado nuevo, o no emite nada si el usuario
   * cancela o el back rechaza el cambio (en ese caso ya se mostró el error).
   */
  cambiarEstado(pub: IPublicacionAdopcion, nuevoEstado: EstadoPublicacion): Observable<IPublicacionAdopcion> {
    const confirmacion$ = nuevoEstado === 'Finalizada'
      ? this._confirm.confirm(
          'Finalizar publicación',
          `¿Está seguro de finalizar la publicación de "${pub.nombreMascota}"? Una publicación finalizada no se puede reactivar.`,
          'Finalizar'
        )
      : of(true);

    return confirmacion$.pipe(
      switchMap(confirmado => {
        if (!confirmado) {
          return EMPTY;
        }
        return this._resource.actualizarEstado(pub.nroPublicacion, {
          idRefugio: this._auth.idRefugio() ?? pub.idRefugio,
          nuevoEstado
        }).pipe(
          map(() => {
            this._messages.notify(`Publicación de ${pub.nombreMascota} ${AVISO_EXITO[nuevoEstado]}.`);
            return { ...pub, estadoPublicacion: nuevoEstado };
          }),
          catchError(err => {
            this._messages.showMessage({
              title: 'No se pudo cambiar el estado',
              text: err?.error?.mensajeRespuesta || err?.error?.mensaje || 'Intente nuevamente en unos minutos.',
              type: 'error'
            });
            return EMPTY;
          })
        );
      })
    );
  }

}
