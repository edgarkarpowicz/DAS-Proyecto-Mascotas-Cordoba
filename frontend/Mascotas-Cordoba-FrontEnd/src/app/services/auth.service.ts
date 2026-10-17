import { Injectable, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResource } from '../api/resources/auth-resource';
import { IUsuarioRefugio } from '../api/models/i-usuario-refugio';
import { ILoginResponse } from '../api/models/i-login-response';

const STORAGE_USER_KEY = 'mascotas_cba_user';
const STORAGE_TOKEN_KEY = 'mascotas_cba_token';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _authResource = inject(AuthResource);
  private readonly _router = inject(Router);

  private _usuario = signal<IUsuarioRefugio | null>(this.loadStoredUser());
  private _token = signal<string | null>(this.loadStoredToken());

  readonly usuario = computed(() => this._usuario());
  readonly token = computed(() => this._token());
  readonly isAuthenticated = computed(() => this._usuario() !== null);
  readonly idRefugio = computed(() => this._usuario()?.idRefugio ?? 1);

  login(cuil: string, clave: string): Observable<ILoginResponse> {
    return this._authResource.login({ cuil: cuil.trim(), clave: clave.trim() }).pipe(
      tap(response => {
        if (response && response.codigoRespuesta === 1 && response.usuario) {
          this.setSession(response.usuario, response.token || '');
        }
      })
    );
  }

  logout(): void {
    this.clearSession();
    this._router.navigate(['/login']);
  }

  private setSession(user: IUsuarioRefugio, token: string): void {
    this._usuario.set(user);
    this._token.set(token);
    try {
      if (typeof window !== 'undefined' && window.sessionStorage) {
        sessionStorage.setItem(STORAGE_USER_KEY, JSON.stringify(user));
        sessionStorage.setItem(STORAGE_TOKEN_KEY, token);
      }
    } catch (e) {
      console.warn('No se pudo guardar la sesión en storage:', e);
    }
  }

  private clearSession(): void {
    this._usuario.set(null);
    this._token.set(null);
    try {
      if (typeof window !== 'undefined' && window.sessionStorage) {
        sessionStorage.removeItem(STORAGE_USER_KEY);
        sessionStorage.removeItem(STORAGE_TOKEN_KEY);
      }
    } catch (e) {
      console.warn('Error al limpiar la sesión en storage:', e);
    }
  }

  private loadStoredUser(): IUsuarioRefugio | null {
    try {
      if (typeof window !== 'undefined' && window.sessionStorage) {
        const item = sessionStorage.getItem(STORAGE_USER_KEY);
        if (item) {
          return JSON.parse(item);
        }
      }
    } catch {
      // Ignorar en SSR o error de parseo
    }
    return null;
  }

  private loadStoredToken(): string | null {
    try {
      if (typeof window !== 'undefined' && window.sessionStorage) {
        return sessionStorage.getItem(STORAGE_TOKEN_KEY);
      }
    } catch {
      // Ignorar en SSR
    }
    return null;
  }

}
