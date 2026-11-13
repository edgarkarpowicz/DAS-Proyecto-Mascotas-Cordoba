import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, of, tap } from 'rxjs';
import { AuthResource } from '../../main/api/resources/auth-resource';
import { PerfilUsuario } from '../../main/api/models/i-usuario-refugio';
import { ILoginResponse } from '../../main/api/models/i-login-response';
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

  logout(redirectToLogin = true): void {
    const tokenActual = this._session.token();
    if (!tokenActual) {
      this.finalizarLogout(redirectToLogin);
      return;
    }

    this._authResource.logout().pipe(
      catchError(() => of(null)),
      finalize(() => this.finalizarLogout(redirectToLogin))
    ).subscribe();
  }

  private finalizarLogout(redirectToLogin: boolean): void {
    this._session.clearSession();
    if (redirectToLogin) {
      this._router.navigate(['/login']);
    }
  }

}
