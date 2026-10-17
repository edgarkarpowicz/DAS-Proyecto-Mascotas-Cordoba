import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize, takeUntil } from 'rxjs';
import { LoaderService } from '../services/loader-service';
import { HttpCancelService } from '../services/http-cancel-service';
import { environment } from '../../environments/environment';

export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const cancel = inject(HttpCancelService);
  const loader = inject(LoaderService);

  loader.start();

  // Clonar la petición para inyectar la cabecera X-API-Key requerida por el backend
  const headersConfig: Record<string, string> = {
    'X-API-Key': environment.apiKey
  };

  const clonedReq = req.clone({
    setHeaders: headersConfig
  });

  return next(clonedReq).pipe(
    takeUntil(cancel.pendingHTTPRequests$),
    finalize(() => loader.complete())
  );
};
