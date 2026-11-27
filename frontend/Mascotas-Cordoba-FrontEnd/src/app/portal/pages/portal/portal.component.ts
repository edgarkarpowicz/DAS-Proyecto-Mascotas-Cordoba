import { Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { getHomeRouteForPerfil } from '../../../core/config/perfil-routes';
import { AuthService } from '../../../core/services/auth.service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { IPortalInfo, IServicioHabilitado } from '../../api/models/i-portal-info';

const FALLBACK_PORTAL: IPortalInfo = {
  nombrePrograma: 'Programa Municipal de Bienestar Animal',
  descripcionPrograma:
    'Plataforma pública de Mascotas Córdoba para consultar redes adheridas, registro sanitario y servicios personalizados según cada perfil.',
  autenticado: false,
  perfil: null,
  usuario: null,
  resumen: {
    publicacionesActivas: 0,
    veterinariasHabilitadas: 0,
    refugiosAdheridos: 0
  },
  serviciosPublicos: [
    {
      codigo: 'VETERINARIAS',
      titulo: 'Veterinarias adheridas',
      descripcion: 'Consultá clínicas veterinarias habilitadas, horarios de atención y servicios.',
      ruta: '/veterinarias',
      disponible: false,
      requiereAutenticacion: false,
      rfReferencia: 'RF17'
    },
    {
      codigo: 'REFUGIOS',
      titulo: 'Refugios y adopciones',
      descripcion: 'Explorá refugios adheridos y las mascotas con publicación activa.',
      ruta: '/refugios',
      disponible: false,
      requiereAutenticacion: false,
      rfReferencia: 'RF18'
    },
    {
      codigo: 'CAMPANAS',
      titulo: 'Campañas municipales',
      descripcion: 'Agenda vigente de castración, vacunación antirrábica y jornadas territoriales.',
      ruta: '/campanas',
      disponible: false,
      requiereAutenticacion: false,
      rfReferencia: 'RF19'
    },
    {
      codigo: 'ASISTENTE',
      titulo: 'Asistente virtual',
      descripcion: 'Orientación pública sobre trámites, cuidado responsable y red municipal.',
      ruta: '/asistente',
      disponible: false,
      requiereAutenticacion: false,
      rfReferencia: 'RF21'
    }
  ],
  serviciosHabilitados: []
};

@Component({
  selector: 'app-portal',
  imports: [RouterLink, NavbarComponent],
  templateUrl: './portal.component.html',
  styleUrls: ['./portal.component.css']
})
export class PortalComponent {

  private readonly _authService = inject(AuthService);

  readonly portalResuelto = input<IPortalInfo | null>(null, { alias: 'portal' });
  readonly sinDatosServidor = computed(() => this.portalResuelto() === null);
  readonly info = computed<IPortalInfo>(() => this.portalResuelto() ?? FALLBACK_PORTAL);

  readonly estaAutenticado = computed(() => this._authService.isAuthenticated());
  readonly usuarioActual = computed(() =>
    this.estaAutenticado() ? (this._authService.usuario() ?? this.info().usuario) : null
  );
  readonly serviciosPerfil = computed<IServicioHabilitado[]>(() =>
    this.estaAutenticado() ? this.info().serviciosHabilitados : []
  );
  readonly servicioPrincipalDisponible = computed<IServicioHabilitado | null>(
    () => this.serviciosPerfil().find(s => s.disponible) ?? null
  );
  readonly rutaAccesoPerfil = computed(() => {
    const disponible = this.servicioPrincipalDisponible();
    if (disponible) {
      return disponible.ruta;
    }
    return getHomeRouteForPerfil(this.info().perfil ?? this._authService.perfil());
  });

}
