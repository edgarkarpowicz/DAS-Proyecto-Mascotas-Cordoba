import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IPublicacionAdopcion } from '../../publicaciones/api/models/i-publicacion-adopcion';
import { IPageRequest, IPageResponse, toPageHttpParams } from '../../shared/models/pagination';
import { IRefugioPublico } from './models/i-refugio-publico';

@Injectable()
export class RefugiosResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/publico/refugios`;

  getRefugios(filtro?: string, pageRequest?: IPageRequest): Observable<IPageResponse<IRefugioPublico>> {
    let params = toPageHttpParams(pageRequest);
    if (filtro?.trim()) {
      params = params.set('filtro', filtro.trim());
    }
    return this._http.get<IPageResponse<IRefugioPublico>>(this._baseUrl, { params });
  }

  getPublicacionesActivas(idRefugio: number): Observable<IPublicacionAdopcion[]> {
    return this._http.get<IPublicacionAdopcion[]>(`${this._baseUrl}/${idRefugio}/publicaciones`);
  }

}
