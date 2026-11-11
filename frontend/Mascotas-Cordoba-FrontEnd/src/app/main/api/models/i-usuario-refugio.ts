export interface IUsuarioRefugio {
  idCiudadano: number;
  apellido: string;
  nombre: string;
  cuil: string;
  correo: string;
  perfil: string;
  idRefugio: number;
  nombreRefugio: string;
  razonSocialRefugio?: string;
  habilitacionMunicipal?: string;
}
