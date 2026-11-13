import { APP_INITIALIZER, ApplicationConfig, ErrorHandler, inject } from '@angular/core';
import { provideRouter, Router, withComponentInputBinding, withNavigationErrorHandler } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { routes } from './app.routes';
import { appHttpInterceptor } from './core/interceptors/app-http-interceptor';
import { AppErrorHandler } from './core/handlers/app-error-handler';
import { AuthService } from './core/services/auth.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes,
      withComponentInputBinding(),
      withNavigationErrorHandler(() => {
        const router = inject(Router);
        if (!router.navigated) {
          queueMicrotask(() => {
            router.navigate(['/login']);
          });
        }
      })
    ),
    provideHttpClient(
      withInterceptors([appHttpInterceptor])
    ),
    {
      provide: APP_INITIALIZER,
      multi: true,
      useFactory: () => {
        const authService = inject(AuthService);
        return () => firstValueFrom(authService.restoreSession());
      }
    },
    { provide: ErrorHandler, useClass: AppErrorHandler }
  ]
};

