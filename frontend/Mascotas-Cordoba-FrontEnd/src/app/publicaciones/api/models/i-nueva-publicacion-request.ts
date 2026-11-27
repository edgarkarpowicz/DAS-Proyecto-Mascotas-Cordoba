export interface INuevaPublicacionRequest {
  idRefugio: number;
  nroRegMunicipal?: number;
  nombreMascota?: string;
  sexo?: string;
  añoNacimiento?: number;
  especie?: string;
  raza?: string;
  fechaPublicacion?: string;
  caracteristicasMascota: string;
  condicionAdopcion?: string;
  condicionSanitaria?: string;
  foto?: string;
  estadoPublicacion?: string;
}
