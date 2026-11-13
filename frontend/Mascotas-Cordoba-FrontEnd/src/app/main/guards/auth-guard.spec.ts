import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { authGuard } from './auth-guard';
import { AuthService } from '../../core/services/auth.service';

describe('authGuard', () => {
  let isAuth = false;
  let allowedPerfil = true;
  let router: Router;

  beforeEach(() => {
    isAuth = false;
    allowedPerfil = true;
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isAuthenticated: () => isAuth,
            hasPerfil: () => allowedPerfil
          }
        }
      ]
    });

    router = TestBed.inject(Router);
  });

  it('should allow access when user is authenticated and profile matches', () => {
    isAuth = true;
    allowedPerfil = true;
    const result = TestBed.runInInjectionContext(() =>
      authGuard({ data: { perfilesPermitidos: ['REFUGIO'] } } as unknown as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result).toBeTrue();
  });

  it('should redirect to /login when user lacks required profile', () => {
    isAuth = true;
    allowedPerfil = false;
    const result = TestBed.runInInjectionContext(() =>
      authGuard({ data: { perfilesPermitidos: ['REFUGIO'] } } as unknown as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result instanceof UrlTree).toBeTrue();
    expect(router.serializeUrl(result as UrlTree)).toBe('/login');
  });

  it('should redirect to /login when user is not authenticated', () => {
    isAuth = false;
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
    expect(result instanceof UrlTree).toBeTrue();
    expect(router.serializeUrl(result as UrlTree)).toBe('/login');
  });
});

