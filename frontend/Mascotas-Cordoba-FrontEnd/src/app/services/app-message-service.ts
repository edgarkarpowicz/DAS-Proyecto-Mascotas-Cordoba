import { Injectable, signal } from '@angular/core';

export interface IMessage {
  text: string;
  num?: number;
  title?: string;
  type?: 'error' | 'info' | 'warning' | 'success';
}

@Injectable({
  providedIn: 'root'
})
export class AppMessageService {

  private _message = signal<IMessage | null>(null);

  readonly message = this._message.asReadonly();

  showMessage(message: IMessage): void {
    this._message.set(message);
  }

  closeMessage(): void {
    this._message.set(null);
  }

}
