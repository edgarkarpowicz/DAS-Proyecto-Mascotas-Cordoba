import { Injectable, computed, signal } from '@angular/core';
import { IUsuarioRefugio, PerfilUsuario } from '../../main/api/models/i-usuario-refugio';

const STORAGE_USER_KEY = 'mascotas_cba_user';
const STORAGE_TOKEN_KEY = 'mascotas_cba_token';

/**
 * Estado reactivo de la sesión autenticada sin dependencias HTTP
 * para evitar ciclos de inyección con AuthService y appHttpInterceptor.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthSessionService {

  private readonly _usuario = signal<IUsuarioRefugio | null>(this.loadStoredUser());
  private readonly _token = signal<string | null>(this.loadStoredToken());

  readonly usuario = computed(() => this._usuario());
  readonly token = computed(() => this._token());
  readonly perfil = computed(() => this._usuario()?.perfil?.toUpperCase() ?? null);
  readonly isAuthenticated = computed(() => {
    const token = this._token();
    return this._usuario() !== null && token !== null && token.trim().length > 0;
  });
  readonly idRefugio = computed(() => this._usuario()?.idRefugio ?? 1);

  hasPerfil(...perfiles: Array<PerfilUsuario | string>): boolean {
    const currentPerfil = this.perfil();
    if (!currentPerfil || !this.isAuthenticated()) {
      return false;
    }
    return perfiles.some(p => p.toUpperCase() === currentPerfil);
  }

  setSession(user: IUsuarioRefugio, token: string): void {
    const cleanToken = token?.trim() ?? '';
    this._usuario.set(user);
    this._token.set(cleanToken || null);
    try {
      if (typeof window !== 'undefined' && window.sessionStorage) {
        sessionStorage.setItem(STORAGE_USER_KEY, JSON.stringify(user));
        sessionStorage.setItem(STORAGE_TOKEN_KEY, cleanToken);
      }
    } catch (e) {
      console.warn('No se pudo guardar la sesión en storage:', e);
    }
  }

  clearSession(): void {
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
    }
    return null;
  }

  private loadStoredToken(): string | null {
    try {
      if (typeof window !== 'undefined' && window.sessionStorage) {
        const token = sessionStorage.getItem(STORAGE_TOKEN_KEY);
        return token && token.trim().length > 0 ? token.trim() : null;
      }
    } catch {
    }
    return null;
  }

}

