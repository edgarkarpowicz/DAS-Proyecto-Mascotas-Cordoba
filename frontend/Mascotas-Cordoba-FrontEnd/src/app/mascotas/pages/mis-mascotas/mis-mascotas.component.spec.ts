import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { MisMascotasComponent } from './mis-mascotas.component';
import { MascotasResource } from '../../api/mascotas-resource';
import { AuthService } from '../../../core/services/auth.service';

describe('MisMascotasComponent', () => {
  let fixture: ComponentFixture<MisMascotasComponent>;
  let resourceSpy: jasmine.SpyObj<MascotasResource>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj('MascotasResource', ['getMisMascotas']);
    resourceSpy.getMisMascotas.and.returnValue(of({
      items: [
        {
          nroRegMunicipal: 100001,
          nombre: 'Firulais',
          sexo: 'M',
          añoNacimiento: 2021,
          especie: 'Perro',
          raza: 'Border Collie',
          castrado: false,
          vive: true,
          cantidadAtenciones: 2,
          estadoSanitario: 'Al día'
        }
      ],
      pagina: 1,
      tamanioPagina: 12,
      totalElementos: 1,
      totalPaginas: 1
    }));

    await TestBed.configureTestingModule({
      imports: [MisMascotasComponent],
      providers: [
        provideRouter([]),
        { provide: MascotasResource, useValue: resourceSpy },
        {
          provide: AuthService,
          useValue: {
            usuario: signal({
              idCiudadano: 1,
              apellido: 'Pérez',
              nombre: 'Juan',
              cuil: '20-28555666-3',
              correo: 'juan@gmail.com',
              perfil: 'CIUDADANO'
            }),
            logout: () => {}
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MisMascotasComponent);
    fixture.detectChanges();
  });

  it('should load and render citizen pets with NRM and carnet link', () => {
    expect(resourceSpy.getMisMascotas).toHaveBeenCalledWith({ pagina: 1, tamanioPagina: 12 });
    expect(fixture.nativeElement.textContent).toContain('Firulais');
    expect(fixture.nativeElement.textContent).toContain('NRM 100001');
    expect(fixture.nativeElement.textContent).toContain('Al día');
    expect(fixture.nativeElement.querySelector('.btn-carnet')?.getAttribute('href')).toContain('/sanidad/carnet');
  });

  it('should render empty state when citizen has no registered pets', () => {
    resourceSpy.getMisMascotas.and.returnValue(of({
      items: [],
      pagina: 1,
      tamanioPagina: 12,
      totalElementos: 0,
      totalPaginas: 0
    }));

    fixture.componentInstance.cargar(1);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Todavía no tenés mascotas registradas');
  });
});
