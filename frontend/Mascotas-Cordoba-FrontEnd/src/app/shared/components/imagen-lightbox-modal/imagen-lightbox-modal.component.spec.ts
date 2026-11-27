import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { IImagenLightboxData, ImagenLightboxModalComponent } from './imagen-lightbox-modal.component';

describe('ImagenLightboxModalComponent', () => {
  let fixture: ComponentFixture<ImagenLightboxModalComponent>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<ImagenLightboxModalComponent>>;

  const data: IImagenLightboxData = {
    imageUrl: 'http://localhost:8080/uploads/publicaciones/churchill.jpg',
    altText: 'Churchill',
    fallbackUrl: 'https://example.com/fallback.jpg',
    caption: 'Churchill · Perro'
  };

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
    await TestBed.configureTestingModule({
      imports: [ImagenLightboxModalComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: data }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ImagenLightboxModalComponent);
    fixture.detectChanges();
  });

  it('should render the full image and caption received as dialog data', () => {
    const img: HTMLImageElement = fixture.nativeElement.querySelector('.lightbox-image');
    expect(img.src).toContain('/uploads/publicaciones/churchill.jpg');
    expect(img.alt).toBe('Churchill');
    expect(fixture.nativeElement.querySelector('.lightbox-caption').textContent).toContain('Churchill · Perro');
  });

  it('should use the fallback image when the photo fails to load', () => {
    const img: HTMLImageElement = fixture.nativeElement.querySelector('.lightbox-image');
    img.dispatchEvent(new Event('error'));
    expect(img.src).toBe('https://example.com/fallback.jpg');
  });

  it('should close its own dialog reference from the close button', () => {
    fixture.nativeElement.querySelector('.lightbox-close-btn').click();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should open itself through MatDialog with the lightbox panel', () => {
    const dialogSpy = jasmine.createSpyObj<MatDialog>('MatDialog', ['open']);
    ImagenLightboxModalComponent.abrir(dialogSpy, data);
    expect(dialogSpy.open).toHaveBeenCalledWith(ImagenLightboxModalComponent, jasmine.objectContaining({
      data,
      panelClass: 'dialogo-lightbox'
    }));
  });
});
