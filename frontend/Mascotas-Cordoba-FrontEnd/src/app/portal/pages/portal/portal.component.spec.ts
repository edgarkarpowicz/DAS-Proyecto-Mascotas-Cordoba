import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { PortalComponent } from './portal.component';
import { AuthService } from '../../../core/services/auth.service';
import { IPortalInfo } from '../../api/models/i-portal-info';
import { IUsuario } from '../../../core/api/models/i-usuario';

describe('PortalComponent', () => {
  let fixture: ComponentFixture<PortalComponent>;
  let component: PortalComponent;
  const userSignal = signal<IUsuario | null>(null);
  const authSignal = signal(false);
  const perfilSignal = signal<string | null>(null);

  const portalAnonimo: IPortalInfo = {
    nombrePrograma: 'Programa Municipal de Bienestar Animal',
    descripcionPrograma: 'Portal público',
    autenticado: false,
    perfil: null,
    usuario: null,
    resumen: {
      publicacionesActivas: 5,
      veterinariasHabilitadas: 3,
      refugiosAdheridos: 2
    },
    serviciosPublicos: [
      {
        codigo: 'VETERINARIAS',
        titulo: 'Veterinarias adheridas',
        descripcion: 'Listado público',
        ruta: '/veterinarias',
        disponible: false,
        requiereAutenticacion: false,
        rfReferencia: 'RF17'
      }
    ],
    serviciosHabilitados: []
  };

  beforeEach(async () => {
    userSignal.set(null);
    authSignal.set(false);
    perfilSignal.set(null);

    await TestBed.configureTestingModule({
      imports: [PortalComponent],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            usuario: userSignal,
            isAuthenticated: authSignal,
            perfil: perfilSignal,
            logout: () => {}
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PortalComponent);
    component = fixture.componentInstance;
  });

  it('should render program metrics and login CTA when unauthenticated', () => {
    fixture.componentRef.setInput('portal', portalAnonimo);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Programa Municipal de Bienestar Animal');
    expect(fixture.nativeElement.textContent).toContain('Adopciones activas');
    expect(fixture.nativeElement.textContent).toContain('Veterinarias adheridas');
    expect(fixture.nativeElement.textContent).toContain('Iniciar sesión');
    expect(fixture.nativeElement.querySelector('[aria-labelledby="titulo-perfil"]')).toBeNull();
  });

  it('should render profile-enabled services when authenticated', () => {
    const refugioUser: IUsuario = {
      idCiudadano: 3,
      apellido: 'Sánchez',
      nombre: 'Roberto',
      cuil: '20-25999000-5',
      correo: 'roberto@patitas.org',
      perfil: 'REFUGIO',
      idRefugio: 1,
      nombreRefugio: 'Refugio Patitas Felices'
    };
    userSignal.set(refugioUser);
    authSignal.set(true);
    perfilSignal.set('REFUGIO');

    fixture.componentRef.setInput('portal', {
      ...portalAnonimo,
      autenticado: true,
      perfil: 'REFUGIO',
      usuario: refugioUser,
      serviciosHabilitados: [
        {
          codigo: 'PUBLICACIONES_ADOPCION',
          titulo: 'Gestionar publicaciones de adopción',
          descripcion: 'Administrar adopciones',
          ruta: '/publicaciones',
          disponible: true,
          requiereAutenticacion: true,
          rfReferencia: 'RF13'
        }
      ]
    });
    fixture.detectChanges();

    expect(component.estaAutenticado()).toBeTrue();
    expect(fixture.nativeElement.querySelector('[aria-labelledby="titulo-perfil"]')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Gestionar publicaciones de adopción');
  });

  it('should hide profile services and restore login CTA immediately after logout', () => {
    const refugioUser: IUsuario = {
      idCiudadano: 3,
      apellido: 'Sánchez',
      nombre: 'Roberto',
      cuil: '20-25999000-5',
      correo: 'roberto@patitas.org',
      perfil: 'REFUGIO',
      idRefugio: 1,
      nombreRefugio: 'Refugio Patitas Felices'
    };
    userSignal.set(refugioUser);
    authSignal.set(true);
    perfilSignal.set('REFUGIO');

    fixture.componentRef.setInput('portal', {
      ...portalAnonimo,
      autenticado: true,
      perfil: 'REFUGIO',
      usuario: refugioUser,
      serviciosHabilitados: [
        {
          codigo: 'PUBLICACIONES_ADOPCION',
          titulo: 'Gestionar publicaciones de adopción',
          descripcion: 'Administrar adopciones',
          ruta: '/publicaciones',
          disponible: true,
          requiereAutenticacion: true,
          rfReferencia: 'RF13'
        }
      ]
    });
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[aria-labelledby="titulo-perfil"]')).toBeTruthy();

    authSignal.set(false);
    userSignal.set(null);
    perfilSignal.set(null);
    fixture.detectChanges();

    expect(component.estaAutenticado()).toBeFalse();
    expect(fixture.nativeElement.querySelector('[aria-labelledby="titulo-perfil"]')).toBeNull();
    expect(fixture.nativeElement.textContent).toContain('Iniciar sesión');
  });

  it('should render fallback content when portal resolver returns null', () => {
    fixture.componentRef.setInput('portal', null);
    fixture.detectChanges();

    expect(component.sinDatosServidor()).toBeTrue();
    expect(fixture.nativeElement.querySelector('.aviso-conexion')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Programa Municipal de Bienestar Animal');
  });
});
