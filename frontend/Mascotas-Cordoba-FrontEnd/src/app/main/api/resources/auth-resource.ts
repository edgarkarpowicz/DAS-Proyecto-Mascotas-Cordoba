import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ILoginRequest } from '../models/i-login-request';
import { ILoginResponse } from '../models/i-login-response';
import { IUsuarioRefugio } from '../models/i-usuario-refugio';

@Injectable({
  providedIn: 'root'
})
export class AuthResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/auth`;

  login(request: ILoginRequest): Observable<ILoginResponse> {
    return this._http.post<ILoginResponse>(`${this._baseUrl}/login`, request);
  }

  me(): Observable<IUsuarioRefugio> {
    return this._http.get<IUsuarioRefugio>(`${this._baseUrl}/me`);
  }

  logout(): Observable<{ codigoRespuesta: number; mensajeRespuesta: string }> {
    return this._http.post<{ codigoRespuesta: number; mensajeRespuesta: string }>(`${this._baseUrl}/logout`, {});
  }

}
