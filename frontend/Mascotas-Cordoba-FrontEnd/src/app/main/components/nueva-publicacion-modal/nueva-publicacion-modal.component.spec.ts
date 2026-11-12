import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { NuevaPublicacionModalComponent } from './nueva-publicacion-modal.component';
import { PublicacionesResource } from '../../api/resources/publicaciones-resource';
import { AuthService } from '../../../core/services/auth.service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { IMascotaDisponible } from '../../api/models/i-mascota-disponible';

describe('NuevaPublicacionModalComponent', () => {
  let component: NuevaPublicacionModalComponent;
  let fixture: ComponentFixture<NuevaPublicacionModalComponent>;
  let pubResourceSpy: jasmine.SpyObj<PublicacionesResource>;
  let messageSpy: jasmine.SpyObj<AppMessageService>;

  const mockPet: IMascotaDisponible = {
    nroRegMunicipal: 100006,
    nombre: 'Thor',
    sexo: 'M',
    añoNacimiento: 2022,
    edadAproximada: 4,
    especie: 'Perro',
    raza: 'Mestizo Canino'
  };

  beforeEach(async () => {
    pubResourceSpy = jasmine.createSpyObj('PublicacionesResource', ['getMascotasDisponibles', 'getEspecies', 'subirFoto']);
    pubResourceSpy.getMascotasDisponibles.and.returnValue(of([mockPet]));
    pubResourceSpy.getEspecies.and.returnValue(of([{ nroValorDominio: 1, nomEspecie: 'Perro' }]));

    messageSpy = jasmine.createSpyObj('AppMessageService', ['showMessage']);

    await TestBed.configureTestingModule({
      imports: [NuevaPublicacionModalComponent],
      providers: [
        { provide: PublicacionesResource, useValue: pubResourceSpy },
        { provide: AuthService, useValue: { idRefugio: () => 2 } },
        { provide: AppMessageService, useValue: messageSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NuevaPublicacionModalComponent);
    component = fixture.componentInstance;
    component.especiesInput = [
      { nroValorDominio: 1, nomEspecie: 'Perro' },
      { nroValorDominio: 2, nomEspecie: 'Gato' }
    ];
    fixture.detectChanges();
  });

  it('should load available pets and species on init', () => {
    expect(pubResourceSpy.getMascotasDisponibles).toHaveBeenCalledWith(2);
    expect(component.mascotasDisponibles().length).toBe(1);
    expect(component.especiesDisponibles().length).toBe(2);
  });

  it('should lock pet identity fields when selecting existing pet and reset on 0', () => {
    component.onMascotaSelect({ target: { value: '100006' } } as unknown as Event);
    expect(component.selectedNroReg()).toBe(100006);
    expect(component.nombreMascota).toBe('Thor');
    expect(component.sexo).toBe('Macho');

    component.onMascotaSelect({ target: { value: '0' } } as unknown as Event);
    expect(component.selectedNroReg()).toBeNull();
    expect(component.nombreMascota).toBe('');
  });

  it('should track unsaved changes accurately', () => {
    expect(component.hasChanges()).toBeFalse();
    component.nombreMascota = 'Bobby';
    expect(component.hasChanges()).toBeTrue();
  });

  it('should prevent double submit and close while parent isSaving is true', () => {
    const saveSpy = spyOn(component.save, 'emit');
    const closeSpy = spyOn(component.close, 'emit');

    component.nombreMascota = 'Bobby';
    component.descripcion = 'Muy juguetón';

    fixture.componentRef.setInput('isSaving', true);
    fixture.detectChanges();

    component.onSubmit();
    component.onClose();

    expect(saveSpy).not.toHaveBeenCalled();
    expect(closeSpy).not.toHaveBeenCalled();

    fixture.componentRef.setInput('isSaving', false);
    fixture.detectChanges();

    component.onSubmit();
    expect(saveSpy).toHaveBeenCalledTimes(1);
  });
});

