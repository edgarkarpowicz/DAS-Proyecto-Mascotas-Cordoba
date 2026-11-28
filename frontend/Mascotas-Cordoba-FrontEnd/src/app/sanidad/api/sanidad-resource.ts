import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  IAtencionSanitaria,
  ICarnetSanitario,
  IRegistrarAtencionRequest,
  ITipoAtencion,
  IValidacionCarnet
} from './models/i-sanidad';
import { IPageResponse, toPageHttpParams } from '../../shared/models/pagination';
import { IMascota } from '../../mascotas/api/models/i-mascota';

export interface IVeterinariaOption {
  idVeterinaria: number;
  nombre: string;
}

@Injectable()
export class SanidadResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/sanidad`;
  private readonly _publicCarnetsUrl = `${environment.apiUrl}/publico/carnets`;
  private readonly _catalogosUrl = `${environment.apiUrl}/publico/catalogos`;

  getTiposAtencion(): Observable<ITipoAtencion[]> {
    return this._http.get<ITipoAtencion[]>(`${this._catalogosUrl}/tipos-atencion`);
  }

  getVeterinariasActivas(): Observable<IVeterinariaOption[]> {
    return this._http
      .get<IPageResponse<IVeterinariaOption>>(`${environment.apiUrl}/publico/veterinarias`, {
        params: toPageHttpParams({ pagina: 1, tamanioPagina: 50 })
      })
      .pipe(map(res => res.items ?? []));
  }

  getMisMascotasResumen(): Observable<IMascota[]> {
    return this._http
      .get<IPageResponse<IMascota>>(`${environment.apiUrl}/mascotas/mias`, {
        params: toPageHttpParams({ pagina: 1, tamanioPagina: 50 })
      })
      .pipe(map(res => res.items ?? []));
  }

  consultarAtenciones(nroRegMunicipal: number): Observable<IAtencionSanitaria[]> {
    return this._http.get<IAtencionSanitaria[]>(`${this._baseUrl}/mascotas/${nroRegMunicipal}/atenciones`);
  }

  registrarAtencion(request: IRegistrarAtencionRequest): Observable<IAtencionSanitaria> {
    return this._http.post<IAtencionSanitaria>(`${this._baseUrl}/atenciones`, request);
  }

  generarCarnet(nroRegMunicipal: number): Observable<ICarnetSanitario> {
    return this._http.post<ICarnetSanitario>(`${this._baseUrl}/carnets/${nroRegMunicipal}`, {});
  }

  consultarCarnet(nroRegMunicipal: number): Observable<ICarnetSanitario> {
    return this._http.get<ICarnetSanitario>(`${this._baseUrl}/carnets/${nroRegMunicipal}`);
  }

  validarCarnet(codigoValidacion: string): Observable<IValidacionCarnet> {
    return this._http.get<IValidacionCarnet>(`${this._publicCarnetsUrl}/${encodeURIComponent(codigoValidacion)}/validar`);
  }

}
