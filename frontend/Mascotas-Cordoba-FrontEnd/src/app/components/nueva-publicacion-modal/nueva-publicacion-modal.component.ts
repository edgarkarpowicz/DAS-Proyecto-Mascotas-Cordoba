import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Publicacion } from '../../models/publicacion.model';

@Component({
  selector: 'app-nueva-publicacion-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nueva-publicacion-modal.component.html',
  styleUrls: ['./nueva-publicacion-modal.component.css']
})
export class NuevaPublicacionModalComponent {

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<Omit<Publicacion, 'nroPublicacion'>>();

  nombreMascota: string = '';
  especie: string = 'Perro';
  sexo: 'Hembra' | 'Macho' = 'Hembra';
  edad: string = '2 años';
  nroRegMunicipal: string = 'MUN-000' + Math.floor(100 + Math.random() * 900);
  descripcion: string = '';
  foto: string = 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80';
  estadoPublicacion: 'Activa' | 'Pausada' = 'Activa';

  onClose(): void {
    this.close.emit();
  }

  onSubmit(): void {
    if (!this.nombreMascota.trim() || !this.descripcion.trim()) {
      alert('Por favor complete el nombre y la descripción');
      return;
    }

    this.save.emit({
      nombreMascota: this.nombreMascota.trim(),
      especie: this.especie,
      sexo: this.sexo,
      edad: this.edad,
      nroRegMunicipal: this.nroRegMunicipal,
      descripcion: this.descripcion.trim(),
      foto: this.foto,
      estadoPublicacion: this.estadoPublicacion
    });

    this.close.emit();
  }

}
