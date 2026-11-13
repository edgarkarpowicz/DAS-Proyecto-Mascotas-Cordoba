import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, finalize, takeUntil, throwError } from 'rxjs';
import { LoaderService } from '../services/loader-service';
import { HttpCancelService } from '../services/http-cancel-service';
import { AuthSessionService } from '../services/auth-session.service';
import { environment } from '../../../environments/environment';

export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const cancel = inject(HttpCancelService);
  const loader = inject(LoaderService);
  const session = inject(AuthSessionService);
  const router = inject(Router);

  loader.start();

  // Inyectar API Key técnica y, si hay sesión activa fuera de /auth/login, el token Bearer
  const headersConfig: Record<string, string> = {
    'X-API-Key': environment.apiKey
  };

  const isLoginRequest = req.url.endsWith('/auth/login');
  const token = session.token();
  if (token && !isLoginRequest) {
    headersConfig['Authorization'] = `Bearer ${token}`;
  }

  const clonedReq = req.clone({
    setHeaders: headersConfig
  });

  return next(clonedReq).pipe(
    takeUntil(cancel.pendingHTTPRequests$),
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && !isLoginRequest) {
        session.clearSession();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    }),
    finalize(() => loader.complete())
  );
};
