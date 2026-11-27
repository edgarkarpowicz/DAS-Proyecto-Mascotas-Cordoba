import { PerfilUsuario } from '../api/models/i-usuario';

export const HOME_BY_PERFIL: Record<PerfilUsuario, string> = {
  REFUGIO: '/publicaciones',
  CIUDADANO: '/',
  VETERINARIA: '/',
  MUNICIPALIDAD: '/'
};

export function getHomeRouteForPerfil(perfil?: string | null): string {
  const normalizado = perfil?.trim().toUpperCase() as PerfilUsuario | undefined;
  return (normalizado && HOME_BY_PERFIL[normalizado]) || '/';
}
