import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PublicacionesResource } from './publicaciones-resource';
import { environment } from '../../../../environments/environment';

describe('PublicacionesResource', () => {
  let resource: PublicacionesResource;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/refugios`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PublicacionesResource,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    resource = TestBed.inject(PublicacionesResource);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch estados from /refugios/publicaciones/estados', () => {
    resource.getEstadosPublicacion().subscribe(res => {
      expect(res.length).toBe(1);
    });

    const req = httpMock.expectOne(`${baseUrl}/publicaciones/estados`);
    expect(req.request.method).toBe('GET');
    req.flush([{ codEstado: 'Activa', descripcion: 'Activa' }]);
  });

  it('should fetch especies from /refugios/especies', () => {
    resource.getEspecies().subscribe(res => {
      expect(res[0].nomEspecie).toBe('Perro');
    });

    const req = httpMock.expectOne(`${baseUrl}/especies`);
    expect(req.request.method).toBe('GET');
    req.flush([{ nroValorDominio: 1, nomEspecie: 'Perro' }]);
  });

  it('should fetch publicaciones by refugio id', () => {
    resource.getPublicacionesPorRefugio(2).subscribe();

    const req = httpMock.expectOne(`${baseUrl}/2/publicaciones`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should PUT estado update', () => {
    const payload = { idRefugio: 1, nuevoEstado: 'Pausada' };
    resource.actualizarEstado(10, payload).subscribe();

    const req = httpMock.expectOne(`${baseUrl}/publicaciones/10/estado`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(payload);
    req.flush({ codigoRespuesta: 0, mensajeRespuesta: 'OK' });
  });
});

