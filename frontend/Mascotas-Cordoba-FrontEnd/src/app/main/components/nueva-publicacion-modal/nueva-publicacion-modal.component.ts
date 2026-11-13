import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PublicacionesResource } from '../../api/resources/publicaciones-resource';
import { AuthService } from '../../../core/services/auth.service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
import { getEdadTexto } from '../../../shared/utils/mascota-formatters';
import { IMascotaDisponible } from '../../api/models/i-mascota-disponible';
import { IEspecie } from '../../api/models/i-especie';
import { INuevaPublicacionRequest } from '../../api/models/i-nueva-publicacion-request';

@Component({
  selector: 'app-nueva-publicacion-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, AutofocusDirective],
  templateUrl: './nueva-publicacion-modal.component.html',
  styleUrls: ['./nueva-publicacion-modal.component.css']
})
export class NuevaPublicacionModalComponent implements OnInit {

  private readonly _pubResource = inject(PublicacionesResource);
  private readonly _authService = inject(AuthService);
  private readonly _messageService = inject(AppMessageService);

  @ViewChild('fileInput') fileInput?: ElementRef<HTMLInputElement>;

  @Input() especiesInput?: IEspecie[];
  isSaving = input<boolean>(false);
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<INuevaPublicacionRequest>();

  mascotasDisponibles = signal<IMascotaDisponible[]>([]);
  especiesDisponibles = signal<IEspecie[]>([]);
  selectedNroReg = signal<number | null>(null);

  nombreMascota: string = '';
  especie: string = 'Perro';
  raza: string = '';
  sexo: string = 'Hembra';
  fechaNacimiento: string = '';
  readonly maxFechaNacimiento: string = this.getTodayIsoDate();
  private anoNacimientoSeleccionado?: number;
  private edadAproximadaSeleccionada?: number;
  nroRegMunicipal: string = '';
  condicionAdopcion: string = 'Hogar responsable y seguimiento veterinario';
  descripcion: string = '';
  foto: string = '';
  estadoPublicacion: string = 'Activa';

  // File upload state
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  isDragging: boolean = false;
  isUploading: boolean = false;

  private readonly MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
  private readonly ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

  ngOnInit(): void {
    const idRefugio = this._authService.idRefugio();
    this._pubResource.getMascotasDisponibles(idRefugio).subscribe((pets) => {
      this.mascotasDisponibles.set(pets || []);
    });

    if (this.especiesInput && this.especiesInput.length > 0) {
      this.especiesDisponibles.set(this.especiesInput);
      this.especie = this.especiesInput[0].nomEspecie;
    } else {
      this._pubResource.getEspecies().subscribe((esp) => {
        if (esp && esp.length > 0) {
          this.especiesDisponibles.set(esp);
          this.especie = esp[0].nomEspecie;
        }
      });
    }
  }

  onMascotaSelect(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const nro = Number(select.value);

    if (nro > 0) {
      const encontrada = this.mascotasDisponibles().find(m => m.nroRegMunicipal === nro);
      if (encontrada) {
        this.selectedNroReg.set(encontrada.nroRegMunicipal);
        this.nroRegMunicipal = String(encontrada.nroRegMunicipal);
        this.nombreMascota = encontrada.nombre || '';
        this.especie = encontrada.especie || 'Perro';
        this.raza = encontrada.raza || '';
        this.sexo = (encontrada.sexo === 'M' || encontrada.sexo === 'm' || encontrada.sexo === 'Macho') ? 'Macho' : 'Hembra';
        if (encontrada.fechaNacimiento) {
          this.fechaNacimiento = encontrada.fechaNacimiento.slice(0, 10);
          this.anoNacimientoSeleccionado = encontrada.añoNacimiento ?? Number(this.fechaNacimiento.slice(0, 4));
        } else {
          this.fechaNacimiento = '';
          this.anoNacimientoSeleccionado = encontrada.añoNacimiento;
        }
        this.edadAproximadaSeleccionada = encontrada.edadAproximada;
        return;
      }
    }

    // Sin mascota previa seleccionada
    this.selectedNroReg.set(null);
    this.nroRegMunicipal = '';
    this.nombreMascota = '';
    this.especie = this.especiesDisponibles()[0]?.nomEspecie || 'Perro';
    this.raza = '';
    this.sexo = 'Hembra';
    this.fechaNacimiento = '';
    this.anoNacimientoSeleccionado = undefined;
    this.edadAproximadaSeleccionada = undefined;
  }

