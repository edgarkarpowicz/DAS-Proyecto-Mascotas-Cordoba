import { Injectable, computed, signal } from '@angular/core';
import { IUsuarioRefugio, PerfilUsuario } from '../api/models/i-usuario-refugio';

const STORAGE_USER_KEY = 'mascotas_cba_user';
const STORAGE_TOKEN_KEY = 'mascotas_cba_token';

interface StoredSessionState {
  usuario: IUsuarioRefugio | null;
  token: string | null;
}

/**
 * Estado reactivo de la sesión autenticada sin dependencias HTTP
 * para evitar ciclos de inyección con AuthService y appHttpInterceptor.
 * La sesión vive en sessionStorage: se mantiene al recargar la pestaña
 * y se pierde al cerrarla.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthSessionService {

  private readonly _initialState = this.loadInitialState();

  private readonly _usuario = signal<IUsuarioRefugio | null>(this._initialState.usuario);
  private readonly _token = signal<string | null>(this._initialState.token);

  readonly usuario = computed(() => this._usuario());
  readonly token = computed(() => this._token());
  readonly perfil = computed(() => this._usuario()?.perfil?.toUpperCase() ?? null);
  readonly isAuthenticated = computed(() => {
    const token = this._token();
    return (
      this._usuario() !== null &&
      token !== null &&
      token.trim().length > 0 &&
      !this.isTokenExpired(token)
    );
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
    this.removeFromStorage();
    const storage = this.getStorage();
    if (!cleanToken || !storage) {
      return;
    }
    try {
      storage.setItem(STORAGE_USER_KEY, JSON.stringify(user));
      storage.setItem(STORAGE_TOKEN_KEY, cleanToken);
    } catch (e) {
      console.warn('No se pudo guardar la sesión en storage:', e);
    }
  }

  updateUsuario(user: IUsuarioRefugio): void {
    this._usuario.set(user);
    const storage = this.getStorage();
    if (!storage) {
      return;
    }
    try {
      storage.setItem(STORAGE_USER_KEY, JSON.stringify(user));
    } catch (e) {
      console.warn('No se pudo actualizar el usuario en storage:', e);
    }
  }

  clearSession(): void {
    this._usuario.set(null);
    this._token.set(null);
    this.removeFromStorage();
  }

  private removeFromStorage(): void {
    const storage = this.getStorage();
    if (!storage) {
      return;
    }
    try {
      storage.removeItem(STORAGE_USER_KEY);
      storage.removeItem(STORAGE_TOKEN_KEY);
    } catch (e) {
      console.warn('Error al limpiar la sesión en storage:', e);
    }
  }

  private loadInitialState(): StoredSessionState {
    const vacio: StoredSessionState = { usuario: null, token: null };
    const storage = this.getStorage();
    if (!storage) {
      return vacio;
    }
    try {
      const rawToken = storage.getItem(STORAGE_TOKEN_KEY);
      const token = rawToken && rawToken.trim().length > 0 ? rawToken.trim() : null;
      if (!token) {
        return vacio;
      }
      if (this.isTokenExpired(token)) {
        storage.removeItem(STORAGE_USER_KEY);
        storage.removeItem(STORAGE_TOKEN_KEY);
        return vacio;
      }
      const rawUser = storage.getItem(STORAGE_USER_KEY);
      let usuario: IUsuarioRefugio | null = null;
      if (rawUser) {
        try {
          usuario = JSON.parse(rawUser) as IUsuarioRefugio;
        } catch {
          usuario = null;
        }
      }
      return { usuario, token };
    } catch {
      return vacio;
    }
  }

  private isTokenExpired(token: string): boolean {
    const parts = token.split('.');
    if (parts.length !== 3) {
      return false;
    }
    try {
      const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
      const binary = atob(padded);
      const bytes = Uint8Array.from(binary, c => c.charCodeAt(0));
      const json = new TextDecoder().decode(bytes);
      const payload = JSON.parse(json) as { exp?: unknown };
      return typeof payload.exp === 'number' && payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }

  private getStorage(): Storage | null {
    return typeof window === 'undefined' ? null : window.sessionStorage;
  }

}
