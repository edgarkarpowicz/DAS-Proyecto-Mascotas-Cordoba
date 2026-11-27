import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IPageRequest, IPageResponse, toPageHttpParams } from '../../shared/models/pagination';
import { IMascota, IRegistrarMascotaRequest } from './models/i-mascota';

@Injectable()
export class MascotasResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/mascotas`;

  registrarMascota(request: IRegistrarMascotaRequest): Observable<IMascota> {
    return this._http.post<IMascota>(this._baseUrl, request);
  }

  getMisMascotas(pageRequest?: IPageRequest): Observable<IPageResponse<IMascota>> {
    return this._http.get<IPageResponse<IMascota>>(`${this._baseUrl}/mias`, {
      params: toPageHttpParams(pageRequest)
    });
  }

}
