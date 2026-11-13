import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, Subject } from 'rxjs';
import { PublicacionesComponent } from './publicaciones.component';
import { PublicacionesResource } from '../../api/resources/publicaciones-resource';
import { AuthService } from '../../../core/services/auth.service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';
import { IAdministrarPublicacionResponse } from '../../api/models/i-administrar-publicacion-response';

describe('PublicacionesComponent', () => {
  let component: PublicacionesComponent;
  let fixture: ComponentFixture<PublicacionesComponent>;
  let pubResourceSpy: jasmine.SpyObj<PublicacionesResource>;
  let messageSpy: jasmine.SpyObj<AppMessageService>;
  let confirmSpy: jasmine.SpyObj<ConfirmDialogService>;

  const samplePub: IPublicacionAdopcion = {
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
  };

  beforeEach(async () => {
    pubResourceSpy = jasmine.createSpyObj('PublicacionesResource', [
      'getMascotasDisponibles',
      'getEspecies',
      'getPublicacionesPorRefugio',
      'crearPublicacion',
      'actualizarEstado'
    ]);
    pubResourceSpy.getMascotasDisponibles.and.returnValue(of([]));
    pubResourceSpy.getEspecies.and.returnValue(of([]));
    pubResourceSpy.getPublicacionesPorRefugio.and.returnValue(of([samplePub]));

    messageSpy = jasmine.createSpyObj('AppMessageService', ['showMessage']);
    confirmSpy = jasmine.createSpyObj('ConfirmDialogService', ['confirm']);

    await TestBed.configureTestingModule({
      imports: [PublicacionesComponent],
      providers: [
        provideRouter([]),
        { provide: PublicacionesResource, useValue: pubResourceSpy },
        { provide: AuthService, useValue: { idRefugio: () => 1, usuario: () => null, logout: () => {} } },
        { provide: AppMessageService, useValue: messageSpy },
        { provide: ConfirmDialogService, useValue: confirmSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PublicacionesComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('publicaciones', [samplePub]);
    fixture.componentRef.setInput('estados', [{ codEstado: 'Activa', descripcion: 'Activa' }]);
    fixture.componentRef.setInput('especies', [{ nroValorDominio: 1, nomEspecie: 'Perro' }]);
    fixture.detectChanges();
  });

  it('should update publicaciones model signal on toggleEstado', () => {
    pubResourceSpy.actualizarEstado.and.returnValue(of({ codigoRespuesta: 0, nroPublicacion: 1 }));

    component.toggleEstado(samplePub);

    expect(component.publicaciones()[0].estadoPublicacion).toBe('Pausada');
    expect(messageSpy.showMessage).toHaveBeenCalled();
  });

  it('should block double submit when saving a publication without photo', () => {
    const response$ = new Subject<IAdministrarPublicacionResponse>();
    pubResourceSpy.crearPublicacion.and.returnValue(response$.asObservable());
    component.abrirNuevaModal();

    const req = {
      idRefugio: 1,
      nombreMascota: 'Firulais',
      caracteristicasMascota: 'Juguetón'
    };

    component.guardarNuevaPublicacion(req);
    component.guardarNuevaPublicacion(req);

    expect(component.isSaving()).toBeTrue();
    expect(pubResourceSpy.crearPublicacion).toHaveBeenCalledTimes(1);

    response$.next({ codigoRespuesta: 1, nroPublicacion: 2, mensajeRespuesta: 'Creada' });
    response$.complete();

    expect(component.isSaving()).toBeFalse();
    expect(component.showNuevaModal()).toBeFalse();
  });

  it('should confirm discard when closing modal with unsaved changes', () => {
    component.abrirNuevaModal();
    fixture.detectChanges();

    expect(component.modalComponent).toBeTruthy();
    component.modalComponent!.nombreMascota = 'Luna';

    confirmSpy.confirm.and.returnValue(of(false));
    component.cerrarNuevaModal();
    expect(confirmSpy.confirm).toHaveBeenCalled();
    expect(component.showNuevaModal()).toBeTrue();

    confirmSpy.confirm.and.returnValue(of(true));
    component.cerrarNuevaModal();
    expect(component.showNuevaModal()).toBeFalse();
  });

  it('should open detail modal when clicking a card and render full pet details', () => {
    const card: HTMLElement = fixture.nativeElement.querySelector('.pet-card');
    expect(card).toBeTruthy();

    card.click();
    fixture.detectChanges();

    const dialog: HTMLElement = fixture.nativeElement.querySelector('[role="dialog"]');
    expect(dialog).toBeTruthy();
    expect(dialog.textContent).toContain('Rocco');
    expect(dialog.textContent).toContain('Mestizo');
    expect(dialog.textContent).toContain('Patio cerrado');
    expect(dialog.textContent).toContain('Muy cariñoso');
  });

  it('should not open detail modal when clicking footer action buttons', () => {
    pubResourceSpy.actualizarEstado.and.returnValue(of({ codigoRespuesta: 0, nroPublicacion: 1 }));
    const pauseBtn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn-pause');
    expect(pauseBtn).toBeTruthy();

    pauseBtn.click();
    fixture.detectChanges();

    expect(pubResourceSpy.actualizarEstado).toHaveBeenCalledTimes(1);
    expect(component.selectedPublicacion()).toBeNull();
    expect(fixture.nativeElement.querySelector('[role="dialog"]')).toBeNull();
  });

  it('should open full image lightbox from card bottom-right button without opening detail modal', () => {
    const expandBtn: HTMLButtonElement | null = fixture.nativeElement.querySelector('.card-expand-btn .expand-image-btn');
    expect(expandBtn).toBeTruthy();

    expandBtn!.click();
    fixture.detectChanges();

    expect(component.selectedPublicacion()).toBeNull();
    expect(component.lightboxPublicacion()).toEqual(samplePub);
    expect(fixture.nativeElement.querySelector('.lightbox-image')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('.detail-card')).toBeNull();
  });
});

