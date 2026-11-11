import { Component, ViewChild, computed, inject, input, model, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { NuevaPublicacionModalComponent } from '../../components/nueva-publicacion-modal/nueva-publicacion-modal.component';
import { PublicacionesResource } from '../../api/resources/publicaciones-resource';
import { AuthService } from '../../../core/services/auth.service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { AutofocusDirective } from '../../../shared/directives/autofocus';
import { FiltrarPorPipe } from '../../../shared/pipes/filtrar-por-pipe';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';
import { IEstadoPublicacion } from '../../api/models/i-estado-publicacion';
import { IEspecie } from '../../api/models/i-especie';
import { INuevaPublicacionRequest } from '../../api/models/i-nueva-publicacion-request';
import { HasChangesComponent } from '../../guards/controlar-cambios-guard';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-publicaciones',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavbarComponent,
    NuevaPublicacionModalComponent,
    AutofocusDirective,
    FiltrarPorPipe
  ],
  templateUrl: './publicaciones.component.html',
  styleUrls: ['./publicaciones.component.css']
})
export class PublicacionesComponent implements HasChangesComponent {

  private readonly _pubResource = inject(PublicacionesResource);
  private readonly _authService = inject(AuthService);
  private readonly _messageService = inject(AppMessageService);
  private readonly _confirmService = inject(ConfirmDialogService);

  @ViewChild(NuevaPublicacionModalComponent) modalComponent?: NuevaPublicacionModalComponent;

  // Resolved inputs from router (withComponentInputBinding)
  publicaciones = model<IPublicacionAdopcion[]>([]);
  estados = input<IEstadoPublicacion[]>([]);
  especies = input<IEspecie[]>([]);

  // Local state signals
  searchQuery = signal<string>('');
  selectedEstado = signal<string>('Todos');
  showNuevaModal = signal<boolean>(false);
  isSaving = signal<boolean>(false);

  // Dynamic states catalog from backend resolver
  estadosCatalogo = computed(() => this.estados() || []);

  // Publications pre-filtered by selected status
  publicacionesPorEstado = computed(() => {
    const list = this.publicaciones() || [];
    const estado = this.selectedEstado();
    if (estado === 'Todos') {
      return list;
    }
    return list.filter(item => item.estadoPublicacion === estado);
  });

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
    // Si es una ruta relativa servida localmente por el backend
    if (foto.startsWith('/uploads/')) {
      return `${environment.apiUrl}${foto}`;
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
        this.publicaciones.update(list =>
          list.map(p => p.nroPublicacion === pub.nroPublicacion ? { ...p, estadoPublicacion: targetEstado } : p)
        );
        this._messageService.showMessage({
          title: 'Estado Actualizado',
          text: `La publicación de "${pub.nombreMascota}" cambió su estado a "${targetEstado}".`,
          type: 'success'
        });
      },
      error: (err) => {
        this._messageService.showMessage({
          title: 'Error al cambiar estado',
          text: err?.error?.mensajeRespuesta || err?.error?.mensaje || 'No se pudo cambiar el estado de la publicación.',
          type: 'error'
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
          this.publicaciones.update(list =>
            list.map(p => p.nroPublicacion === pub.nroPublicacion ? { ...p, estadoPublicacion: 'Finalizada' } : p)
          );
          this._messageService.showMessage({
            title: 'Publicación Finalizada',
            text: `La publicación de "${pub.nombreMascota}" ha sido finalizada correctamente.`,
            type: 'info'
          });
        },
        error: (err) => {
          this._messageService.showMessage({
            title: 'Error al finalizar',
            text: err?.error?.mensajeRespuesta || err?.error?.mensaje || 'No se pudo marcar como finalizada la publicación.',
            type: 'error'
          });
        }
      });
    });
  }

  abrirNuevaModal(): void {
    this.showNuevaModal.set(true);
  }

  cerrarNuevaModal(): void {
    if (this.isSaving()) {
      return;
    }

    if (this.modalComponent?.hasChanges()) {
      this._confirmService.confirm(
        'Descartar cambios',
        '¿Está seguro de descartar sus cambios? Se perderán los datos ingresados en la publicación.'
      ).subscribe((confirmed: boolean) => {
        if (confirmed) {
          this.showNuevaModal.set(false);
        }
      });
      return;
    }

    this.showNuevaModal.set(false);
  }

  guardarNuevaPublicacion(request: INuevaPublicacionRequest): void {
    if (this.isSaving()) {
      return;
    }

    this.isSaving.set(true);
    this._pubResource.crearPublicacion(request)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: (res) => {
          this.showNuevaModal.set(false);
          this._messageService.showMessage({
            title: 'Publicación Registrada',
            text: res?.mensajeRespuesta || res?.mensaje || 'Se registró exitosamente la nueva publicación de adopción.',
            type: 'success'
          });

          // Recargar el listado actualizado de publicaciones desde el backend
          const idRefugio = this._authService.idRefugio();
          this._pubResource.getPublicacionesPorRefugio(idRefugio).subscribe({
            next: (actualizadas) => this.publicaciones.set(actualizadas)
          });
        },
        error: (err) => {
          this._messageService.showMessage({
            title: 'Error al registrar publicación',
            text: err?.error?.mensajeRespuesta || err?.error?.mensaje || 'Ocurrió un error al intentar crear la publicación. Verifique los datos.',
            type: 'error'
          });
        }
      });
  }

}

