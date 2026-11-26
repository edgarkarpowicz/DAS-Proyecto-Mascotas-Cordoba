import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { Subject, of, throwError } from 'rxjs';
import { NuevaPublicacionModalComponent } from './nueva-publicacion-modal.component';
import { PublicacionesResource } from '../../api/publicaciones-resource';
import { AuthService } from '../../../core/services/auth.service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { IMascotaDisponible } from '../../api/models/i-mascota-disponible';
import { IAdministrarPublicacionResponse } from '../../api/models/i-administrar-publicacion-response';

describe('NuevaPublicacionModalComponent', () => {
  let fixture: ComponentFixture<NuevaPublicacionModalComponent>;
  let component: NuevaPublicacionModalComponent;
  let resourceSpy: jasmine.SpyObj<PublicacionesResource>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<NuevaPublicacionModalComponent, boolean>>;
  let messageSpy: jasmine.SpyObj<AppMessageService>;
  let confirmSpy: jasmine.SpyObj<ConfirmDialogService>;

  const mascotaRegistrada: IMascotaDisponible = {
    nroRegMunicipal: 100004,
    nombre: 'Rocco',
    sexo: 'M',
    añoNacimiento: 2021,
    especie: 'Perro',
    raza: 'Mestizo'
  };

  function completarFormularioValido(): void {
    component.form.patchValue({
      nombreMascota: 'Firulais',
      especie: 'Perro',
      sexo: 'Macho',
      anioNacimiento: new Date(2022, 0, 1),
      raza: 'Mestizo',
      condicionAdopcion: 'Patio cerrado',
      descripcion: 'Juguetón y sociable'
    });
    component.form.markAsDirty();
  }

  beforeEach(async () => {
    resourceSpy = jasmine.createSpyObj('PublicacionesResource', [
      'getMascotasDisponibles', 'getEspecies', 'crearPublicacion', 'subirFoto'
    ]);
    resourceSpy.getMascotasDisponibles.and.returnValue(of([mascotaRegistrada]));
    resourceSpy.getEspecies.and.returnValue(of([
      { nroValorDominio: 1, nomEspecie: 'Perro' },
      { nroValorDominio: 2, nomEspecie: 'Gato' }
    ]));
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close', 'backdropClick', 'keydownEvents']);
    dialogRefSpy.backdropClick.and.returnValue(new Subject<MouseEvent>().asObservable());
    dialogRefSpy.keydownEvents.and.returnValue(new Subject<KeyboardEvent>().asObservable());
    messageSpy = jasmine.createSpyObj('AppMessageService', ['showMessage', 'notify']);
    confirmSpy = jasmine.createSpyObj('ConfirmDialogService', ['confirm']);

    await TestBed.configureTestingModule({
      imports: [NuevaPublicacionModalComponent],
      providers: [
        { provide: PublicacionesResource, useValue: resourceSpy },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: AuthService, useValue: { idRefugio: () => 1 } },
        { provide: AppMessageService, useValue: messageSpy },
        { provide: ConfirmDialogService, useValue: confirmSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NuevaPublicacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load its own data (available pets and species) instead of receiving it', () => {
    expect(resourceSpy.getMascotasDisponibles).toHaveBeenCalledWith(1);
    expect(resourceSpy.getEspecies).toHaveBeenCalled();
    expect(component.mascotasDisponibles().length).toBe(1);
    expect(component.form.controls.especie.value).toBe('Perro');
  });

  it('should ask for adoption conditions in a textarea and the birth year with a year calendar', () => {
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('textarea#condicionAdopcion')).toBeTruthy();
    expect(el.querySelector('input[type="date"]')).toBeNull();
    expect(el.querySelector('#anioNacimiento')).toBeTruthy();
  });

  it('should show inline errors only after submit and not save an invalid form', () => {
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelectorAll('.field-error').length).toBe(0);

    el.querySelector<HTMLButtonElement>('.save-btn')!.click();
    fixture.detectChanges();

    expect(el.querySelectorAll('.field-error').length).toBeGreaterThanOrEqual(4);
    expect(el.textContent).toContain('Ingresá el nombre de la mascota.');
    expect(el.textContent).toContain('Elegí el año de nacimiento.');
    expect(el.textContent).toContain('Ingresá las condiciones de adopción.');
    expect(resourceSpy.crearPublicacion).not.toHaveBeenCalled();
    expect(messageSpy.showMessage).not.toHaveBeenCalled();
  });

  it('should save the publication itself, sending only the birth year, and close returning true', () => {
    resourceSpy.crearPublicacion.and.returnValue(of({ codigoRespuesta: 1, nroPublicacion: 9, mensajeRespuesta: 'Creada' }));
    completarFormularioValido();

    component.publicar();

    const request = resourceSpy.crearPublicacion.calls.mostRecent().args[0];
    expect(request.añoNacimiento).toBe(2022);
    expect(request.fechaNacimiento).toBeUndefined();
    expect(request.condicionAdopcion).toBe('Patio cerrado');
    expect(request.nroRegMunicipal).toBeUndefined();
    expect(messageSpy.notify).toHaveBeenCalledWith('Creada');
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('should block double submit while saving', () => {
    const response$ = new Subject<IAdministrarPublicacionResponse>();
    resourceSpy.crearPublicacion.and.returnValue(response$.asObservable());
    completarFormularioValido();

    component.publicar();
    component.publicar();

    expect(component.guardando()).toBeTrue();
    expect(resourceSpy.crearPublicacion).toHaveBeenCalledTimes(1);

    response$.next({ codigoRespuesta: 1, nroPublicacion: 2 });
    response$.complete();
    expect(component.guardando()).toBeFalse();
  });

  it('should show the backend error inside the dialog and stay open', () => {
    resourceSpy.crearPublicacion.and.returnValue(throwError(() => ({
      error: { mensajeRespuesta: 'La mascota ya tiene una publicación activa' }
    })));
    completarFormularioValido();

    component.publicar();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.form-error-banner').textContent)
      .toContain('La mascota ya tiene una publicación activa');
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
    expect(messageSpy.showMessage).not.toHaveBeenCalled();
  });

  it('should lock identity fields and use the registered pet data when one is selected', () => {
    component.form.controls.nroRegMunicipal.setValue(100004);

    expect(component.mascotaSeleccionada()?.nroRegMunicipal).toBe(100004);
    expect(component.form.controls.nombreMascota.disabled).toBeTrue();
    expect(component.form.controls.nombreMascota.value).toBe('Rocco');
    expect(component.form.controls.sexo.value).toBe('Macho');
    expect(component.form.controls.anioNacimiento.value?.getFullYear()).toBe(2021);

    component.form.controls.nroRegMunicipal.setValue(0);
    expect(component.form.controls.nombreMascota.enabled).toBeTrue();
    expect(component.form.controls.nombreMascota.value).toBe('');
  });

  it('should keep only the year when one is picked in the calendar', () => {
    const picker = jasmine.createSpyObj('MatDatepicker', ['close']);
    component.elegirAnio(new Date(2019, 6, 15), picker);

    const valor = component.form.controls.anioNacimiento.value!;
    expect(valor.getFullYear()).toBe(2019);
    expect(valor.getMonth()).toBe(0);
    expect(picker.close).toHaveBeenCalled();
  });

  it('should reject an invalid photo inline, without opening a message dialog', () => {
    const archivo = new File(['x'], 'doc.pdf', { type: 'application/pdf' });
    const input: HTMLInputElement = document.createElement('input');
    Object.defineProperty(input, 'files', { value: [archivo] });

    component.onFileSelected({ target: input } as unknown as Event);
    fixture.detectChanges();

    expect(component.errorFoto()).toContain('JPG, PNG, WEBP o GIF');
    expect(fixture.nativeElement.textContent).toContain('Solo se admiten imágenes');
    expect(messageSpy.showMessage).not.toHaveBeenCalled();
  });

  it('should ask for confirmation before discarding entered data', () => {
    component.form.controls.nombreMascota.setValue('Luna');
    component.form.markAsDirty();

    confirmSpy.confirm.and.returnValue(of(false));
    component.cancelar();
    expect(confirmSpy.confirm).toHaveBeenCalled();
    expect(dialogRefSpy.close).not.toHaveBeenCalled();

    confirmSpy.confirm.and.returnValue(of(true));
    component.cancelar();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });

  it('should close without asking when nothing was entered', () => {
    component.cancelar();
    expect(confirmSpy.confirm).not.toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });
});
