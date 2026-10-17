import { Injectable, signal } from '@angular/core';
import { Observable, Subject } from 'rxjs';

export interface IConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {

  private _dialogData = signal<IConfirmDialogData | null>(null);
  private _resultSubject: Subject<boolean> | null = null;

  readonly dialogData = this._dialogData.asReadonly();

  confirm(title: string = 'Descartar cambios', message: string = '¿Está seguro de descartar sus cambios?'): Observable<boolean> {
    this._resultSubject = new Subject<boolean>();
    this._dialogData.set({
      title,
      message,
      confirmText: 'Aceptar',
      cancelText: 'Cancelar'
    });
    return this._resultSubject.asObservable();
  }

  resolve(accepted: boolean): void {
    if (this._resultSubject) {
      this._resultSubject.next(accepted);
      this._resultSubject.complete();
      this._resultSubject = null;
    }
    this._dialogData.set(null);
  }

}
