import { Component, OnInit, ViewChild, computed, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { NuevaPublicacionModalComponent } from '../../components/nueva-publicacion-modal/nueva-publicacion-modal.component';
import { PublicacionesResource } from '../../api/resources/publicaciones-resource';
import { AuthService } from '../../services/auth.service';
import { AppMessageService } from '../../services/app-message-service';
import { ConfirmDialogService } from '../../services/confirm-dialog.service';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';
import { IEstadoPublicacion } from '../../api/models/i-estado-publicacion';
import { INuevaPublicacionRequest } from '../../api/models/i-nueva-publicacion-request';
import { HasChangesComponent } from '../../guards/controlar-cambios-guard';

@Component({
  selector: 'app-publicaciones',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, NuevaPublicacionModalComponent],
  templateUrl: './publicaciones.component.html',
  styleUrls: ['./publicaciones.component.css']
})
export class PublicacionesComponent implements OnInit, HasChangesComponent {

  private readonly _pubResource = inject(PublicacionesResource);
  private readonly _authService = inject(AuthService);
  private readonly _messageService = inject(AppMessageService);
  private readonly _confirmService = inject(ConfirmDialogService);

  @ViewChild(NuevaPublicacionModalComponent) modalComponent?: NuevaPublicacionModalComponent;

  // Resolved inputs from router (withComponentInputBinding)
  publicaciones = input<IPublicacionAdopcion[]>([]);
  estados = input<IEstadoPublicacion[]>([]);

  // Local state signals
  listaPublicaciones = signal<IPublicacionAdopcion[]>([]);
  searchQuery = signal<string>('');
  selectedEstado = signal<string>('Todos');
  showNuevaModal = signal<boolean>(false);

  // Dynamic states catalog from backend resolver
  estadosCatalogo = computed(() => {
    const list = this.estados();
    if (list && list.length > 0) {
      return list;
    }
    return [
      { codEstado: 'Activa', descripcion: 'Publicación activa' },
      { codEstado: 'Pausada', descripcion: 'Publicación pausada' },
      { codEstado: 'Finalizada', descripcion: 'Publicación finalizada' }
    ];
  });

  // Filtered publications computed signal
  filteredPublicaciones = computed(() => {
    const list = this.listaPublicaciones();
    const query = this.searchQuery().trim().toLowerCase();
    const estado = this.selectedEstado();

    return list.filter(item => {
      const nombre = (item.nombreMascota || '').toLowerCase();
      const matchesName = !query || nombre.includes(query);
      const matchesEstado = estado === 'Todos' || item.estadoPublicacion === estado;
      return matchesName && matchesEstado;
    });
  });

  ngOnInit(): void {
    // Inicializar lista con los datos provistos por el resolver
    this.listaPublicaciones.set(this.publicaciones() || []);
  }

  hasChanges(): boolean {
    return this.showNuevaModal() && (this.modalComponent?.hasChanges() ?? false);
  }

  onSearchChange(val: string): void {
    this.searchQuery.set(val);
  }

  onEstadoChange(val: string): void {
    this.selectedEstado.set(val);
  }

  getEdadTexto(pub: IPublicacionAdopcion): string {
    if (pub.edadAproximada && pub.edadAproximada > 0) {
      return `${pub.edadAproximada} años`;
    }
    if (pub.añoNacimiento && pub.añoNacimiento > 0) {
      const edadCalc = new Date().getFullYear() - pub.añoNacimiento;
      return `${edadCalc > 0 ? edadCalc : 1} años`;
    }
    return '2 años';
  }

  getFotoMascota(pub: IPublicacionAdopcion): string {
    const foto = pub.foto?.trim();
    // Si la URL es nula, vacía o apunta al dominio de pruebas simulado, usar placeholder
    if (!foto || foto.includes('mascotas.cordoba.gob.ar')) {
      return this.getDefaultFoto(pub);
    }
    return foto;
  }

  getDefaultFoto(pub: IPublicacionAdopcion): string {
    const especie = (pub.especie || '').toLowerCase();
    if (especie.includes('gato') || especie.includes('gata') || especie.includes('felin')) {
      return 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80';
    }
    return 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80';
  }

  onImageError(event: Event, pub: IPublicacionAdopcion): void {
    const target = event.target as HTMLImageElement;
    if (target && !target.dataset['fallback']) {
      target.dataset['fallback'] = 'true';
      target.src = this.getDefaultFoto(pub);
    }
  }

  toggleEstado(pub: IPublicacionAdopcion): void {
    const targetEstado = pub.estadoPublicacion === 'Activa' ? 'Pausada' : 'Activa';
    const idRefugio = this._authService.idRefugio();

    this._pubResource.actualizarEstado(pub.nroPublicacion, {
      idRefugio: idRefugio,
      nuevoEstado: targetEstado
    }).subscribe({
      next: () => {
        this.listaPublicaciones.update(list =>
          list.map(p => p.nroPublicacion === pub.nroPublicacion ? { ...p, estadoPublicacion: targetEstado } : p)
        );
        this._messageService.showMessage({
          title: 'Estado Actualizado',
          text: `La publicación de "${pub.nombreMascota}" cambió su estado a "${targetEstado}".`,
          type: 'success'
        });
      }
    });
  }

  finalizarPublicacion(pub: IPublicacionAdopcion): void {
    this._confirmService.confirm(
      'Finalizar Publicación',
      `¿Está seguro de marcar como Finalizada la publicación de "${pub.nombreMascota}"?`
    ).subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      const idRefugio = this._authService.idRefugio();
      this._pubResource.actualizarEstado(pub.nroPublicacion, {
        idRefugio: idRefugio,
        nuevoEstado: 'Finalizada'
      }).subscribe({
        next: () => {
          this.listaPublicaciones.update(list =>
            list.map(p => p.nroPublicacion === pub.nroPublicacion ? { ...p, estadoPublicacion: 'Finalizada' } : p)
          );
          this._messageService.showMessage({
            title: 'Publicación Finalizada',
            text: `La publicación de "${pub.nombreMascota}" ha sido finalizada correctamente.`,
            type: 'info'
          });
        }
      });
    });
  }

  abrirNuevaModal(): void {
    this.showNuevaModal.set(true);
  }

  cerrarNuevaModal(): void {
    this.showNuevaModal.set(false);
  }

  guardarNuevaPublicacion(request: INuevaPublicacionRequest): void {
    this._pubResource.crearPublicacion(request).subscribe({
      next: (res) => {
        this.cerrarNuevaModal();
        this._messageService.showMessage({
          title: 'Publicación Registrada',
          text: res?.mensajeRespuesta || res?.mensaje || 'Se registró exitosamente la nueva publicación de adopción.',
          type: 'success'
        });

        // Recargar el listado actualizado de publicaciones desde el backend
        const idRefugio = this._authService.idRefugio();
        this._pubResource.getPublicacionesPorRefugio(idRefugio).subscribe({
          next: (actualizadas) => this.listaPublicaciones.set(actualizadas)
        });
      }
    });
  }

}

