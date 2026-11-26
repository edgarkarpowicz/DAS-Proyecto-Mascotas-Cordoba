import { Component, DestroyRef, ElementRef, inject, signal, viewChild, ChangeDetectionStrategy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepicker, MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogRef } from '@angular/material/dialog';
import { Observable, finalize, map, of, switchMap } from 'rxjs';
import { PublicacionesResource } from '../../api/publicaciones-resource';
import { AuthService } from '../../../core/services/auth.service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
import { getEdadTexto } from '../../../shared/utils/mascota-formatters';
import { IMascotaDisponible } from '../../api/models/i-mascota-disponible';
import { IEspecie } from '../../api/models/i-especie';
import { INuevaPublicacionRequest } from '../../api/models/i-nueva-publicacion-request';

/** El datepicker muestra y elige solo el año. */
const FORMATO_ANIO = {
  parse: { dateInput: { year: 'numeric' } },
  display: {
    dateInput: { year: 'numeric' },
    monthYearLabel: { year: 'numeric' },
    dateA11yLabel: { year: 'numeric' },
    monthYearA11yLabel: { year: 'numeric' }
  }
};

const ANTIGUEDAD_MAXIMA_ANIOS = 30;

/**
 * Alta de una publicación de adopción. Se abre por referencia con MatDialog,
 * busca sus propios datos (mascotas disponibles y especies) y graba la
 * publicación: al terminar se cierra devolviendo true.
 */
@Component({
  selector: 'app-nueva-publicacion-modal',
  imports: [ReactiveFormsModule, MatDatepickerModule, AutofocusDirective],
  providers: [provideNativeDateAdapter(FORMATO_ANIO), { provide: MAT_DATE_LOCALE, useValue: 'es-AR' }],
  templateUrl: './nueva-publicacion-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./nueva-publicacion-modal.component.css']
})
export class NuevaPublicacionModalComponent {

  private readonly _dialogRef = inject(MatDialogRef<NuevaPublicacionModalComponent, boolean>);
  private readonly _pubResource = inject(PublicacionesResource);
  private readonly _authService = inject(AuthService);
  private readonly _messageService = inject(AppMessageService);
  private readonly _confirmService = inject(ConfirmDialogService);
  private readonly _destroyRef = inject(DestroyRef);
  private readonly _fb = inject(FormBuilder);

  private readonly fileInput = viewChild<ElementRef<HTMLInputElement>>('fileInput');

  readonly MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
  private readonly ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

  readonly anioMaximo = new Date(new Date().getFullYear(), 11, 31);
  readonly anioMinimo = new Date(new Date().getFullYear() - ANTIGUEDAD_MAXIMA_ANIOS, 0, 1);

  readonly form = this._fb.group({
    nroRegMunicipal: this._fb.nonNullable.control(0),
    nombreMascota: this._fb.nonNullable.control('', [Validators.required, Validators.maxLength(64)]),
    especie: this._fb.nonNullable.control('', Validators.required),
    sexo: this._fb.nonNullable.control('Hembra', Validators.required),
    anioNacimiento: this._fb.control<Date | null>(null, Validators.required),
    raza: this._fb.nonNullable.control('', Validators.maxLength(64)),
    condicionAdopcion: this._fb.nonNullable.control('', [Validators.required, Validators.maxLength(1000)]),
    descripcion: this._fb.nonNullable.control('', [Validators.required, Validators.maxLength(2000)])
  });

  readonly mascotasDisponibles = signal<IMascotaDisponible[]>([]);
  readonly especiesDisponibles = signal<IEspecie[]>([]);
  readonly mascotaSeleccionada = signal<IMascotaDisponible | null>(null);

  /** Los errores de validación se muestran recién al intentar publicar. */
  readonly enviado = signal(false);
  readonly guardando = signal(false);
  readonly errorServidor = signal<string | null>(null);

  readonly archivoFoto = signal<File | null>(null);
  readonly previewUrl = signal<string | null>(null);
  readonly errorFoto = signal<string | null>(null);
  readonly arrastrando = signal(false);

  private readonly _anio = toSignal(this.form.controls.anioNacimiento.valueChanges, { initialValue: null });

  constructor() {
    const idRefugio = this._authService.idRefugio();
    this._pubResource.getMascotasDisponibles(idRefugio)
      .pipe(takeUntilDestroyed())
      .subscribe(mascotas => this.mascotasDisponibles.set(mascotas || []));

    this._pubResource.getEspecies()
      .pipe(takeUntilDestroyed())
      .subscribe(especies => {
        this.especiesDisponibles.set(especies || []);
        if (!this.form.controls.especie.value && especies?.length) {
          this.form.controls.especie.setValue(especies[0].nomEspecie);
        }
      });

    this.form.controls.nroRegMunicipal.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe(nro => this.aplicarMascotaSeleccionada(Number(nro)));

    // Cerrar con Esc o con el fondo pide confirmación si hay datos cargados.
    this._dialogRef.backdropClick().pipe(takeUntilDestroyed()).subscribe(() => this.cancelar());
    this._dialogRef.keydownEvents().pipe(takeUntilDestroyed()).subscribe(event => {
      if (event.key === 'Escape') {
        this.cancelar();
      }
    });
  }

  // ---------------------------------------------------------------- validación

  mostrarError(campo: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[campo];
    return this.enviado() && control.enabled && control.invalid;
  }

  hasChanges(): boolean {
    return this.form.dirty || this.archivoFoto() !== null;
  }

  // ------------------------------------------------------------ mascota y año

