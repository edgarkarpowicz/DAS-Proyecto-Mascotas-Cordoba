export interface IAtencionSanitaria {
  nroRegMunicipal: number;
  nroRegistro: number;
  fechaAtencion: string;
  tipoAtencion: string;
  detalleAtencion: string;
  fechaVencimiento?: string;
  idVeterinaria: number;
  profesional?: string;
}

export interface IRegistrarAtencionRequest {
  nroRegMunicipal: number;
  fechaAtencion: string;
  codTipoAtencion: number;
  detalleAtencion: string;
  fechaVencimiento?: string;
  idProfesional?: number;
}

export interface ICarnetSanitario {
  nroRegMunicipal: number;
  codigoValidacion: string;
  nombreMascota: string;
  responsableNombre?: string;
  fechaEmision: string;
  vigente: boolean;
  atenciones: IAtencionSanitaria[];
}

export interface IValidacionCarnet {
  autentico: boolean;
  vigente: boolean;
  nroRegMunicipal?: number;
  mensaje: string;
}
