import { Injectable, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private _usuario = signal<Usuario | null>({
    cuil: '27-12345678-9',
    nombre: 'Juan',
    apellido: 'Pérez',
    perfil: 'Refugio',
    idRefugio: 1,
    nombreRefugio: 'Refugio Huellitas Felices'
  });

  readonly usuario = computed(() => this._usuario());
  readonly isAuthenticated = computed(() => this._usuario() !== null);

  constructor(private router: Router) {}

  login(cuil: string, clave: string): boolean {
    if (!cuil || !clave) {
      return false;
    }

    this._usuario.set({
      cuil: cuil.trim(),
      nombre: 'Usuario',
      apellido: 'Refugio',
      perfil: 'Refugio',
      idRefugio: 1,
      nombreRefugio: 'Refugio Municipal'
    });

    this.router.navigate(['/publicaciones']);
    return true;
  }

  logout(): void {
    this._usuario.set(null);
    this.router.navigate(['/login']);
  }

}