  private aplicarMascotaSeleccionada(nro: number): void {
    const identidad = [
      this.form.controls.nombreMascota,
      this.form.controls.especie,
      this.form.controls.sexo,
      this.form.controls.anioNacimiento,
      this.form.controls.raza
    ];
    const encontrada = nro > 0 ? this.mascotasDisponibles().find(m => m.nroRegMunicipal === nro) : undefined;

    if (encontrada) {
      // Mascota del Registro Único: sus datos de identidad no se modifican.
      const anio = encontrada.añoNacimiento
        ?? (encontrada.fechaNacimiento ? Number(encontrada.fechaNacimiento.slice(0, 4)) : null);
      this.mascotaSeleccionada.set(encontrada);
      this.form.patchValue({
        nombreMascota: encontrada.nombre || '',
        especie: encontrada.especie || '',
        sexo: this.normalizarSexo(encontrada.sexo),
        anioNacimiento: anio ? new Date(anio, 0, 1) : null,
        raza: encontrada.raza || ''
      });
      identidad.forEach(control => control.disable());
      return;
    }

    this.mascotaSeleccionada.set(null);
    identidad.forEach(control => control.enable());
    this.form.patchValue({
      nombreMascota: '',
      especie: this.especiesDisponibles()[0]?.nomEspecie || '',
      sexo: 'Hembra',
      anioNacimiento: null,
      raza: ''
    });
  }

  private normalizarSexo(sexo?: string): string {
    const valor = (sexo || '').trim().toUpperCase();
    return valor === 'M' || valor === 'MACHO' ? 'Macho' : 'Hembra';
  }

  especieEnCatalogo(especie: string): boolean {
    return this.especiesDisponibles().some(e => e.nomEspecie === especie);
  }

  elegirAnio(fecha: Date, picker: MatDatepicker<Date>): void {
    const control = this.form.controls.anioNacimiento;
    control.setValue(new Date(fecha.getFullYear(), 0, 1));
    control.markAsDirty();
    picker.close();
  }

  edadTexto(): string | null {
    const anio = this._anio() ?? this.form.controls.anioNacimiento.value;
    return anio ? getEdadTexto(undefined, anio.getFullYear()) : null;
  }

  // -------------------------------------------------------------------- foto

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.arrastrando.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.arrastrando.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.arrastrando.set(false);
    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  private handleFile(file: File): void {
    if (!this.ALLOWED_TYPES.includes(file.type)) {
      this.errorFoto.set('Solo se admiten imágenes JPG, PNG, WEBP o GIF.');
      return;
    }
    if (file.size > this.MAX_FILE_SIZE) {
      this.errorFoto.set('La imagen no puede superar los 5 MB.');
      return;
    }
    this.errorFoto.set(null);
    this.archivoFoto.set(file);
    const reader = new FileReader();
    reader.onload = () => this.previewUrl.set(reader.result as string);
    reader.readAsDataURL(file);
  }

  removeFile(): void {
    this.archivoFoto.set(null);
    this.previewUrl.set(null);
    this.errorFoto.set(null);
    const input = this.fileInput()?.nativeElement;
    if (input) {
      input.value = '';
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  // ----------------------------------------------------------------- acciones

  cancelar(): void {
    if (this.guardando()) {
      return;
    }
    if (!this.hasChanges()) {
      this._dialogRef.close(false);
      return;
    }
    this._confirmService.confirm(
      'Descartar cambios',
      '¿Está seguro de descartar sus cambios? Se perderán los datos ingresados en la publicación.'
    ).pipe(takeUntilDestroyed(this._destroyRef)).subscribe(confirmado => {
      if (confirmado) {
        this._dialogRef.close(false);
      }
    });
  }

  publicar(): void {
    if (this.guardando()) {
      return;
    }
    this.enviado.set(true);
    this.errorServidor.set(null);
    if (this.form.invalid || this.errorFoto()) {
      return;
    }

    this.guardando.set(true);
    this.subirFoto().pipe(
      switchMap(urlFoto => this._pubResource.crearPublicacion(this.armarRequest(urlFoto))),
      finalize(() => this.guardando.set(false)),
      takeUntilDestroyed(this._destroyRef)
    ).subscribe({
      next: res => {
        this._messageService.notify(res?.mensajeRespuesta || 'Publicación registrada.');
        this._dialogRef.close(true);
      },
      error: err => {
        this.errorServidor.set(
          err?.error?.mensajeRespuesta || err?.error?.mensaje || 'No se pudo registrar la publicación. Intente nuevamente.'
        );
      }
    });
  }

  private subirFoto(): Observable<string | undefined> {
    const archivo = this.archivoFoto();
    if (!archivo) {
      return of(undefined);
    }
    return this._pubResource.subirFoto(archivo).pipe(map(res => res.urlFoto || undefined));
  }

  private armarRequest(urlFoto?: string): INuevaPublicacionRequest {
    const valores = this.form.getRawValue();
    const mascota = this.mascotaSeleccionada();
    return {
      idRefugio: this._authService.idRefugio(),
      nroRegMunicipal: mascota?.nroRegMunicipal,
      nombreMascota: valores.nombreMascota.trim(),
      especie: valores.especie,
      raza: valores.raza.trim() || undefined,
      sexo: valores.sexo,
      añoNacimiento: valores.anioNacimiento?.getFullYear(),
      caracteristicasMascota: valores.descripcion.trim(),
      condicionAdopcion: valores.condicionAdopcion.trim(),
      foto: urlFoto,
      estadoPublicacion: 'Activa'
    };
  }

}
