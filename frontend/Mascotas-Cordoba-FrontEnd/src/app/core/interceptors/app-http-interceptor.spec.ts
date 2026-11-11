import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { appHttpInterceptor } from './app-http-interceptor';
import { LoaderService } from '../services/loader-service';
import { environment } from '../../../environments/environment';

describe('appHttpInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let loaderService: LoaderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([appHttpInterceptor])),
        provideHttpClientTesting()
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    loaderService = TestBed.inject(LoaderService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should inject X-API-Key header and toggle loader on success', () => {
    expect(loaderService.loading()).toBeFalse();

    http.get('/test').subscribe();

    expect(loaderService.loading()).toBeTrue();
    const req = httpMock.expectOne('/test');
    expect(req.request.headers.get('X-API-Key')).toBe(environment.apiKey);

    req.flush({ ok: true });
    expect(loaderService.loading()).toBeFalse();
  });

  it('should turn off loader when HTTP request fails', () => {
    http.get('/fail').subscribe({
      error: () => {
        // esperado
      }
    });

    expect(loaderService.loading()).toBeTrue();
    const req = httpMock.expectOne('/fail');
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });

    expect(loaderService.loading()).toBeFalse();
  });
});
