import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { SanidadResource } from '../../api/sanidad-resource';
import { CarnetComponent } from './carnet.component';

describe('CarnetComponent', () => {
  let component: CarnetComponent;
  let fixture: ComponentFixture<CarnetComponent>;
  let resourceSpy: jasmine.SpyObj<SanidadResource>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj<SanidadResource>('SanidadResource', [
      'getMisMascotasResumen',
      'consultarCarnet',
      'generarCarnet'
    ]);
    resourceSpy.getMisMascotasResumen.and.returnValue(of([]));
    resourceSpy.consultarCarnet.and.returnValue(of({
      nroRegMunicipal: 100001,
      codigoValidacion: 'CBA-100001-SEED01',
      nombreMascota: 'Firulais',
      especie: 'Perro',
      raza: 'Border Collie',
      castrado: false,
      responsableNombre: 'Juan Carlos Pérez',
      fechaEmision: '2026-10-01',
      vigente: true,
      estadoSanitario: 'Al día',
      atenciones: [
        {
          nroRegMunicipal: 100001,
          nroRegistro: 1,
          fechaAtencion: '2026-04-10',
          tipoAtencion: 'Vacunación',
          detalleAtencion: 'Antirrábica anual',
          idVeterinaria: 1,
          veterinaria: 'San Antonio Vet'
        }
      ]
    }));
    resourceSpy.generarCarnet.and.returnValue(of({
      nroRegMunicipal: 100001,
      codigoValidacion: 'CBA-100001-NEW002',
      nombreMascota: 'Firulais',
      fechaEmision: '2026-11-28',
      vigente: true,
      atenciones: []
    }));

    const authStub = {
      usuario: signal({
        idCiudadano: 1,
        apellido: 'Pérez',
        nombre: 'Juan Carlos',
        cuil: '20-28555666-3',
        correo: 'juan.perez@gmail.com',
        perfil: 'CIUDADANO'
      }).asReadonly(),
      logout: jasmine.createSpy('logout')
    };

    await TestBed.configureTestingModule({
      imports: [CarnetComponent],
      providers: [
        provideRouter([]),
        { provide: SanidadResource, useValue: resourceSpy },
        { provide: AuthService, useValue: authStub },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: convertToParamMap({ nrm: '100001' }) } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CarnetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería cargar el carnet vigente por query param y permitir renovarlo', () => {
    expect(resourceSpy.consultarCarnet).toHaveBeenCalledWith(100001);
    expect(component.carnet()?.codigoValidacion).toBe('CBA-100001-SEED01');

    component.generarCarnet();

    expect(resourceSpy.generarCarnet).toHaveBeenCalledWith(100001);
    expect(component.carnet()?.codigoValidacion).toBe('CBA-100001-NEW002');
  });
});
