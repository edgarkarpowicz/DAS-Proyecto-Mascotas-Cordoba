export interface IMascota {
  nroRegMunicipal: number;
  nombre: string;
  sexo: string;
  añoNacimiento: number;
  especie: string;
  raza?: string;
  microchip?: string;
  castrado?: boolean;
  vive: boolean;
  idResponsable?: number;
  idRefugio?: number;
}

export interface IRegistrarMascotaRequest {
  nombre: string;
  sexo: string;
  añoNacimiento: number;
  especie: string;
  raza?: string;
  microchip?: string;
  castrado?: boolean;
  cuilResponsable?: string;
  idRefugio?: number;
}
