export interface IRefugioPublico {
  idRefugio: number;
  nombre: string;
  domicilio: string;
  telefono: string;
  correo: string;
  horarios?: string;
  publicacionesActivas: number;
}
