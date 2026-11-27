export type PerfilUsuario = 'CIUDADANO' | 'REFUGIO' | 'VETERINARIA' | 'MUNICIPALIDAD';

export interface IUsuario {
  idCiudadano: number;
  apellido: string;
  nombre: string;
  cuil: string;
  correo: string;
  perfil: PerfilUsuario | string;
  idRefugio?: number;
  nombreRefugio?: string;
  razonSocialRefugio?: string;
  habilitacionMunicipal?: string;
  idVeterinaria?: number;
  nombreVeterinaria?: string;
}
