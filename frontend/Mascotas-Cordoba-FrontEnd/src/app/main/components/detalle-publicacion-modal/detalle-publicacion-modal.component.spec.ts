import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetallePublicacionModalComponent } from './detalle-publicacion-modal.component';
import { IPublicacionAdopcion } from '../../api/models/i-publicacion-adopcion';

describe('DetallePublicacionModalComponent', () => {
  let component: DetallePublicacionModalComponent;
  let fixture: ComponentFixture<DetallePublicacionModalComponent>;

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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetallePublicacionModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(DetallePublicacionModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('publicacion', samplePub);
    fixture.detectChanges();
  });

  it('should render photograph and all required pet and adoption details', () => {
    const el: HTMLElement = fixture.nativeElement;
    const img: HTMLImageElement | null = el.querySelector('.detail-hero-img');

    expect(img).toBeTruthy();
    expect(img!.src).toContain('/uploads/publicaciones/luna.jpg');
    expect(el.textContent).toContain('Luna');
    expect(el.textContent).toContain('Perro');
    expect(el.textContent).toContain('Hembra');
    expect(el.textContent).toContain('4 años');
    expect(el.textContent).toContain('Border Collie');
    expect(el.textContent).toContain('Perra dócil, castrada y con vacunas al día');
    expect(el.textContent).toContain('Patio cerrado y seguimiento veterinario');
    expect(el.textContent).toContain('15/10/2026');
  });

  it('should emit close on Escape key and backdrop click', () => {
    const closeSpy = spyOn(component.close, 'emit');

    component.onEscapeKey();
    expect(closeSpy).toHaveBeenCalledTimes(1);

    const backdrop = fixture.nativeElement.querySelector('.detail-backdrop');
    backdrop.dispatchEvent(new MouseEvent('click'));
    expect(closeSpy).toHaveBeenCalledTimes(2);
  });

  it('should emit toggleEstado and finalizar actions cleanly', () => {
    const toggleSpy = spyOn(component.toggleEstado, 'emit');
    const finalizarSpy = spyOn(component.finalizar, 'emit');

    fixture.nativeElement.querySelector('.btn-outline-warn').click();
    expect(toggleSpy).toHaveBeenCalledWith(samplePub);

    fixture.nativeElement.querySelector('.btn-outline-danger').click();
    expect(finalizarSpy).toHaveBeenCalledWith(samplePub);
  });

  it('should open image lightbox from bottom-right expand button and Escape closes only the lightbox first', () => {
    const closeSpy = spyOn(component.close, 'emit');
    const expandBtn: HTMLButtonElement | null = fixture.nativeElement.querySelector('.detail-expand-btn .expand-image-btn');

    expect(expandBtn).toBeTruthy();
    expandBtn!.click();
    fixture.detectChanges();

    expect(component.showImageLightbox()).toBeTrue();
    const lightboxImg: HTMLImageElement | null = fixture.nativeElement.querySelector('.lightbox-image');
    expect(lightboxImg).toBeTruthy();
    expect(lightboxImg!.src).toContain('/uploads/publicaciones/luna.jpg');

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    fixture.detectChanges();

    expect(component.showImageLightbox()).toBeFalse();
    expect(closeSpy).not.toHaveBeenCalled();

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    expect(closeSpy).toHaveBeenCalledTimes(1);
  });

  it('should render formatted birth date and month age when fechaNacimiento is present', () => {
    const today = new Date();
    const birthDate = new Date(today.getFullYear(), today.getMonth() - 5, 1);
    const yyyy = birthDate.getFullYear();
    const mm = String(birthDate.getMonth() + 1).padStart(2, '0');
    const dd = String(birthDate.getDate()).padStart(2, '0');
    const isoDate = `${yyyy}-${mm}-${dd}`;

    fixture.componentRef.setInput('publicacion', {
      ...samplePub,
      añoNacimiento: yyyy,
      edadAproximada: 0,
      fechaNacimiento: isoDate
    });
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('5 meses');
    expect(el.textContent).toContain(`(nac. ${dd}/${mm}/${yyyy})`);
  });
});

