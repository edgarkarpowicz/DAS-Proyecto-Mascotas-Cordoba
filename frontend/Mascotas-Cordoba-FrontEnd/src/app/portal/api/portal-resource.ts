import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IPortalInfo } from './models/i-portal-info';

@Injectable()
export class PortalResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/publico/portal`;

  getPortal(): Observable<IPortalInfo> {
    return this._http.get<IPortalInfo>(this._baseUrl);
  }

}
