import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PublicacionesResource } from '../../api/resources/publicaciones-resource';
import { AuthService } from '../../../core/services/auth.service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
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
  edad: string = '2 años';
  edadAproximada: number = 2;
  anoNacimiento: number = new Date().getFullYear() - 2;
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
        if (encontrada.añoNacimiento) {
          this.anoNacimiento = encontrada.añoNacimiento;
        } else if (encontrada.edadAproximada) {
          this.anoNacimiento = new Date().getFullYear() - encontrada.edadAproximada;
        }
        this.edadAproximada = encontrada.edadAproximada || (new Date().getFullYear() - this.anoNacimiento);
        this.edad = `${this.edadAproximada} años (nac. ${this.anoNacimiento})`;
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
    this.anoNacimiento = new Date().getFullYear() - 2;
    this.edadAproximada = 2;
    this.edad = '2 años';
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
    const req: INuevaPublicacionRequest = {
      idRefugio: idRefugio,
      nroRegMunicipal: this.selectedNroReg() ?? undefined,
      nombreMascota: this.nombreMascota.trim(),
      especie: this.especie,
      raza: this.raza.trim() || undefined,
      sexo: this.sexo,
      añoNacimiento: this.anoNacimiento || (new Date().getFullYear() - (this.edadAproximada || 2)),
      caracteristicasMascota: this.descripcion.trim(),
      condicionAdopcion: this.condicionAdopcion.trim(),
      foto: this.foto.trim() || undefined,
      estadoPublicacion: this.estadoPublicacion
    };

    this.save.emit(req);
  }

}
