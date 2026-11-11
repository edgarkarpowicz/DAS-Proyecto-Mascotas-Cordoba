import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppMessageService } from '../../services/app-message-service';

@Component({
  selector: 'app-modal-message',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-message.component.html',
  styleUrls: ['./modal-message.component.css']
})
export class ModalMessageComponent {

  private readonly _messageService = inject(AppMessageService);
  readonly message = this._messageService.message;

  close(): void {
    this._messageService.closeMessage();
  }

}
