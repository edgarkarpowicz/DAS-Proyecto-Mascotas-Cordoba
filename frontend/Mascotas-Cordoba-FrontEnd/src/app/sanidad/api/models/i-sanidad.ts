export interface ITipoAtencion {
  codTipoAtencion: number;
  descTipoAtencion: string;
}

export interface IAtencionSanitaria {
  nroRegMunicipal: number;
  nroRegistro: number;
  fechaAtencion: string;
  tipoAtencion: string;
  detalleAtencion: string;
  fechaVencimiento?: string;
  idVeterinaria: number;
  veterinaria?: string;
  profesional?: string;
}

export interface IRegistrarAtencionRequest {
  nroRegMunicipal: number;
  fechaAtencion: string;
  codTipoAtencion: number;
  detalleAtencion: string;
  fechaVencimiento?: string;
  idVeterinaria?: number;
  idProfesional?: number;
  profesional?: string;
}

export interface ICarnetSanitario {
  nroRegMunicipal: number;
  codigoValidacion: string;
  nombreMascota: string;
  especie?: string;
  raza?: string;
  castrado?: boolean;
  microchip?: string;
  vive?: boolean;
  responsableNombre?: string;
  fechaEmision: string;
  vigente: boolean;
  estadoSanitario?: string;
  atenciones: IAtencionSanitaria[];
}

export interface IValidacionCarnet {
  autentico: boolean;
  vigente: boolean;
  nroRegMunicipal?: number;
  codigoValidacion?: string;
  nombreMascota?: string;
  especie?: string;
  responsableNombre?: string;
  fechaEmision?: string;
  estadoSanitario?: string;
  mensaje: string;
}
