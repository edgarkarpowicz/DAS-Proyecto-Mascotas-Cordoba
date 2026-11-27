import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IPageRequest, IPageResponse, toPageHttpParams } from '../../shared/models/pagination';
import { IVeterinariaPublica } from './models/i-veterinaria-publica';

@Injectable()
export class VeterinariasResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/publico/veterinarias`;

  getVeterinarias(filtro?: string, pageRequest?: IPageRequest): Observable<IPageResponse<IVeterinariaPublica>> {
    let params = toPageHttpParams(pageRequest);
    if (filtro?.trim()) {
      params = params.set('filtro', filtro.trim());
    }
    return this._http.get<IPageResponse<IVeterinariaPublica>>(this._baseUrl, { params });
  }

}
