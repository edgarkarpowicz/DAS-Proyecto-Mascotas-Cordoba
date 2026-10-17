import { Component, EventEmitter, OnInit, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PublicacionesResource } from '../../api/resources/publicaciones-resource';
import { AuthService } from '../../services/auth.service';
import { IMascotaDisponible } from '../../api/models/i-mascota-disponible';
import { INuevaPublicacionRequest } from '../../api/models/i-nueva-publicacion-request';

@Component({
  selector: 'app-nueva-publicacion-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nueva-publicacion-modal.component.html',
  styleUrls: ['./nueva-publicacion-modal.component.css']
})
export class NuevaPublicacionModalComponent implements OnInit {

  private readonly _pubResource = inject(PublicacionesResource);
  private readonly _authService = inject(AuthService);

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<INuevaPublicacionRequest>();

  mascotasDisponibles = signal<IMascotaDisponible[]>([]);
  selectedNroReg = signal<number | null>(null);

  nombreMascota: string = '';
  especie: string = 'Perro';
  sexo: string = 'Hembra';
  edad: string = '2 años';
  edadAproximada: number = 2;
  nroRegMunicipal: string = '';
  condicionAdopcion: string = 'Hogar responsable y seguimiento veterinario';
  descripcion: string = '';
  foto: string = 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80';
  estadoPublicacion: string = 'Activa';

  ngOnInit(): void {
    const idRefugio = this._authService.idRefugio();
    this._pubResource.getMascotasDisponibles(idRefugio).subscribe({
      next: (pets) => {
        this.mascotasDisponibles.set(pets || []);
      },
      error: () => {
        this.mascotasDisponibles.set([]);
      }
    });
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
        this.sexo = encontrada.sexo || 'Hembra';
        this.edadAproximada = encontrada.edadAproximada || 2;
        this.edad = `${this.edadAproximada} años`;
        this.onEspecieChange();
        return;
      }
    }

    // Sin mascota previa seleccionada
    this.selectedNroReg.set(null);
    this.nroRegMunicipal = '';
  }

  onEspecieChange(): void {
    if (this.foto.includes('photo-1543466835') || this.foto.includes('photo-1514888286')) {
      const isCat = this.especie.toLowerCase().includes('gato') || this.especie.toLowerCase().includes('gata');
      this.foto = isCat
        ? 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80'
        : 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80';
    }
  }

  hasChanges(): boolean {
    return Boolean(this.nombreMascota.trim() || this.descripcion.trim() || this.selectedNroReg());
  }

  onClose(): void {
    this.close.emit();
  }

  onSubmit(): void {
    if (!this.nombreMascota.trim() || !this.descripcion.trim()) {
      alert('Por favor complete el nombre de la mascota y las características');
      return;
    }

    const idRefugio = this._authService.idRefugio();
    const req: INuevaPublicacionRequest = {
      idRefugio: idRefugio,
      nroRegMunicipal: this.selectedNroReg() ?? undefined,
      nombreMascota: this.nombreMascota.trim(),
      especie: this.especie,
      sexo: this.sexo,
      añoNacimiento: new Date().getFullYear() - (this.edadAproximada || 2),
      caracteristicasMascota: this.descripcion.trim(),
      condicionAdopcion: this.condicionAdopcion.trim(),
      foto: this.foto.trim() || undefined,
      estadoPublicacion: this.estadoPublicacion
    };

    this.save.emit(req);
  }

}

