import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { EMPTY, Subject, of } from 'rxjs';
import { DetallePublicacionModalComponent } from './detalle-publicacion-modal.component';
import { EstadoPublicacionService, accionesPermitidas } from '../../services/estado-publicacion.service';
import { ImagenLightboxModalComponent } from '../../../shared/components/imagen-lightbox-modal/imagen-lightbox-modal.component';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';

describe('DetallePublicacionModalComponent', () => {
  let fixture: ComponentFixture<DetallePublicacionModalComponent>;
  let component: DetallePublicacionModalComponent;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<DetallePublicacionModalComponent>>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;
  let estadoSpy: jasmine.SpyObj<EstadoPublicacionService>;
  let backdrop$: Subject<MouseEvent>;
  let keydown$: Subject<KeyboardEvent>;

  const samplePub: IPublicacionAdopcion = {
    nroPublicacion: 4,
    nroRegMunicipal: 100008,
    idRefugio: 1,
    fechaPublicacion: '2026-10-15',
    caracteristicasMascota: 'Perra dócil, castrada y con vacunas al día',
    condicionAdopcion: 'Patio cerrado y seguimiento veterinario',
    foto: '/uploads/publicaciones/luna.jpg',
    estadoPublicacion: 'Activa',
    nombreMascota: 'Luna',
    sexo: 'H',
    añoNacimiento: 2022,
    edadAproximada: 4,
    especie: 'Perro',
    raza: 'Border Collie'
  };

  function crear(pub: IPublicacionAdopcion): void {
    TestBed.overrideProvider(MAT_DIALOG_DATA, { useValue: { publicacion: pub } });
    fixture = TestBed.createComponent(DetallePublicacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(async () => {
    backdrop$ = new Subject<MouseEvent>();
    keydown$ = new Subject<KeyboardEvent>();
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close', 'backdropClick', 'keydownEvents']);
    dialogRefSpy.backdropClick.and.returnValue(backdrop$.asObservable());
    dialogRefSpy.keydownEvents.and.returnValue(keydown$.asObservable());
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    // Reglas reales de transición: Activa -> Pausar/Finalizar, Finalizada -> ninguna
    estadoSpy = jasmine.createSpyObj('EstadoPublicacionService', ['accionesDisponibles', 'cambiarEstado']);
    estadoSpy.accionesDisponibles.and.callFake(accionesPermitidas);

    await TestBed.configureTestingModule({
      imports: [DetallePublicacionModalComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: EstadoPublicacionService, useValue: estadoSpy },
        { provide: MAT_DIALOG_DATA, useValue: { publicacion: samplePub } }
      ]
    }).compileComponents();
  });

  it('should render photograph and all required pet and adoption details', () => {
    crear(samplePub);
    const el: HTMLElement = fixture.nativeElement;
    const img: HTMLImageElement | null = el.querySelector('.detail-hero-img');

    expect(img).toBeTruthy();
    expect(img!.src).toContain('/uploads/publicaciones/luna.jpg');
    expect(el.textContent).toContain('Luna');
    expect(el.textContent).toContain('Hembra');
    expect(el.textContent).toContain('4 años');
    expect(el.textContent).toContain('(nac. 2022)');
    expect(el.textContent).toContain('Border Collie');
    expect(el.textContent).toContain('Perra dócil, castrada y con vacunas al día');
    expect(el.textContent).toContain('Patio cerrado y seguimiento veterinario');
    expect(el.textContent).toContain('15/10/2026');
  });

  it('should save the state change itself and return the updated publication on close', () => {
    crear(samplePub);
    estadoSpy.cambiarEstado.and.returnValue(of({ ...samplePub, estadoPublicacion: 'Pausada' }));

    const pausar: HTMLButtonElement = fixture.nativeElement.querySelector('.btn-outline-warn');
    pausar.click();
    fixture.detectChanges();

    expect(estadoSpy.cambiarEstado).toHaveBeenCalledWith(samplePub, 'Pausada');
    expect(component.publicacion().estadoPublicacion).toBe('Pausada');
    expect(fixture.nativeElement.textContent).toContain('Reanudar publicación');

    keydown$.next(new KeyboardEvent('keydown', { key: 'Escape' }));
    expect(dialogRefSpy.close).toHaveBeenCalledWith(jasmine.objectContaining({ estadoPublicacion: 'Pausada' }));
  });

  it('should keep the publication unchanged when the user cancels the change', () => {
    crear(samplePub);
    estadoSpy.cambiarEstado.and.returnValue(EMPTY);

    component.cambiarEstado('Finalizada');

    expect(component.publicacion().estadoPublicacion).toBe('Activa');
    expect(component.procesando()).toBeFalse();
  });

  it('should not offer any action (no reactivar) for a finalized publication', () => {
    crear({ ...samplePub, estadoPublicacion: 'Finalizada' });
    const el: HTMLElement = fixture.nativeElement;

    expect(el.querySelectorAll('.detail-state-actions button').length).toBe(0);
    expect(el.textContent).not.toContain('Reactivar');
    expect(el.textContent).toContain('no admite cambios de estado');
  });

  it('should close returning the publication on backdrop click', () => {
    crear(samplePub);
    backdrop$.next(new MouseEvent('click'));
    expect(dialogRefSpy.close).toHaveBeenCalledWith(samplePub);
  });

  it('should open the image lightbox by reference', () => {
    crear(samplePub);
    const expandBtn: HTMLButtonElement = fixture.nativeElement.querySelector('.detail-expand-btn .expand-image-btn');
    expandBtn.click();

    expect(dialogSpy.open).toHaveBeenCalledWith(ImagenLightboxModalComponent, jasmine.objectContaining({
      data: jasmine.objectContaining({ altText: 'Luna' })
    }));
  });

  it('should ignore close attempts while a state update is in flight', () => {
    crear(samplePub);
    const pending$ = new Subject<IPublicacionAdopcion>();
    estadoSpy.cambiarEstado.and.returnValue(pending$.asObservable());

    component.cambiarEstado('Pausada');
    expect(component.procesando()).toBeTrue();

    component.cerrar();
    backdrop$.next(new MouseEvent('click'));
    keydown$.next(new KeyboardEvent('keydown', { key: 'Escape' }));
    expect(dialogRefSpy.close).not.toHaveBeenCalled();

    pending$.next({ ...samplePub, estadoPublicacion: 'Pausada' });
    pending$.complete();
    expect(component.procesando()).toBeFalse();

    component.cerrar();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(jasmine.objectContaining({ estadoPublicacion: 'Pausada' }));
  });
});
