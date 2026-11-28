import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { VeterinariasComponent } from './veterinarias.component';
import { VeterinariasResource } from '../../api/veterinarias-resource';
import { AuthService } from '../../../core/services/auth.service';

describe('VeterinariasComponent', () => {
  let fixture: ComponentFixture<VeterinariasComponent>;
  let resourceSpy: jasmine.SpyObj<VeterinariasResource>;

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj('VeterinariasResource', ['getVeterinarias']);
    resourceSpy.getVeterinarias.and.returnValue(of({
      items: [
        {
          idVeterinaria: 1,
          nombre: 'San Antonio Vet',
          domicilio: 'Roca 221, Villa Carlos Paz',
          telefono: '3514001122',
          correo: 'sanantonio@gmail.com',
          horarios: '13:00 - 22:00',
          serviciosPrestados: 'Castración, Vacunación'
        }
      ],
      pagina: 1,
      tamanioPagina: 12,
      totalElementos: 1,
      totalPaginas: 1
    }));

    await TestBed.configureTestingModule({
      imports: [VeterinariasComponent],
      providers: [
        provideRouter([]),
        { provide: VeterinariasResource, useValue: resourceSpy },
        { provide: AuthService, useValue: { usuario: signal(null), logout: () => {} } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(VeterinariasComponent);
    fixture.detectChanges();
  });

  it('should load and display enabled clinics on init', () => {
    expect(resourceSpy.getVeterinarias).toHaveBeenCalledWith('', { pagina: 1, tamanioPagina: 12 });
    expect(fixture.nativeElement.textContent).toContain('San Antonio Vet');
    expect(fixture.nativeElement.textContent).toContain('Castración, Vacunación');
  });

  it('should filter clinics and show empty state when no results match', () => {
    resourceSpy.getVeterinarias.and.returnValue(of({
      items: [],
      pagina: 1,
      tamanioPagina: 12,
      totalElementos: 0,
      totalPaginas: 0
    }));

    fixture.componentInstance.filtroControl.setValue('Inexistente');
    fixture.componentInstance.buscar(1);
    fixture.detectChanges();

    expect(resourceSpy.getVeterinarias).toHaveBeenCalledWith('Inexistente', { pagina: 1, tamanioPagina: 12 });
    expect(fixture.nativeElement.textContent).toContain('No se encontraron veterinarias habilitadas');
  });
});