  get edadCalculadaTexto(): string | null {
    if (this.fechaNacimiento) {
      return getEdadTexto(undefined, undefined, this.fechaNacimiento);
    }
    if (this.selectedNroReg() !== null && (this.anoNacimientoSeleccionado || this.edadAproximadaSeleccionada !== undefined)) {
      const edad = getEdadTexto(this.edadAproximadaSeleccionada, this.anoNacimientoSeleccionado);
      return this.anoNacimientoSeleccionado ? `${edad} (nac. ${this.anoNacimientoSeleccionado})` : edad;
    }
    return null;
  }

  private getTodayIsoDate(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onEspecieChange(): void {
    // No-op mantenido para extensibilidad
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  private handleFile(file: File): void {
    if (!this.ALLOWED_TYPES.includes(file.type)) {
      this._messageService.showMessage({
        title: 'Formato no permitido',
        text: 'Solo se admiten imágenes en formato JPG, PNG, WEBP o GIF.',
        type: 'warning'
      });
      return;
    }

    if (file.size > this.MAX_FILE_SIZE) {
      this._messageService.showMessage({
        title: 'Archivo demasiado grande',
        text: 'La imagen seleccionada no puede superar los 5 MB.',
        type: 'warning'
      });
      return;
    }

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  removeFile(): void {
    this.selectedFile = null;
    this.previewUrl = null;
    this.foto = '';
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  hasChanges(): boolean {
    return Boolean(
      this.nombreMascota.trim() ||
      this.raza.trim() ||
      this.fechaNacimiento ||
      this.descripcion.trim() ||
      this.selectedNroReg() ||
      this.selectedFile
    );
  }

  onClose(): void {
    if (this.isUploading || this.isSaving()) {
      return;
    }
    this.close.emit();
  }

  onSubmit(): void {
    if (this.isUploading || this.isSaving()) {
      return;
    }

    if (!this.nombreMascota.trim() || !this.descripcion.trim()) {
      this._messageService.showMessage({
        title: 'Campos requeridos',
        text: 'Por favor complete el nombre de la mascota y las características.',
        type: 'warning'
      });
      return;
    }

    if (this.fechaNacimiento && this.fechaNacimiento > this.maxFechaNacimiento) {
      this._messageService.showMessage({
        title: 'Fecha inválida',
        text: 'La fecha de nacimiento no puede ser posterior a hoy.',
        type: 'warning'
      });
      return;
    }

    if (this.selectedFile) {
      this.isUploading = true;
      this._pubResource.subirFoto(this.selectedFile).subscribe({
        next: (res) => {
          this.isUploading = false;
          this.foto = res.urlFoto || '';
          this.emitirGuardar();
        },
        error: (err) => {
          this.isUploading = false;
          this._messageService.showMessage({
            title: 'Error al subir foto',
            text: err?.error?.mensaje || 'No se pudo subir la foto de la mascota. Intente nuevamente.',
            type: 'error'
          });
        }
      });
    } else {
      this.emitirGuardar();
    }
  }

  private emitirGuardar(): void {
    const idRefugio = this._authService.idRefugio();
    const fechaNacClean = this.fechaNacimiento.trim() || undefined;
    const anoNacClean = fechaNacClean
      ? Number(fechaNacClean.slice(0, 4))
      : this.anoNacimientoSeleccionado;

    const req: INuevaPublicacionRequest = {
      idRefugio: idRefugio,
      nroRegMunicipal: this.selectedNroReg() ?? undefined,
      nombreMascota: this.nombreMascota.trim(),
      especie: this.especie,
      raza: this.raza.trim() || undefined,
      sexo: this.sexo,
      añoNacimiento: anoNacClean,
      fechaNacimiento: fechaNacClean,
      caracteristicasMascota: this.descripcion.trim(),
      condicionAdopcion: this.condicionAdopcion.trim(),
      foto: this.foto.trim() || undefined,
      estadoPublicacion: this.estadoPublicacion
    };

    this.save.emit(req);
  }

}
