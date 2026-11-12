import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { of, Observable } from 'rxjs';
import { controlarCambiosGuard, HasChangesComponent } from './controlar-cambios-guard';
import { ConfirmDialogService } from '../../core/services/confirm-dialog.service';

describe('controlarCambiosGuard', () => {
  let confirmSpy: jasmine.SpyObj<ConfirmDialogService>;

  beforeEach(() => {
    confirmSpy = jasmine.createSpyObj('ConfirmDialogService', ['confirm']);

    TestBed.configureTestingModule({
      providers: [
        { provide: ConfirmDialogService, useValue: confirmSpy }
      ]
    });
  });

  it('should allow deactivation directly when component has no changes', () => {
    const component: HasChangesComponent = { hasChanges: () => false };

    const result = TestBed.runInInjectionContext(() =>
      controlarCambiosGuard(
        component,
        {} as ActivatedRouteSnapshot,
        {} as RouterStateSnapshot,
        {} as RouterStateSnapshot
      )
    );

    expect(result).toBeTrue();
    expect(confirmSpy.confirm).not.toHaveBeenCalled();
  });

  it('should invoke ConfirmDialogService when component has unsaved changes', () => {
    confirmSpy.confirm.and.returnValue(of(true));
    const component: HasChangesComponent = { hasChanges: () => true };

    const result = TestBed.runInInjectionContext(() =>
      controlarCambiosGuard(
        component,
        {} as ActivatedRouteSnapshot,
        {} as RouterStateSnapshot,
        {} as RouterStateSnapshot
      )
    ) as Observable<boolean>;

    expect(confirmSpy.confirm).toHaveBeenCalled();
    result.subscribe(allowed => expect(allowed).toBeTrue());
  });
});

