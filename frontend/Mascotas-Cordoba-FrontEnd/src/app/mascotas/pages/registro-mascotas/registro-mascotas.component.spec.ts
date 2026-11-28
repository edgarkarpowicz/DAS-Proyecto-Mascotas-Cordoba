import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { RegistroMascotasComponent } from './registro-mascotas.component';
import { MascotasResource } from '../../api/mascotas-resource';
import { AppMessageService } from '../../../core/services/app-message-service';
import { AuthService } from '../../../core/services/auth.service';

describe('RegistroMascotasComponent', () => {
  let fixture: ComponentFixture<RegistroMascotasComponent>;
  let component: RegistroMascotasComponent;
  let resourceSpy: jasmine.SpyObj<MascotasResource>;
  let messageSpy: jasmine.SpyObj<AppMessageService>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj('MascotasResource', ['getEspecies', 'registrarMascota']);
    resourceSpy.getEspecies.and.returnValue(of([
      { nroValorDominio: 1, nomEspecie: 'Perro' },
      { nroValorDominio: 2, nomEspecie: 'Gato' }
    ]));
    messageSpy = jasmine.createSpyObj('AppMessageService', ['notify', 'showMessage']);

    await TestBed.configureTestingModule({
      imports: [RegistroMascotasComponent],
      providers: [
        provideRouter([]),
        { provide: MascotasResource, useValue: resourceSpy },
        { provide: AppMessageService, useValue: messageSpy },
        { provide: AuthService, useValue: { usuario: signal(null), logout: () => {} } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegistroMascotasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show inline validation errors on submit without calling backend', () => {
    component.onSubmit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('.field-error').length).toBeGreaterThan(0);
    expect(resourceSpy.registrarMascota).not.toHaveBeenCalled();
  });

  it('should submit pet and display reused NRM feedback when duplicate is detected', () => {
    resourceSpy.registrarMascota.and.returnValue(of({
      nroRegMunicipal: 100001,
      nombre: 'Firulais',
      sexo: 'M',
      añoNacimiento: 2021,
      especie: 'Perro',
      castrado: false,
      vive: true,
      nrmReutilizado: true
    }));

    component.form.patchValue({
      nombre: 'Firulais',
      especie: 'Perro',
      sexo: 'Macho',
      anioNacimiento: new Date(2021, 0, 1),
      microchip: '981098104523123'
    });
    component.onSubmit();
    fixture.detectChanges();

    expect(resourceSpy.registrarMascota).toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('NRM existente reutilizado: 100001');
    expect(messageSpy.notify).toHaveBeenCalled();
  });
});
