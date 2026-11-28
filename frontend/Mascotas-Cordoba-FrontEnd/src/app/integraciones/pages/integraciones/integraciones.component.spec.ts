import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { IntegracionesResource } from '../../api/integraciones-resource';
import { IntegracionesComponent } from './integraciones.component';

describe('IntegracionesComponent', () => {
  let component: IntegracionesComponent;
  let fixture: ComponentFixture<IntegracionesComponent>;
  let resourceSpy: jasmine.SpyObj<IntegracionesResource>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj<IntegracionesResource>('IntegracionesResource', [
      'listarClinicas',
      'listarLotes',
      'sincronizar'
    ]);
    resourceSpy.listarClinicas.and.returnValue(of([
      {
        idVeterinaria: 1,
        nombreVeterinaria: 'San Antonio Vet',
        urlServicio: 'http://localhost:8081/api/veterinaria',
        tecnologia: 'REST'
      },
      {
        idVeterinaria: 2,
        nombreVeterinaria: 'Veterinaria Central Córdoba',
        urlServicio: 'http://localhost:8082/services/VeterinariaSoapService',
        tecnologia: 'SOAP'
      }
    ]));
    resourceSpy.listarLotes.and.returnValue(of([]));
    resourceSpy.sincronizar.and.returnValue(of([
      {
        idVeterinaria: 1,
        nombreVeterinaria: 'San Antonio Vet',
        tecnologia: 'REST',
        fechaDesde: '2026-10-01',
        fechaHasta: '2026-11-28',
        estado: 'EXITO',
        mascotasNuevas: 2,
        mascotasDeduplicadas: 1,
        nrmsConfirmados: 3,
        atencionesProcesadas: 3
      }
    ]));

    const authStub = {
      usuario: signal({
        idCiudadano: 6,
        apellido: 'Benítez',
        nombre: 'Laura',
        cuil: '27-22333444-1',
        correo: 'laura@cordoba.gob.ar',
        perfil: 'MUNICIPALIDAD'
      }).asReadonly(),
      logout: jasmine.createSpy('logout')
    };

    await TestBed.configureTestingModule({
      imports: [IntegracionesComponent],
      providers: [
        provideRouter([]),
        { provide: IntegracionesResource, useValue: resourceSpy },
        { provide: AuthService, useValue: authStub }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(IntegracionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería listar las veterinarias configuradas y ejecutar la sincronización manual', () => {
    expect(component.clinicas().length).toBe(2);

    component.sincronizarTodas();

    expect(resourceSpy.sincronizar).toHaveBeenCalled();
    expect(component.resultadosRecientes().length).toBe(1);
    expect(component.resultadosRecientes()[0].mascotasNuevas).toBe(2);
  });
});
