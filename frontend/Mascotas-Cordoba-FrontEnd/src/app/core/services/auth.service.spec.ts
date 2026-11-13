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

  function buildJwt(expEpochSeconds: number): string {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' })).replace(/=+$/, '');
    const payload = btoa(JSON.stringify({ sub: '10', exp: expEpochSeconds })).replace(/=+$/, '');
    return `${header}.${payload}.sig`;
  }

  beforeEach(() => {
    sessionStorage.clear();
    localStorage.clear();
    authResourceSpy = jasmine.createSpyObj('AuthResource', ['login', 'logout', 'me']);
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
    localStorage.clear();
  });

  it('should start unauthenticated when storages are empty', () => {
    expect(service.isAuthenticated()).toBeFalse();
    expect(service.usuario()).toBeNull();
  });

  it('should store user and token in sessionStorage when recordarme is false', () => {
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
    expect(localStorage.getItem('mascotas_cba_token')).toBeNull();
  });

  it('should persist user and token in localStorage when recordarme is true', () => {
    const validJwt = buildJwt(Math.floor(Date.now() / 1000) + 3600);
    authResourceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: validJwt,
      usuario: mockUser
    }));

    service.login('27-12345678-9', 'pass123', true).subscribe();

    expect(localStorage.getItem('mascotas_cba_token')).toBe(validJwt);
    expect(sessionStorage.getItem('mascotas_cba_token')).toBeNull();
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('should ignore expired JWT stored in localStorage when creating AuthSessionService', () => {
    const expiredJwt = buildJwt(Math.floor(Date.now() / 1000) - 60);
    localStorage.setItem('mascotas_cba_token', expiredJwt);
    localStorage.setItem('mascotas_cba_user', JSON.stringify(mockUser));

    const freshSession = new AuthSessionService();

    expect(freshSession.token()).toBeNull();
    expect(freshSession.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('mascotas_cba_token')).toBeNull();
  });

  it('should hydrate usuario via /auth/me on restoreSession and clear on 401', () => {
    const validJwt = buildJwt(Math.floor(Date.now() / 1000) + 3600);
    localStorage.setItem('mascotas_cba_token', validJwt);
    localStorage.setItem('mascotas_cba_user', JSON.stringify(mockUser));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: AuthResource, useValue: authResourceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });
    const freshAuthService = TestBed.inject(AuthService);
    const updatedUser: IUsuarioRefugio = { ...mockUser, nombre: 'Ana Sofía' };
    authResourceSpy.me.and.returnValue(of(updatedUser));

    freshAuthService.restoreSession().subscribe();

    expect(authResourceSpy.me).toHaveBeenCalledTimes(1);
    expect(freshAuthService.usuario()?.nombre).toBe('Ana Sofía');
    expect(JSON.parse(localStorage.getItem('mascotas_cba_user') || '{}').nombre).toBe('Ana Sofía');

    authResourceSpy.me.and.returnValue(throwError(() => ({ status: 401 })));
    freshAuthService.restoreSession().subscribe();

    expect(freshAuthService.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('mascotas_cba_token')).toBeNull();
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

