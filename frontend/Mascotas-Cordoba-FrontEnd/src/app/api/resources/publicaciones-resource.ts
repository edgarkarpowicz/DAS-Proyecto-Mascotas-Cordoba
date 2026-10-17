import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IEstadoPublicacion } from '../models/i-estado-publicacion';
import { IPublicacionAdopcion } from '../models/i-publicacion-adopcion';
import { IMascotaDisponible } from '../models/i-mascota-disponible';
import { INuevaPublicacionRequest } from '../models/i-nueva-publicacion-request';
import { ICambioEstadoRequest } from '../models/i-cambio-estado-request';
import { IAdministrarPublicacionResponse } from '../models/i-administrar-publicacion-response';

@Injectable({
  providedIn: 'root'
})
export class PublicacionesResource {

  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = `${environment.apiUrl}/refugios`;

  getEstadosPublicacion(): Observable<IEstadoPublicacion[]> {
    return this._http.get<IEstadoPublicacion[]>(`${this._baseUrl}/publicaciones/estados`);
  }

  getPublicacionesPorRefugio(idRefugio: number): Observable<IPublicacionAdopcion[]> {
    return this._http.get<IPublicacionAdopcion[]>(`${this._baseUrl}/${idRefugio}/publicaciones`);
  }

  getMascotasDisponibles(idRefugio: number): Observable<IMascotaDisponible[]> {
    return this._http.get<IMascotaDisponible[]>(`${this._baseUrl}/${idRefugio}/mascotas-disponibles`);
  }

  crearPublicacion(request: INuevaPublicacionRequest): Observable<IAdministrarPublicacionResponse> {
    return this._http.post<IAdministrarPublicacionResponse>(`${this._baseUrl}/publicaciones`, request);
  }

  actualizarEstado(nroPublicacion: number, request: ICambioEstadoRequest): Observable<IAdministrarPublicacionResponse> {
    return this._http.put<IAdministrarPublicacionResponse>(
      `${this._baseUrl}/publicaciones/${nroPublicacion}/estado`,
      request
    );
  }

}
