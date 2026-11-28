import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { getHttpErrorMessage } from '../../../shared/utils/http-error.util';
import { IVeterinariaOption, SanidadResource } from '../../api/sanidad-resource';
import {
  IAtencionSanitaria,
  IRegistrarAtencionRequest,
  ITipoAtencion
} from '../../api/models/i-sanidad';

@Component({
  selector: 'app-atenciones',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NavbarComponent],
  templateUrl: './atenciones.component.html',
  styleUrls: ['./atenciones.component.css']
})
export class AtencionesComponent implements OnInit {

  private readonly _fb = inject(FormBuilder);
  private readonly _route = inject(ActivatedRoute);
  private readonly _sanidadResource = inject(SanidadResource);
  private readonly _authService = inject(AuthService);

  readonly usuario = this._authService.usuario;
  readonly esVeterinaria = computed(() => this.usuario()?.perfil === 'VETERINARIA');

  readonly tiposAtencion = signal<ITipoAtencion[]>([]);
  readonly veterinarias = signal<IVeterinariaOption[]>([]);
  readonly historial = signal<IAtencionSanitaria[]>([]);
  readonly nrmConsultado = signal<number | null>(null);
  readonly guardando = signal(false);
  readonly consultandoHistorial = signal(false);
  readonly error = signal<string | null>(null);
  readonly exito = signal<IAtencionSanitaria | null>(null);

  readonly form = this._fb.nonNullable.group({
    nroRegMunicipal: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
    idVeterinaria: [''],
    codTipoAtencion: ['', Validators.required],
    fechaAtencion: [this.hoyIso(), Validators.required],
    fechaVencimiento: [''],
    profesional: [''],
    detalleAtencion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(255)]]
  });

  ngOnInit(): void {
    const idVetUsuario = this.usuario()?.idVeterinaria;
    if (this.esVeterinaria() && idVetUsuario) {
      this.form.patchValue({ idVeterinaria: String(idVetUsuario) });
    }

    this._sanidadResource.getTiposAtencion().subscribe({
      next: tipos => {
        this.tiposAtencion.set(tipos);
        if (tipos.length > 0 && !this.form.controls.codTipoAtencion.value) {
          this.form.patchValue({ codTipoAtencion: String(tipos[0].codTipoAtencion) });
        }
      },
      error: () => {
        this.tiposAtencion.set([]);
      }
    });

    if (!this.esVeterinaria()) {
      this._sanidadResource.getVeterinariasActivas().subscribe({
        next: vets => {
          this.veterinarias.set(vets);
          if (vets.length > 0 && !this.form.controls.idVeterinaria.value) {
            this.form.patchValue({ idVeterinaria: String(vets[0].idVeterinaria) });
          }
        },
        error: () => this.veterinarias.set([])
      });
    }

    const nrmParam = this._route.snapshot.queryParamMap.get('nrm');
    if (nrmParam && /^\d+$/.test(nrmParam)) {
      this.form.patchValue({ nroRegMunicipal: nrmParam });
      this.buscarHistorial(Number(nrmParam));
    }
  }

  buscarHistorialDesdeFormulario(): void {
    const raw = this.form.controls.nroRegMunicipal.value.trim();
    if (!/^\d+$/.test(raw) || Number(raw) <= 0) {
      this.error.set('Ingresá un NRM numérico válido para consultar el historial.');
      return;
    }
    this.buscarHistorial(Number(raw));
  }

  registrar(): void {
    this.error.set(null);
    this.exito.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Completá los campos obligatorios con valores válidos.');
      return;
    }

    const value = this.form.getRawValue();
    const nrm = Number(value.nroRegMunicipal);
    const idVet = this.esVeterinaria()
      ? this.usuario()?.idVeterinaria
      : (value.idVeterinaria ? Number(value.idVeterinaria) : undefined);

    if (!idVet) {
      this.error.set('Seleccioná la veterinaria que realizó la práctica sanitaria.');
      return;
    }

    const payload: IRegistrarAtencionRequest = {
      nroRegMunicipal: nrm,
      codTipoAtencion: Number(value.codTipoAtencion),
      fechaAtencion: value.fechaAtencion,
      detalleAtencion: value.detalleAtencion.trim(),
      idVeterinaria: idVet
    };
    if (value.fechaVencimiento) {
      payload.fechaVencimiento = value.fechaVencimiento;
    }
    if (value.profesional.trim()) {
      payload.profesional = value.profesional.trim();
    }

    this.guardando.set(true);
    this._sanidadResource.registrarAtencion(payload).subscribe({
      next: creada => {
        this.guardando.set(false);
        this.exito.set(creada);
        this.form.patchValue({ detalleAtencion: '', fechaVencimiento: '' });
        this.buscarHistorial(creada.nroRegMunicipal);
      },
      error: err => {
        this.guardando.set(false);
        this.error.set(getHttpErrorMessage(err, 'No se pudo registrar la práctica sanitaria.'));
      }
    });
  }

  private buscarHistorial(nrm: number): void {
    this.error.set(null);
    this.consultandoHistorial.set(true);
    this._sanidadResource.consultarAtenciones(nrm).subscribe({
      next: lista => {
        this.consultandoHistorial.set(false);
        this.nrmConsultado.set(nrm);
        this.historial.set(lista);
      },
      error: err => {
        this.consultandoHistorial.set(false);
        this.nrmConsultado.set(nrm);
        this.historial.set([]);
        this.error.set(getHttpErrorMessage(err, 'No se pudo consultar el historial de esa mascota.'));
      }
    });
  }

  private hoyIso(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
