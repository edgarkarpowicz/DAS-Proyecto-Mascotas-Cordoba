import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { roleGuard } from './role-guard';
import { AuthService } from '../services/auth.service';

describe('roleGuard', () => {
  let isAuth = false;
  let currentPerfil = 'CIUDADANO';
  let router: Router;

  beforeEach(() => {
    isAuth = false;
    currentPerfil = 'CIUDADANO';
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isAuthenticated: () => isAuth,
            perfil: () => currentPerfil,
            hasPerfil: (...perfiles: string[]) => perfiles.includes(currentPerfil)
          }
        }
      ]
    });

    router = TestBed.inject(Router);
  });

  it('should redirect unauthenticated users to /login', () => {
    isAuth = false;
    const guard = roleGuard('REFUGIO');
    const result = TestBed.runInInjectionContext(() =>
      guard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result instanceof UrlTree).toBeTrue();
    expect(router.serializeUrl(result as UrlTree)).toBe('/login');
  });

  it('should redirect authenticated user with wrong profile to profile home', () => {
    isAuth = true;
    currentPerfil = 'CIUDADANO';
    const guard = roleGuard('REFUGIO');
    const result = TestBed.runInInjectionContext(() =>
      guard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result instanceof UrlTree).toBeTrue();
    expect(router.serializeUrl(result as UrlTree)).toBe('/');
  });

  it('should allow authenticated user with allowed profile', () => {
    isAuth = true;
    currentPerfil = 'REFUGIO';
    const guard = roleGuard('REFUGIO');
    const result = TestBed.runInInjectionContext(() =>
      guard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result).toBeTrue();
  });
});
