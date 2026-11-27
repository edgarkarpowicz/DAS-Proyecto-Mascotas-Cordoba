import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../../core/services/auth.service';
import { IUsuarioRefugio } from '../../../core/api/models/i-usuario-refugio';

describe('NavbarComponent', () => {
  let fixture: ComponentFixture<NavbarComponent>;
  let component: NavbarComponent;
  let logoutSpy: jasmine.Spy;

  const mockUser = signal<IUsuarioRefugio | null>({
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
});
