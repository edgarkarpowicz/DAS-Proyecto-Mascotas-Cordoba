import { Component, OnInit, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepicker, MatDatepickerModule } from '@angular/material/datepicker';
import { finalize } from 'rxjs';
import { IEspecie } from '../../../publicaciones/api/models/i-especie';
import { AppMessageService } from '../../../core/services/app-message-service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
import { IMascota, IRegistrarMascotaRequest } from '../../api/models/i-mascota';
import { MascotasResource } from '../../api/mascotas-resource';

const FORMATO_ANIO = {
  parse: { dateInput: { year: 'numeric' } },
  display: {
    dateInput: { year: 'numeric' },
    monthYearLabel: { year: 'numeric' },
    dateA11yLabel: { year: 'numeric' },
    monthYearA11yLabel: { year: 'numeric' }
  }
};

function noSoloEspacios(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  return typeof value === 'string' && value.trim().length === 0 ? { required: true } : null;
}

@Component({
  selector: 'app-registro-mascotas',
  imports: [ReactiveFormsModule, MatDatepickerModule, NavbarComponent, AutofocusDirective],
  providers: [provideNativeDateAdapter(FORMATO_ANIO), { provide: MAT_DATE_LOCALE, useValue: 'es-AR' }],
  templateUrl: './registro-mascotas.component.html',
  styleUrls: ['./registro-mascotas.component.css']
})
export class RegistroMascotasComponent implements OnInit {

  private readonly _resource = inject(MascotasResource);
  private readonly _messages = inject(AppMessageService);
  private readonly _fb = inject(FormBuilder);

  readonly anioMaximo = new Date();
  readonly anioMinimo = new Date(1990, 0, 1);

  readonly especies = signal<IEspecie[]>([]);
  readonly enviado = signal(false);
  readonly guardando = signal(false);
  readonly errorServidor = signal<string | null>(null);
  readonly resultado = signal<IMascota | null>(null);

  readonly form = this._fb.nonNullable.group({
    nombre: ['', [Validators.required, noSoloEspacios]],
    especie: ['', Validators.required],
    raza: [''],
    sexo: ['Hembra', Validators.required],
    anioNacimiento: [null as Date | null, Validators.required],
    castrado: [false],
    microchip: [''],
    cuilResponsable: ['']
  });

  ngOnInit(): void {
    this._resource.getEspecies().subscribe({
      next: lista => {
        this.especies.set(lista || []);
        if (!this.form.controls.especie.value && lista?.length) {
          this.form.controls.especie.setValue(lista[0].nomEspecie);
        }
      }
    });
  }

  elegirAnio(anio: Date, datepicker: MatDatepicker<Date>): void {
    this.form.controls.anioNacimiento.setValue(new Date(anio.getFullYear(), 0, 1));
    datepicker.close();
  }

  mostrarError(campo: 'nombre' | 'especie' | 'anioNacimiento'): boolean {
    const control = this.form.controls[campo];
    return this.enviado() && control.invalid;
  }

  onSubmit(): void {
    this.enviado.set(true);
    this.errorServidor.set(null);
    this.resultado.set(null);

    if (this.form.invalid) {
      return;
    }

    const v = this.form.getRawValue();
    const request: IRegistrarMascotaRequest = {
      nombre: v.nombre.trim(),
      especie: v.especie,
      raza: v.raza.trim() || undefined,
      sexo: v.sexo,
      añoNacimiento: v.anioNacimiento!.getFullYear(),
      castrado: v.castrado,
      microchip: v.microchip.trim() || undefined,
      cuilResponsable: v.cuilResponsable.trim() || undefined
    };

    this.guardando.set(true);
    this._resource.registrarMascota(request)
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: mascota => {
          this.resultado.set(mascota);
          const msg = mascota.nrmReutilizado
            ? `Se detectó coincidencia existente: se reutilizó NRM ${mascota.nroRegMunicipal}.`
            : `Mascota registrada con nuevo NRM ${mascota.nroRegMunicipal}.`;
          this._messages.notify(msg);
          this.enviado.set(false);
          this.form.patchValue({
            nombre: '',
            raza: '',
            sexo: 'Hembra',
            anioNacimiento: null,
            castrado: false,
            microchip: '',
            cuilResponsable: ''
          });
        },
        error: err => {
          this.errorServidor.set(
            err?.error?.mensajeRespuesta || err?.error?.message || err?.error?.mensaje || 'No se pudo registrar la mascota.'
          );
        }
      });
  }
}
