import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { getHttpErrorMessage } from '../../../shared/utils/http-error.util';
import { SanidadResource } from '../../api/sanidad-resource';
import { IValidacionCarnet } from '../../api/models/i-sanidad';

@Component({
  selector: 'app-validar-carnet',
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './validar-carnet.component.html',
  styleUrls: ['./validar-carnet.component.css']
})
export class ValidarCarnetComponent implements OnInit {

  private readonly _fb = inject(FormBuilder);
  private readonly _route = inject(ActivatedRoute);
  private readonly _sanidadResource = inject(SanidadResource);

  readonly validando = signal(false);
  readonly error = signal<string | null>(null);
  readonly resultado = signal<IValidacionCarnet | null>(null);

  readonly form = this._fb.nonNullable.group({
    codigo: ['', [Validators.required, Validators.minLength(4)]]
  });

  ngOnInit(): void {
    const codigoParam = this._route.snapshot.queryParamMap.get('codigo');
    if (codigoParam && codigoParam.trim()) {
      this.form.patchValue({ codigo: codigoParam.trim() });
      this.validar(codigoParam.trim());
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Ingresá el código de validación del carnet digital.');
      return;
    }
    this.validar(this.form.controls.codigo.value.trim());
  }

  validarCodigoEjemplo(codigo: string): void {
    this.form.patchValue({ codigo });
    this.validar(codigo);
  }

  private validar(codigo: string): void {
    this.error.set(null);
    this.validando.set(true);
    this._sanidadResource.validarCarnet(codigo).subscribe({
      next: res => {
        this.validando.set(false);
        this.resultado.set(res);
      },
      error: err => {
        this.validando.set(false);
        this.resultado.set(null);
        this.error.set(getHttpErrorMessage(err, 'No se pudo verificar el código del carnet.'));
      }
    });
  }
}
