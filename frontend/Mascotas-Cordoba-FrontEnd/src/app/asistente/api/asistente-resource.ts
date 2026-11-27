import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SKIP_GLOBAL_LOADER } from '../../core/http/http-context-tokens';
import { IConsultaAsistenteRequest, IRespuestaAsistente } from './models/i-asistente';

@Injectable()
export class AsistenteResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/publico/asistente`;

  consultar(request: IConsultaAsistenteRequest): Observable<IRespuestaAsistente> {
    return this._http.post<IRespuestaAsistente>(`${this._baseUrl}/consultar`, request, {
      context: new HttpContext().set(SKIP_GLOBAL_LOADER, true)
    });
  }

}
