import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../../core/services/auth.service';
import { IUsuario } from '../../../core/api/models/i-usuario';

describe('NavbarComponent', () => {
  let fixture: ComponentFixture<NavbarComponent>;
  let component: NavbarComponent;
  let logoutSpy: jasmine.Spy;

  const mockUser = signal<IUsuario | null>({
    idCiudadano: 3,
    apellido: 'Sánchez',
    nombre: 'Roberto',
    cuil: '20-25999000-5',
    correo: 'roberto@patitas.org',
    perfil: 'REFUGIO',
    idRefugio: 1,
    nombreRefugio: 'Refugio Patitas Felices'
  });

  beforeEach(async () => {
    mockUser.set({
      idCiudadano: 3,
      apellido: 'Sánchez',
      nombre: 'Roberto',
      cuil: '20-25999000-5',
      correo: 'roberto@patitas.org',
      perfil: 'REFUGIO',
      idRefugio: 1,
      nombreRefugio: 'Refugio Patitas Felices'
    });
    logoutSpy = jasmine.createSpy('logout');

    await TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: { usuario: mockUser, logout: logoutSpy } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should toggle dropdown and close on outside click or Escape', () => {
    const userBtn: HTMLButtonElement = fixture.nativeElement.querySelector('.user-btn');
    userBtn.click();
    fixture.detectChanges();
    expect(component.showDropdown).toBeTrue();
    expect(fixture.nativeElement.querySelector('.dropdown-menu')).toBeTruthy();

    document.body.click();
    fixture.detectChanges();
    expect(component.showDropdown).toBeFalse();
    expect(fixture.nativeElement.querySelector('.dropdown-menu')).toBeNull();

    userBtn.click();
    fixture.detectChanges();
    expect(component.showDropdown).toBeTrue();

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    fixture.detectChanges();
    expect(component.showDropdown).toBeFalse();
  });

  it('should filter navigation links by profile and show login link when anonymous', () => {
    const linksRefugio = Array.from(fixture.nativeElement.querySelectorAll('.nav-link'))
      .map((el: unknown) => (el as HTMLElement).textContent?.trim());
    expect(linksRefugio).toEqual(['Portal', 'Veterinarias', 'Refugios', 'Validar carnet', 'Mis publicaciones', 'Carnet digital']);

    mockUser.set({
      idCiudadano: 1,
      apellido: 'Pérez',
      nombre: 'Juan',
      cuil: '20-28555666-3',
      correo: 'juan@gmail.com',
      perfil: 'CIUDADANO'
    });
    fixture.detectChanges();
    const linksCiudadano = Array.from(fixture.nativeElement.querySelectorAll('.nav-link'))
      .map((el: unknown) => (el as HTMLElement).textContent?.trim());
    expect(linksCiudadano).toEqual(['Portal', 'Veterinarias', 'Refugios', 'Validar carnet', 'Mis mascotas', 'Carnet digital']);

    mockUser.set(null);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.user-btn')).toBeNull();
    expect(fixture.nativeElement.querySelector('.login-link-btn')?.textContent?.trim()).toBe('Iniciar sesión');

    mockUser.set({
      idCiudadano: 6,
      apellido: 'Benítez',
      nombre: 'Laura',
      cuil: '27-22333444-1',
      correo: 'laura@cordoba.gob.ar',
      perfil: 'MUNICIPALIDAD'
    });
    fixture.detectChanges();
    const linksMuni = Array.from(fixture.nativeElement.querySelectorAll('.nav-link'))
      .map((el: unknown) => (el as HTMLElement).textContent?.trim());
    expect(linksMuni).toEqual([
      'Portal',
      'Veterinarias',
      'Refugios',
      'Validar carnet',
      'Registrar mascota',
      'Atenciones',
      'Carnet digital',
      'Integraciones'
    ]);
  });
});
