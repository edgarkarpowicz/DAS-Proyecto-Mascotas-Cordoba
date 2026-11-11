import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoaderComponent } from './core/layouts/loader/loader.component';
import { ModalMessageComponent } from './core/layouts/modal-message/modal-message.component';
import { ModalDescartarCambiosComponent } from './core/layouts/modal-descartar-cambios/modal-descartar-cambios.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    LoaderComponent,
    ModalMessageComponent,
    ModalDescartarCambiosComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Mascotas-Cordoba-FrontEnd';
}

