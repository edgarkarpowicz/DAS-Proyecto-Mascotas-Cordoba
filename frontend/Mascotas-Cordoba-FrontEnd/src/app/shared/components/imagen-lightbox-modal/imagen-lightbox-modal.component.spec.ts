import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ImagenLightboxModalComponent } from './imagen-lightbox-modal.component';

describe('ImagenLightboxModalComponent', () => {
  let component: ImagenLightboxModalComponent;
  let fixture: ComponentFixture<ImagenLightboxModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImagenLightboxModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ImagenLightboxModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('imageUrl', 'http://localhost:8080/uploads/publicaciones/churchill.jpg');
    fixture.componentRef.setInput('altText', 'Churchill');
    fixture.componentRef.setInput('fallbackUrl', 'https://example.com/fallback.jpg');
    fixture.componentRef.setInput('caption', 'Churchill · Perro');
    fixture.detectChanges();
  });

  it('should render full uncropped image and caption inside modal dialog', () => {
    const el: HTMLElement = fixture.nativeElement;
    const img: HTMLImageElement | null = el.querySelector('.lightbox-image');

    expect(img).toBeTruthy();
    expect(img!.src).toContain('/uploads/publicaciones/churchill.jpg');
    expect(el.querySelector('.lightbox-caption')?.textContent).toContain('Churchill · Perro');
  });

  it('should emit close on close button, backdrop click, and Escape key without closing on image click', () => {
    const closeSpy = spyOn(component.close, 'emit');

    const img: HTMLElement = fixture.nativeElement.querySelector('.lightbox-image');
    img.click();
    expect(closeSpy).not.toHaveBeenCalled();

    const closeBtn: HTMLButtonElement = fixture.nativeElement.querySelector('.lightbox-close-btn');
    closeBtn.click();
    expect(closeSpy).toHaveBeenCalledTimes(1);

    const backdrop: HTMLElement = fixture.nativeElement.querySelector('.lightbox-backdrop');
    backdrop.dispatchEvent(new MouseEvent('click'));
    expect(closeSpy).toHaveBeenCalledTimes(2);

    component.onEscapeKey();
    expect(closeSpy).toHaveBeenCalledTimes(3);
  });

  it('should apply fallback image url when image load fails', () => {
    const img: HTMLImageElement = fixture.nativeElement.querySelector('.lightbox-image');
    img.dispatchEvent(new Event('error'));
    expect(img.src).toBe('https://example.com/fallback.jpg');
  });
});

