import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
import { IPageResponse } from '../../../shared/models/pagination';
import { IVeterinariaPublica } from '../../api/models/i-veterinaria-publica';
import { VeterinariasResource } from '../../api/veterinarias-resource';

@Component({
  selector: 'app-veterinarias',
  imports: [ReactiveFormsModule, NavbarComponent, AutofocusDirective],
  templateUrl: './veterinarias.component.html',
  styleUrls: ['./veterinarias.component.css']
})
export class VeterinariasComponent implements OnInit {

  private readonly _resource = inject(VeterinariasResource);

  readonly filtroControl = new FormControl('', { nonNullable: true });
  readonly page = signal<IPageResponse<IVeterinariaPublica>>({
    items: [],
    pagina: 1,
    tamanioPagina: 12,
    totalElementos: 0,
    totalPaginas: 0
  });
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.buscar(1);
  }

  buscar(pagina = 1): void {
    this.cargando.set(true);
    this.error.set(null);
    this._resource.getVeterinarias(this.filtroControl.value, { pagina, tamanioPagina: 12 })
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: res => this.page.set(res),
        error: () => this.error.set('No se pudieron cargar las veterinarias adheridas.')
      });
  }

  limpiar(): void {
    this.filtroControl.setValue('');
    this.buscar(1);
  }

  irAPagina(pagina: number): void {
    if (pagina < 1 || (this.page().totalPaginas > 0 && pagina > this.page().totalPaginas)) {
      return;
    }
    this.buscar(pagina);
  }
}
