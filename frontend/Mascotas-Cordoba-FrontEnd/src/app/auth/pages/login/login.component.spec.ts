import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let navSpy: jasmine.Spy;

  const usuarioRefugio = {
    idCiudadano: 3,
    apellido: 'Sánchez',
    nombre: 'Roberto',
    cuil: '20-25999000-5',
    correo: 'roberto@patitas.org',
    perfil: 'REFUGIO' as const,
    idRefugio: 1,
    nombreRefugio: 'Patitas Felices'
  };

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login', 'logout']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    const router = TestBed.inject(Router);
    navSpy = spyOn(router, 'navigate');
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should navigate to /publicaciones when user has REFUGIO profile', () => {
    authServiceSpy.login.and.returnValue(of({ codigoRespuesta: 1, token: 'tok-refugio', usuario: usuarioRefugio }));

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith('20-25999000-5', 'pass123');
    expect(navSpy).toHaveBeenCalledWith(['/publicaciones']);
    expect(authServiceSpy.logout).not.toHaveBeenCalled();
    expect(component.error()).toBeNull();
  });

  it('should navigate to / (portal) for CIUDADANO, VETERINARIA and MUNICIPALIDAD profiles', () => {
    for (const perfil of ['CIUDADANO', 'VETERINARIA', 'MUNICIPALIDAD']) {
      navSpy.calls.reset();
      authServiceSpy.login.and.returnValue(of({
        codigoRespuesta: 1,
        token: 'tok-otro',
        usuario: { ...usuarioRefugio, idRefugio: undefined, nombreRefugio: undefined, perfil }
      }));

      component.onSubmit();

      expect(authServiceSpy.logout).not.toHaveBeenCalled();
      expect(navSpy).toHaveBeenCalledWith(['/']);
      expect(component.error()).toBeNull();
    }
  });

  it('should not render the removed "Recordarme" option and should include a link back to portal', () => {
    expect(fixture.nativeElement.querySelector('#recordarme')).toBeNull();
    expect(fixture.nativeElement.textContent).not.toContain('Recordarme');
    expect(fixture.nativeElement.querySelector('.volver-portal-link')).toBeTruthy();
  });

  it('should show inline errors only after submit and not call the backend', () => {
    component.form.setValue({ cuil: '', clave: '   ' });
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.field-error').length).toBe(0);

    component.onSubmit();
    fixture.detectChanges();

    const errores = fixture.nativeElement.querySelectorAll('.field-error');
    expect(errores.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('Ingresá tu CUIL.');
    expect(fixture.nativeElement.textContent).toContain('Ingresá tu contraseña.');
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });
});
