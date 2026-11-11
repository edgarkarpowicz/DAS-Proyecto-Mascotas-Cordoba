import { TestBed } from '@angular/core/testing';
import { LoaderService } from './loader-service';

describe('LoaderService', () => {
  let service: LoaderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoaderService);
  });

  it('should start with loading false', () => {
    expect(service.loading()).toBeFalse();
  });

  it('should track concurrent requests accurately', () => {
    service.start();
    service.start();
    expect(service.loading()).toBeTrue();

    service.complete();
    expect(service.loading()).toBeTrue();

    service.complete();
    expect(service.loading()).toBeFalse();

    service.complete();
    expect(service.loading()).toBeFalse();
  });
});

