import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { RefugiosComponent } from './refugios.component';
import { RefugiosResource } from '../../api/refugios-resource';
import { AuthService } from '../../../core/services/auth.service';

describe('RefugiosComponent', () => {
  let fixture: ComponentFixture<RefugiosComponent>;
  let resourceSpy: jasmine.SpyObj<RefugiosResource>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj('RefugiosResource', ['getRefugios', 'getPublicacionesActivas']);
    resourceSpy.getRefugios.and.returnValue(of({
      items: [
        {
          idRefugio: 1,
          nombre: 'Refugio Patitas Felices',
          domicilio: 'Camino a 60 Cuadras Km 8',
          telefono: '3516789012',
          correo: 'contacto@patitasfelices.org',
          horarios: 'Sábados 10:00 - 17:00',
          publicacionesActivas: 2
        }
      ],
      pagina: 1,
      tamanioPagina: 12,
      totalElementos: 1,
      totalPaginas: 1
    }));
    resourceSpy.getPublicacionesActivas.and.returnValue(of([
      {
        nroPublicacion: 1,
        nroRegMunicipal: 100004,
        idRefugio: 1,
        fechaPublicacion: '2026-10-01',
        caracteristicasMascota: 'Muy cariñoso',
        condicionAdopcion: 'Patio cerrado',
        estadoPublicacion: 'Activa',
        nombreMascota: 'Rocco',
        sexo: 'M',
        añoNacimiento: 2023,
        especie: 'Perro',
        raza: 'Mestizo'
      }
    ]));

    await TestBed.configureTestingModule({
      imports: [RefugiosComponent],
      providers: [
        provideRouter([]),
        { provide: RefugiosResource, useValue: resourceSpy },
        { provide: MatDialog, useValue: jasmine.createSpyObj('MatDialog', ['open']) },
        { provide: AuthService, useValue: { usuario: signal(null), logout: () => {} } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RefugiosComponent);
    fixture.detectChanges();
  });

  it('should load shelters on init and lazily fetch active publications when toggled', () => {
    expect(resourceSpy.getRefugios).toHaveBeenCalledWith('', { pagina: 1, tamanioPagina: 12 });
    expect(fixture.nativeElement.textContent).toContain('Refugio Patitas Felices');
    expect(resourceSpy.getPublicacionesActivas).not.toHaveBeenCalled();

    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn-toggle-pubs');
    btn.click();
    fixture.detectChanges();

    expect(resourceSpy.getPublicacionesActivas).toHaveBeenCalledWith(1);
    expect(fixture.nativeElement.textContent).toContain('Rocco');
  });
});
