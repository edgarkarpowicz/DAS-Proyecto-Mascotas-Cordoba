import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import * as QRCode from 'qrcode';
import { AuthService } from '../../../core/services/auth.service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { getHttpErrorMessage } from '../../../shared/utils/http-error.util';
import { IMascota } from '../../../mascotas/api/models/i-mascota';
import { ICarnetSanitario } from '../../api/models/i-sanidad';
import { SanidadResource } from '../../api/sanidad-resource';

@Component({
  selector: 'app-carnet',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NavbarComponent],
  templateUrl: './carnet.component.html',
  styleUrls: ['./carnet.component.css']
})
export class CarnetComponent implements OnInit {

  private readonly _fb = inject(FormBuilder);
  private readonly _route = inject(ActivatedRoute);
  private readonly _sanidadResource = inject(SanidadResource);
  private readonly _authService = inject(AuthService);

  readonly usuario = this._authService.usuario;
  readonly esCiudadano = computed(() => this.usuario()?.perfil === 'CIUDADANO');
  readonly misMascotas = signal<IMascota[]>([]);

  readonly carnet = signal<ICarnetSanitario | null>(null);
  readonly qrDataUrl = signal<string | null>(null);
  readonly sinCarnetEmitidoNrm = signal<number | null>(null);
  readonly cargando = signal(false);
  readonly emitiendo = signal(false);
  readonly error = signal<string | null>(null);

  readonly nrmForm = this._fb.nonNullable.group({
    nroRegMunicipal: ['', [Validators.required, Validators.pattern(/^\d+$/)]]
  });

  ngOnInit(): void {
    if (this.esCiudadano()) {
      this._sanidadResource.getMisMascotasResumen().subscribe({
        next: list => this.misMascotas.set(list),
        error: () => this.misMascotas.set([])
      });
    }

    const nrmParam = this._route.snapshot.queryParamMap.get('nrm');
    if (nrmParam && /^\d+$/.test(nrmParam)) {
      this.nrmForm.patchValue({ nroRegMunicipal: nrmParam });
      this.consultarPorNrm(Number(nrmParam));
    }
  }

  buscarDesdeFormulario(): void {
    if (this.nrmForm.invalid) {
      this.nrmForm.markAllAsTouched();
      this.error.set('Ingresá un número de registro municipal (NRM) válido.');
      return;
    }
    this.consultarPorNrm(Number(this.nrmForm.controls.nroRegMunicipal.value));
  }

  seleccionarMascota(nrm: number): void {
    this.nrmForm.patchValue({ nroRegMunicipal: String(nrm) });
    this.consultarPorNrm(nrm);
  }

  generarCarnet(): void {
    const raw = this.nrmForm.controls.nroRegMunicipal.value.trim();
    if (!/^\d+$/.test(raw) || Number(raw) <= 0) {
      this.error.set('Ingresá un NRM válido para emitir el carnet digital.');
      return;
    }
    const nrm = Number(raw);
    this.error.set(null);
    this.emitiendo.set(true);
    this._sanidadResource.generarCarnet(nrm).subscribe({
      next: emitido => {
        this.emitiendo.set(false);
        this.sinCarnetEmitidoNrm.set(null);
        this.carnet.set(emitido);
        this.renderQr(emitido.codigoValidacion);
      },
      error: err => {
        this.emitiendo.set(false);
        this.error.set(getHttpErrorMessage(err, 'No se pudo emitir el carnet sanitario digital.'));
      }
    });
  }

  imprimir(): void {
    window.print();
  }

  private consultarPorNrm(nrm: number): void {
    this.error.set(null);
    this.sinCarnetEmitidoNrm.set(null);
    this.cargando.set(true);
    this._sanidadResource.consultarCarnet(nrm).subscribe({
      next: encontrado => {
        this.cargando.set(false);
        this.carnet.set(encontrado);
        this.renderQr(encontrado.codigoValidacion);
      },
      error: err => {
        this.cargando.set(false);
        this.carnet.set(null);
        this.qrDataUrl.set(null);
        if (err?.status === 404) {
          this.sinCarnetEmitidoNrm.set(nrm);
          return;
        }
        this.error.set(getHttpErrorMessage(err, 'No se pudo consultar el carnet sanitario.'));
      }
    });
  }

  private renderQr(codigo: string): void {
    const origin = typeof window !== 'undefined' ? window.location.origin : '';
    const validationUrl = `${origin}/sanidad/validar-carnet?codigo=${encodeURIComponent(codigo)}`;
    QRCode.toDataURL(validationUrl, { width: 180, margin: 1 })
      .then(url => this.qrDataUrl.set(url))
      .catch(() => this.qrDataUrl.set(null));
  }
}
