import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  IAtencionSanitaria,
  ICarnetSanitario,
  IRegistrarAtencionRequest,
  IValidacionCarnet
} from './models/i-sanidad';

@Injectable()
export class SanidadResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/sanidad`;
  private readonly _publicUrl = `${environment.apiUrl}/publico/carnets`;

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
    return this._http.get<IValidacionCarnet>(`${this._publicUrl}/${encodeURIComponent(codigoValidacion)}/validar`);
  }

}
