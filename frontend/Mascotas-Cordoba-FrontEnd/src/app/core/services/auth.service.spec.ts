import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from './auth.service';
import { AuthResource } from '../../main/api/resources/auth-resource';
import { IUsuarioRefugio } from '../../main/api/models/i-usuario-refugio';

describe('AuthService', () => {
  let service: AuthService;
  let authResourceSpy: jasmine.SpyObj<AuthResource>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockUser: IUsuarioRefugio = {
    idCiudadano: 10,
    apellido: 'Morales',
    nombre: 'Ana',
    cuil: '27-12345678-9',
    correo: 'ana@refugio.org',
    perfil: 'Refugio',
    idRefugio: 2,
    nombreRefugio: 'Hogar Animal Córdoba'
  };

  beforeEach(() => {
    sessionStorage.clear();
    authResourceSpy = jasmine.createSpyObj('AuthResource', ['login']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: AuthResource, useValue: authResourceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    service = TestBed.inject(AuthService);
  });

  afterEach(() => {
    sessionStorage.clear();
  });

  it('should start unauthenticated when sessionStorage is empty', () => {
    expect(service.isAuthenticated()).toBeFalse();
    expect(service.usuario()).toBeNull();
  });

  it('should store user and token on successful login', () => {
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
    expect(sessionStorage.getItem('mascotas_cba_token')).toBe('abc-token');
  });

  it('should clear session and navigate to /login on logout', () => {
    authResourceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: 'abc-token',
      usuario: mockUser
    }));
    service.login('27-12345678-9', 'pass123').subscribe();

    service.logout();

    expect(service.isAuthenticated()).toBeFalse();
    expect(service.usuario()).toBeNull();
    expect(sessionStorage.getItem('mascotas_cba_user')).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});

