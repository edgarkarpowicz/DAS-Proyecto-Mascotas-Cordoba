import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { PublicacionesComponent } from './publicaciones.component';
import { PublicacionesResource } from '../../api/publicaciones-resource';
import { EstadoPublicacionService, accionesPermitidas } from '../../services/estado-publicacion.service';
import { AuthService } from '../../../core/services/auth.service';
import { NuevaPublicacionModalComponent } from '../../components/nueva-publicacion-modal/nueva-publicacion-modal.component';
import { DetallePublicacionModalComponent } from '../../components/detalle-publicacion-modal/detalle-publicacion-modal.component';
import { ImagenLightboxModalComponent } from '../../../shared/components/imagen-lightbox-modal/imagen-lightbox-modal.component';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';

describe('PublicacionesComponent', () => {
  let component: PublicacionesComponent;
  let fixture: ComponentFixture<PublicacionesComponent>;
  let pubResourceSpy: jasmine.SpyObj<PublicacionesResource>;
  let estadoSpy: jasmine.SpyObj<EstadoPublicacionService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const activa: IPublicacionAdopcion = {
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
  const finalizada: IPublicacionAdopcion = {
    ...activa,
    nroPublicacion: 2,
    nroRegMunicipal: 100005,
    nombreMascota: 'Mora',
    estadoPublicacion: 'Finalizada'
  };

  function dialogRefCon(resultado: unknown, componentInstance: unknown = {}) {
    return { afterClosed: () => of(resultado), componentInstance } as never;
  }

  beforeEach(async () => {
    pubResourceSpy = jasmine.createSpyObj('PublicacionesResource', ['getPublicacionesPorRefugio']);
    pubResourceSpy.getPublicacionesPorRefugio.and.returnValue(of([activa, finalizada]));

    estadoSpy = jasmine.createSpyObj('EstadoPublicacionService', ['accionesDisponibles', 'cambiarEstado']);
    estadoSpy.accionesDisponibles.and.callFake(accionesPermitidas);

    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    dialogSpy.open.and.returnValue(dialogRefCon(undefined));

    await TestBed.configureTestingModule({
      imports: [PublicacionesComponent],
      providers: [
        provideRouter([]),
        { provide: PublicacionesResource, useValue: pubResourceSpy },
        { provide: EstadoPublicacionService, useValue: estadoSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: AuthService, useValue: { idRefugio: () => 1, usuario: () => null, logout: () => {} } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PublicacionesComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('publicaciones', [activa, finalizada]);
    fixture.componentRef.setInput('estados', [{ codEstado: 'Activa', descripcion: 'Activa' }]);
    fixture.detectChanges();
  });

  it('should render the publications resolved on navigation', () => {
    const cards = fixture.nativeElement.querySelectorAll('.pet-card');
    expect(cards.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('Rocco');
  });

  it('should not offer reactivation for a finalized publication', () => {
    const cards: HTMLElement[] = Array.from(fixture.nativeElement.querySelectorAll('.pet-card'));
    const cardFinalizada = cards.find(c => c.textContent?.includes('Mora'))!;
    expect(cardFinalizada.querySelectorAll('.card-actions-footer button').length).toBe(0);
    expect(fixture.nativeElement.querySelector('[title="Reactivar publicación"]')).toBeNull();
  });

  it('should filter by name and state with reactive form controls', () => {
    component.busqueda.setValue('mor');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.pet-card').length).toBe(1);

    component.busqueda.setValue('');
    component.estadoFiltro.setValue('Activa');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.pet-card').length).toBe(1);
    expect(fixture.nativeElement.textContent).toContain('Rocco');
  });

  it('should pause from the card without opening the detail dialog', () => {
    estadoSpy.cambiarEstado.and.returnValue(of({ ...activa, estadoPublicacion: 'Pausada' }));
    const pauseBtn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn-pause');

    pauseBtn.click();
    fixture.detectChanges();

    expect(estadoSpy.cambiarEstado).toHaveBeenCalledWith(activa, 'Pausada');
    expect(component.publicaciones()[0].estadoPublicacion).toBe('Pausada');
    expect(dialogSpy.open).not.toHaveBeenCalled();
  });

  it('should open the detail dialog by reference and apply the publication it returns', () => {
    dialogSpy.open.and.returnValue(dialogRefCon({ ...activa, estadoPublicacion: 'Pausada' }));

    fixture.nativeElement.querySelector('.pet-card').click();

    expect(dialogSpy.open).toHaveBeenCalledWith(DetallePublicacionModalComponent, jasmine.objectContaining({
      data: { publicacion: activa }
    }));
    expect(component.publicaciones()[0].estadoPublicacion).toBe('Pausada');
  });

  it('should open the lightbox from the card without opening the detail dialog', () => {
    fixture.nativeElement.querySelector('.card-expand-btn .expand-image-btn').click();

    expect(dialogSpy.open).toHaveBeenCalledTimes(1);
    expect(dialogSpy.open.calls.mostRecent().args[0]).toBe(ImagenLightboxModalComponent);
  });

  it('should reload the list only when the new publication dialog reports it was saved', () => {
    dialogSpy.open.and.returnValue(dialogRefCon(false));
    component.abrirNuevaModal();
    expect(dialogSpy.open.calls.mostRecent().args[0]).toBe(NuevaPublicacionModalComponent);
    expect(pubResourceSpy.getPublicacionesPorRefugio).not.toHaveBeenCalled();

    dialogSpy.open.and.returnValue(dialogRefCon(true));
    component.abrirNuevaModal();
    expect(pubResourceSpy.getPublicacionesPorRefugio).toHaveBeenCalledWith(1);
  });

  it('should report unsaved changes while the new publication dialog has data', () => {
    expect(component.hasChanges()).toBeFalse();

    dialogSpy.open.and.returnValue({
      afterClosed: () => of(),
      componentInstance: { hasChanges: () => true }
    } as never);
    component.abrirNuevaModal();

    expect(component.hasChanges()).toBeTrue();
  });
});
