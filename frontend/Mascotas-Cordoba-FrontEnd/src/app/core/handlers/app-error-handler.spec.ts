import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { AppErrorHandler } from './app-error-handler';
import { AppMessageService } from '../services/app-message-service';

describe('AppErrorHandler', () => {
  let handler: AppErrorHandler;
  let messageServiceSpy: jasmine.SpyObj<AppMessageService>;

  beforeEach(() => {
    messageServiceSpy = jasmine.createSpyObj('AppMessageService', ['showMessage']);

    TestBed.configureTestingModule({
      providers: [
        AppErrorHandler,
        { provide: AppMessageService, useValue: messageServiceSpy }
      ]
    });

    handler = TestBed.inject(AppErrorHandler);
  });

  it('should unwrap promise rejections from resolvers and show connection message on status 0', () => {
    const httpError = new HttpErrorResponse({ status: 0, statusText: 'Unknown Error' });
    handler.handleError({ rejection: httpError });

    expect(messageServiceSpy.showMessage).toHaveBeenCalledWith(
      jasmine.objectContaining({
        title: 'Error de Comunicación',
        num: 0,
        type: 'error'
      })
    );
  });

  it('should extract mensajeRespuesta from backend HttpErrorResponse', () => {
    const httpError = new HttpErrorResponse({
      status: 400,
      error: { mensajeRespuesta: 'Estado no válido' }
    });
    handler.handleError(httpError);

    expect(messageServiceSpy.showMessage).toHaveBeenCalledWith(
      jasmine.objectContaining({
        title: 'Error en la Operación',
        text: 'Estado no válido',
        num: 400,
        type: 'error'
      })
    );
  });

  it('should show warning dialog on HTTP 403 Forbidden', () => {
    const httpError = new HttpErrorResponse({
      status: 403,
      error: { mensajeRespuesta: 'Perfil CIUDADANO no autorizado' }
    });
    handler.handleError(httpError);

    expect(messageServiceSpy.showMessage).toHaveBeenCalledWith(
      jasmine.objectContaining({
        title: 'Operación No Permitida',
        text: 'Perfil CIUDADANO no autorizado',
        num: 403,
        type: 'warning'
      })
    );
  });
});

