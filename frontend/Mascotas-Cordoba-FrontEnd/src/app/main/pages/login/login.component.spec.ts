import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login', 'logout']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should navigate to /publicaciones when user has REFUGIO profile', () => {
    authServiceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: 'tok-refugio',
      usuario: {
        idCiudadano: 3,
        apellido: 'Sánchez',
        nombre: 'Roberto',
        cuil: '20-25999000-5',
        correo: 'roberto@patitas.org',
        perfil: 'REFUGIO',
        idRefugio: 1,
        nombreRefugio: 'Patitas Felices'
      }
    }));

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith('20-25999000-5', 'pass123', false);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/publicaciones']);
    expect(authServiceSpy.logout).not.toHaveBeenCalled();
    expect(component.error).toBeNull();
  });

  it('should pass recordarme=true to AuthService.login when checkbox is checked', () => {
    authServiceSpy.login.and.returnValue(of({
      codigoRespuesta: 1,
      token: 'tok-refugio',
      usuario: {
        idCiudadano: 3,
        apellido: 'Sánchez',
        nombre: 'Roberto',
        cuil: '20-25999000-5',
        correo: 'roberto@patitas.org',
        perfil: 'REFUGIO',
        idRefugio: 1
      }
    }));

    const checkbox: HTMLInputElement | null = fixture.nativeElement.querySelector('#recordarme');
    expect(checkbox).not.toBeNull();
    checkbox!.click();
    fixture.detectChanges();

    component.onSubmit();

    expect(component.recordarme).toBeTrue();
    expect(authServiceSpy.login).toHaveBeenCalledWith('20-25999000-5', 'pass123', true);
  });

  it('should reject non-REFUGIO profiles cleanly and revoke session', () => {
    for (const perfil of ['CIUDADANO', 'VETERINARIA', 'MUNICIPALIDAD']) {
      authServiceSpy.logout.calls.reset();
      routerSpy.navigate.calls.reset();
      authServiceSpy.login.and.returnValue(of({
        codigoRespuesta: 1,
        token: 'tok-otro',
        usuario: {
          idCiudadano: 5,
          apellido: 'Quito',
          nombre: 'Esteban',
          cuil: '20-30111222-8',
          correo: 'esteban@vet.com',
          perfil
        }
      }));

      component.onSubmit();

      expect(authServiceSpy.logout).toHaveBeenCalledWith(false);
      expect(routerSpy.navigate).not.toHaveBeenCalled();
      expect(component.error).toContain(perfil);
    }
  });
});

