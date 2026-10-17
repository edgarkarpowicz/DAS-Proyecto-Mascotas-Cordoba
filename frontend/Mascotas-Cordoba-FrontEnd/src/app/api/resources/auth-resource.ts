import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ILoginRequest } from '../models/i-login-request';
import { ILoginResponse } from '../models/i-login-response';

@Injectable({
  providedIn: 'root'
})
export class AuthResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/auth`;

  login(request: ILoginRequest): Observable<ILoginResponse> {
    return this._http.post<ILoginResponse>(`${this._baseUrl}/login`, request);
  }

}
