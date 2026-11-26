import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoaderComponent } from './core/layouts/loader/loader.component';

@Component({
    selector: 'app-root',
    imports: [
        RouterOutlet,
        LoaderComponent
    ],
    templateUrl: './app.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Mascotas-Cordoba-FrontEnd';
}

