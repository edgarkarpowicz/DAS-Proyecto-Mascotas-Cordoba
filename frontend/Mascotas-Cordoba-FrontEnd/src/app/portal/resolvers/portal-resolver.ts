import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { catchError, of } from 'rxjs';
import { PortalResource } from '../api/portal-resource';
import { IPortalInfo } from '../api/models/i-portal-info';

export const portalResolver: ResolveFn<IPortalInfo | null> = () => {
  return inject(PortalResource).getPortal().pipe(
    catchError(() => of(null))
  );
};
