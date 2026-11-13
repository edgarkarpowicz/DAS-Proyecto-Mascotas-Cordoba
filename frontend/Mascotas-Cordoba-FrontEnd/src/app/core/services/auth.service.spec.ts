import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { AuthSessionService } from './auth-session.service';
import { AuthResource } from '../../main/api/resources/auth-resource';
import { IUsuarioRefugio } from '../../main/api/models/i-usuario-refugio';

describe('AuthService', () => {
  let service: AuthService;
  let sessionService: AuthSessionService;
  let authResourceSpy: jasmine.SpyObj<AuthResource>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockUser: IUsuarioRefugio = {
    idCiudadano: 10,
    apellido: 'Morales',
    nombre: 'Ana',
    cuil: '27-12345678-9',
    correo: 'ana@refugio.org',
    perfil: 'REFUGIO',
    idRefugio: 2,
    nombreRefugio: 'Hogar Animal Córdoba'
  };

  beforeEach(() => {
    sessionStorage.clear();
    authResourceSpy = jasmine.createSpyObj('AuthResource', ['login', 'logout']);
    authResourceSpy.logout.and.returnValue(of({ codigoRespuesta: 1, mensajeRespuesta: 'OK' }));
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: AuthResource, useValue: authResourceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    sessionService = TestBed.inject(AuthSessionService);
    sessionService.clearSession();
    service = TestBed.inject(AuthService);
  });

  afterEach(() => {
    sessionStorage.clear();
  });

  it('should start unauthenticated when sessionStorage is empty', () => {
    expect(service.isAuthenticated()).toBeFalse();
    expect(service.usuario()).toBeNull();
  });

  it('should store user and token on successful login and verify profile', () => {
    authResourceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: 'abc-token',
      usuario: mockUser
    }));

    service.login(' 27-12345678-9 ', ' pass123 ').subscribe();

    expect(authResourceSpy.login).toHaveBeenCalledWith({
      cuil: '27-12345678-9',
      clave: 'pass123'
    });
    expect(service.isAuthenticated()).toBeTrue();
    expect(service.idRefugio()).toBe(2);
    expect(service.token()).toBe('abc-token');
    expect(service.hasPerfil('REFUGIO')).toBeTrue();
    expect(service.hasPerfil('CIUDADANO')).toBeFalse();
    expect(sessionStorage.getItem('mascotas_cba_token')).toBe('abc-token');
  });

  it('should not mark user authenticated if token is missing', () => {
    authResourceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: '',
      usuario: mockUser
    }));

    service.login('27-12345678-9', 'pass123').subscribe();

    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should revoke session on backend, clear storage and navigate to /login on logout', () => {
    authResourceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: 'abc-token',
      usuario: mockUser
    }));
    service.login('27-12345678-9', 'pass123').subscribe();

    service.logout();

    expect(authResourceSpy.logout).toHaveBeenCalledTimes(1);
    expect(service.isAuthenticated()).toBeFalse();
    expect(service.usuario()).toBeNull();
    expect(sessionStorage.getItem('mascotas_cba_user')).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should still clear local session when backend logout fails with 401', () => {
    authResourceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: 'abc-token',
      usuario: mockUser
    }));
    authResourceSpy.logout.and.returnValue(throwError(() => ({ status: 401 })));
    service.login('27-12345678-9', 'pass123').subscribe();

    service.logout();

    expect(service.isAuthenticated()).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});

