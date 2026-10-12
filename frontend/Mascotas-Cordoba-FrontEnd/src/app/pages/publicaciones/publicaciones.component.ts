import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { NuevaPublicacionModalComponent } from '../../components/nueva-publicacion-modal/nueva-publicacion-modal.component';
import { PublicacionesService } from '../../services/publicaciones.service';
import { Publicacion } from '../../models/publicacion.model';

@Component({
  selector: 'app-publicaciones',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, NuevaPublicacionModalComponent],
  templateUrl: './publicaciones.component.html',
  styleUrls: ['./publicaciones.component.css']
})
export class PublicacionesComponent {

  private pubService = inject(PublicacionesService);

  searchQuery = signal<string>('');
  selectedEstado = signal<string>('Todos');
  showNuevaModal = signal<boolean>(false);

  // Filtered publications computed signal
  filteredPublicaciones = computed(() => {
    const list = this.pubService.publicaciones();
    const query = this.searchQuery().trim().toLowerCase();
    const estado = this.selectedEstado();

    return list.filter(item => {
      const matchesName = !query || item.nombreMascota.toLowerCase().includes(query);
      const matchesEstado = estado === 'Todos' || item.estadoPublicacion === estado;
      return matchesName && matchesEstado;
    });
  });

  onSearchChange(val: string): void {
    this.searchQuery.set(val);
  }

  onEstadoChange(val: string): void {
    this.selectedEstado.set(val);
  }

  toggleEstado(pub: Publicacion): void {
    this.pubService.toggleEstado(pub.nroPublicacion);
  }

  onEliminar(pub: Publicacion): void {
    if (confirm(`¿Desea finalizar/eliminar la publicación de ${pub.nombreMascota}?`)) {
      this.pubService.eliminarPublicacion(pub.nroPublicacion);
    }
  }

  abrirNuevaModal(): void {
    this.showNuevaModal.set(true);
  }

  cerrarNuevaModal(): void {
    this.showNuevaModal.set(false);
  }

  guardarNuevaPublicacion(nueva: Omit<Publicacion, 'nroPublicacion'>): void {
    this.pubService.agregarPublicacion(nueva);
  }

}
