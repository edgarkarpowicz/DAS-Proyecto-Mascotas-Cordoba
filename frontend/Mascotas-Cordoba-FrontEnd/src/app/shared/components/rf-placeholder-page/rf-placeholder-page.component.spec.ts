import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { RfPlaceholderPageComponent } from './rf-placeholder-page.component';
import { AuthService } from '../../../core/services/auth.service';

describe('RfPlaceholderPageComponent', () => {
  let fixture: ComponentFixture<RfPlaceholderPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RfPlaceholderPageComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: { usuario: signal(null), logout: () => {} } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RfPlaceholderPageComponent);
    fixture.componentRef.setInput('rfCodigo', 'RF17');
    fixture.componentRef.setInput('titulo', 'Veterinarias adheridas');
    fixture.componentRef.setInput('descripcion', 'Directorio público de clínicas');
    fixture.componentRef.setInput('endpointEsperado', 'GET /publico/veterinarias');
    fixture.detectChanges();
  });

  it('should render RF metadata, expected endpoint, and link back to portal', () => {
    const text = fixture.nativeElement.textContent;
    expect(text).toContain('RF17');
    expect(text).toContain('Veterinarias adheridas');
    expect(text).toContain('GET /publico/veterinarias');
    const backLink: HTMLAnchorElement | null = fixture.nativeElement.querySelector('.volver-link');
    expect(backLink).toBeTruthy();
    expect(backLink?.getAttribute('href')).toBe('/');
  });
});
