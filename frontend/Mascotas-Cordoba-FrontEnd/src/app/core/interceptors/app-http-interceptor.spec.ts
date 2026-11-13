import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { appHttpInterceptor } from './app-http-interceptor';
import { LoaderService } from '../services/loader-service';
import { AuthSessionService } from '../services/auth-session.service';
import { IUsuarioRefugio } from '../../main/api/models/i-usuario-refugio';
import { environment } from '../../../environments/environment';

describe('appHttpInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let loaderService: LoaderService;
  let authSession: AuthSessionService;
  let router: Router;

  const mockUser: IUsuarioRefugio = {
    idCiudadano: 3,
    apellido: 'Sánchez',
    nombre: 'Roberto',
    cuil: '20-25999000-5',
    correo: 'roberto@patitas.org',
    perfil: 'REFUGIO',
    idRefugio: 1,
    nombreRefugio: 'Patitas Felices'
  };

  beforeEach(() => {
    sessionStorage.clear();
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([appHttpInterceptor])),
        provideHttpClientTesting()
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    loaderService = TestBed.inject(LoaderService);
    authSession = TestBed.inject(AuthSessionService);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
    localStorage.clear();
  });

  it('should inject X-API-Key header and Bearer token when session exists', () => {
    authSession.setSession(mockUser, 'tok-abc-123');
    expect(loaderService.loading()).toBeFalse();

    http.get('/test').subscribe();

    expect(loaderService.loading()).toBeTrue();
    const req = httpMock.expectOne('/test');
    expect(req.request.headers.get('X-API-Key')).toBe(environment.apiKey);
    expect(req.request.headers.get('Authorization')).toBe('Bearer tok-abc-123');

    req.flush({ ok: true });
    expect(loaderService.loading()).toBeFalse();
  });

  it('should not attach Authorization header on /auth/login even if session exists', () => {
    authSession.setSession(mockUser, 'stale-tok');
    http.post('/auth/login', {}).subscribe();

    const req = httpMock.expectOne('/auth/login');
    expect(req.request.headers.get('X-API-Key')).toBe(environment.apiKey);
    expect(req.request.headers.has('Authorization')).toBeFalse();

    req.flush({});
  });

  it('should clear session and redirect to /login on 401 from protected endpoint', () => {
    authSession.setSession(mockUser, 'expired-tok');
    const navSpy = spyOn(router, 'navigate');

    http.get('/refugios/1/publicaciones').subscribe({
      error: () => {
        // esperado
      }
    });

    const req = httpMock.expectOne('/refugios/1/publicaciones');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(authSession.isAuthenticated()).toBeFalse();
    expect(authSession.token()).toBeNull();
    expect(navSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should clear session without redirecting on 401 from /auth/me during startup check', () => {
    authSession.setSession(mockUser, 'revoked-tok', true);
    const navSpy = spyOn(router, 'navigate');

    http.get('/auth/me').subscribe({
      error: () => {
        // esperado
      }
    });

    const req = httpMock.expectOne('/auth/me');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(authSession.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('mascotas_cba_token')).toBeNull();
    expect(navSpy).not.toHaveBeenCalled();
  });

  it('should turn off loader when HTTP request fails', () => {
    http.get('/fail').subscribe({
      error: () => {
        // esperado
      }
    });

    expect(loaderService.loading()).toBeTrue();
    const req = httpMock.expectOne('/fail');
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });

    expect(loaderService.loading()).toBeFalse();
  });
});

