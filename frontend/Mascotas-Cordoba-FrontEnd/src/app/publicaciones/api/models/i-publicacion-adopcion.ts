export interface IPublicacionAdopcion {
  nroPublicacion: number;
  nroRegMunicipal: number;
  idRefugio: number;
  fechaPublicacion?: string;
  caracteristicasMascota: string;
  condicionAdopcion: string;
  foto?: string;
  estadoPublicacion: string;
  descripcionEstado?: string;
  nombreMascota: string;
  sexo: string;
  añoNacimiento?: number;
  edadAproximada?: number;
  especie?: string;
  raza?: string;
}
