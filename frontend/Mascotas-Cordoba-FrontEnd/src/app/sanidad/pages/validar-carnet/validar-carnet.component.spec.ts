import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { SanidadResource } from '../../api/sanidad-resource';
import { ValidarCarnetComponent } from './validar-carnet.component';

describe('ValidarCarnetComponent', () => {
  let component: ValidarCarnetComponent;
  let fixture: ComponentFixture<ValidarCarnetComponent>;
  let resourceSpy: jasmine.SpyObj<SanidadResource>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj<SanidadResource>('SanidadResource', ['validarCarnet']);
    resourceSpy.validarCarnet.and.returnValue(of({
      autentico: true,
      vigente: true,
      nroRegMunicipal: 100001,
      codigoValidacion: 'CBA-100001-SEED01',
      nombreMascota: 'Firulais',
      especie: 'Perro',
      responsableNombre: 'Juan Carlos Pérez',
      fechaEmision: '2026-10-01',
      estadoSanitario: 'Al día',
      mensaje: 'Carnet sanitario digital auténtico y vigente.'
    }));

    const authStub = {
      usuario: signal(null).asReadonly(),
      logout: jasmine.createSpy('logout')
    };

    await TestBed.configureTestingModule({
      imports: [ValidarCarnetComponent],
      providers: [
        provideRouter([]),
        { provide: SanidadResource, useValue: resourceSpy },
        { provide: AuthService, useValue: authStub },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: convertToParamMap({ codigo: 'CBA-100001-SEED01' }) } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ValidarCarnetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería validar automáticamente el código recibido por query param', () => {
    expect(resourceSpy.validarCarnet).toHaveBeenCalledWith('CBA-100001-SEED01');
    expect(component.resultado()?.autentico).toBeTrue();
    expect(component.resultado()?.vigente).toBeTrue();
    expect(fixture.nativeElement.textContent).toContain('Firulais');
  });
});
