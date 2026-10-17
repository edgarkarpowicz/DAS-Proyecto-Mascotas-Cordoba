import { ApplicationConfig, ErrorHandler } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { appHttpInterceptor } from './interceptors/app-http-interceptor';
import { AppErrorHandler } from './handlers/app-error-handler';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes,
      withComponentInputBinding()
    ),
    provideHttpClient(
      withInterceptors([appHttpInterceptor])
    ),
    { provide: ErrorHandler, useClass: AppErrorHandler }
  ]
};

