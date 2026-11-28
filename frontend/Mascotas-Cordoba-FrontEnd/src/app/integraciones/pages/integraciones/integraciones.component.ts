import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { getHttpErrorMessage } from '../../../shared/utils/http-error.util';
import { IntegracionesResource } from '../../api/integraciones-resource';
import {
  ILoteSincronizacion,
  IResultadoSincronizacionVeterinaria,
  IVeterinariaConfiguracion
} from '../../api/models/i-integracion';

@Component({
  selector: 'app-integraciones',
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './integraciones.component.html',
  styleUrls: ['./integraciones.component.css']
})
export class IntegracionesComponent implements OnInit {

  private readonly _fb = inject(FormBuilder);
  private readonly _resource = inject(IntegracionesResource);

  readonly clinicas = signal<IVeterinariaConfiguracion[]>([]);
  readonly lotes = signal<ILoteSincronizacion[]>([]);
  readonly resultadosRecientes = signal<IResultadoSincronizacionVeterinaria[]>([]);
  readonly cargando = signal(false);
  readonly sincronizando = signal(false);
  readonly error = signal<string | null>(null);

  readonly filtroForm = this._fb.nonNullable.group({
    fechaDesde: ['2026-10-01'],
    fechaHasta: [this.hoyIso()]
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando.set(true);
    this.error.set(null);
    this._resource.listarClinicas().subscribe({
      next: lista => {
        this.cargando.set(false);
        this.clinicas.set(lista);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(getHttpErrorMessage(err, 'No se pudo cargar la configuración de veterinarias integradas.'));
      }
    });

    this._resource.listarLotes(20).subscribe({
      next: historial => this.lotes.set(historial),
      error: () => this.lotes.set([])
    });
  }

  sincronizarTodas(): void {
    this.ejecutarSincronizacion();
  }

  sincronizarClinica(idVeterinaria: number): void {
    this.ejecutarSincronizacion(idVeterinaria);
  }

  private ejecutarSincronizacion(idVeterinaria?: number): void {
    this.error.set(null);
    this.sincronizando.set(true);
    const { fechaDesde, fechaHasta } = this.filtroForm.getRawValue();
    this._resource.sincronizar(fechaDesde || undefined, fechaHasta || undefined, idVeterinaria).subscribe({
      next: resultados => {
        this.sincronizando.set(false);
        this.resultadosRecientes.set(resultados);
        this.cargarDatos();
      },
      error: err => {
        this.sincronizando.set(false);
        this.error.set(getHttpErrorMessage(err, 'No se pudo ejecutar la sincronización por lotes.'));
      }
    });
  }

  private hoyIso(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
