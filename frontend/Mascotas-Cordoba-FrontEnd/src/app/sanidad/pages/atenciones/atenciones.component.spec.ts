import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { SanidadResource } from '../../api/sanidad-resource';
import { AtencionesComponent } from './atenciones.component';

describe('AtencionesComponent', () => {
  let component: AtencionesComponent;
  let fixture: ComponentFixture<AtencionesComponent>;
  let resourceSpy: jasmine.SpyObj<SanidadResource>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj<SanidadResource>('SanidadResource', [
      'getTiposAtencion',
      'getVeterinariasActivas',
      'consultarAtenciones',
      'registrarAtencion'
    ]);
    resourceSpy.getTiposAtencion.and.returnValue(of([{ codTipoAtencion: 1, descTipoAtencion: 'Vacunación' }]));
    resourceSpy.getVeterinariasActivas.and.returnValue(of([{ idVeterinaria: 1, nombre: 'San Antonio Vet' }]));
    resourceSpy.consultarAtenciones.and.returnValue(of([]));
    resourceSpy.registrarAtencion.and.returnValue(of({
      nroRegMunicipal: 100001,
      nroRegistro: 3,
      fechaAtencion: '2026-10-01',
      tipoAtencion: 'Vacunación',
      detalleAtencion: 'Antirrábica anual Lote Z1',
      idVeterinaria: 1,
      veterinaria: 'San Antonio Vet'
    }));

    const authStub = {
      usuario: signal({
        idCiudadano: 5,
        apellido: 'Quito',
        nombre: 'Esteban',
        cuil: '20-30111222-8',
        correo: 'esteban@sanantoniovet.com',
        perfil: 'VETERINARIA',
        idVeterinaria: 1,
        nombreVeterinaria: 'San Antonio Vet'
      }).asReadonly(),
      logout: jasmine.createSpy('logout')
    };

    await TestBed.configureTestingModule({
      imports: [AtencionesComponent],
      providers: [
        provideRouter([]),
        { provide: SanidadResource, useValue: resourceSpy },
        { provide: AuthService, useValue: authStub }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AtencionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería cargar tipos y registrar una atención para la veterinaria de sesión', () => {
    component.form.patchValue({
      nroRegMunicipal: '100001',
      codTipoAtencion: '1',
      detalleAtencion: 'Antirrábica anual Lote Z1'
    });

    component.registrar();

    expect(resourceSpy.registrarAtencion).toHaveBeenCalledWith(jasmine.objectContaining({
      nroRegMunicipal: 100001,
      codTipoAtencion: 1,
      idVeterinaria: 1
    }));
    expect(component.exito()?.nroRegistro).toBe(3);
  });
});
