import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ILoteSincronizacion,
  IResultadoSincronizacionVeterinaria,
  IVeterinariaConfiguracion
} from './models/i-integracion';

@Injectable()
export class IntegracionesResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/integraciones/veterinarias`;

  listarClinicas(): Observable<IVeterinariaConfiguracion[]> {
    return this._http.get<IVeterinariaConfiguracion[]>(this._baseUrl);
  }

  listarLotes(top = 20): Observable<ILoteSincronizacion[]> {
    return this._http.get<ILoteSincronizacion[]>(`${this._baseUrl}/lotes`, {
      params: new HttpParams().set('top', top)
    });
  }

  sincronizar(fechaDesde?: string, fechaHasta?: string, idVeterinaria?: number): Observable<IResultadoSincronizacionVeterinaria[]> {
    let params = new HttpParams();
    if (fechaDesde) {
      params = params.set('fechaDesde', fechaDesde);
    }
    if (fechaHasta) {
      params = params.set('fechaHasta', fechaHasta);
    }
    if (idVeterinaria != null) {
      params = params.set('idVeterinaria', idVeterinaria);
    }
    return this._http.post<IResultadoSincronizacionVeterinaria[]>(`${this._baseUrl}/sincronizar`, {}, { params });
  }
}
