import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, map, of, tap } from 'rxjs';
import { AuthResource } from '../api/auth-resource';
import { PerfilUsuario } from '../api/models/i-usuario';
import { ILoginResponse } from '../api/models/i-login-response';
import { AuthSessionService } from './auth-session.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _authResource = inject(AuthResource);
  private readonly _router = inject(Router);
  private readonly _session = inject(AuthSessionService);

  readonly usuario = this._session.usuario;
  readonly token = this._session.token;
  readonly perfil = this._session.perfil;
  readonly isAuthenticated = this._session.isAuthenticated;
  readonly idRefugio = this._session.idRefugio;
  readonly idVeterinaria = this._session.idVeterinaria;

  hasPerfil(...perfiles: Array<PerfilUsuario | string>): boolean {
    return this._session.hasPerfil(...perfiles);
  }

  login(cuil: string, clave: string): Observable<ILoginResponse> {
    return this._authResource.login({ cuil: cuil.trim(), clave: clave.trim() }).pipe(
      tap(response => {
        if (response && response.codigoRespuesta === 1 && response.usuario && response.token) {
          this._session.setSession(response.usuario, response.token);
        }
      })
    );
  }

  restoreSession(): Observable<void> {
    if (!this._session.token()) {
      return of(void 0);
    }

    return this._authResource.me().pipe(
      tap(usuario => {
        if (usuario) {
          this._session.updateUsuario(usuario);
        }
      }),
      map(() => void 0),
      catchError((error: { status?: number }) => {
        if (error?.status === 401 || error?.status === 403) {
          this._session.clearSession();
        }
        return of(void 0);
      })
    );
  }

  logout(redirectTo: string | null = '/'): void {
    const tokenActual = this._session.token();
    if (!tokenActual) {
      this.finalizarLogout(redirectTo);
      return;
    }

    this._authResource.logout().pipe(
      catchError(() => of(null)),
      finalize(() => this.finalizarLogout(redirectTo))
    ).subscribe();
  }

  private finalizarLogout(redirectTo: string | null): void {
    this._session.clearSession();
    if (redirectTo) {
      this._router.navigate([redirectTo]);
    }
  }

}
