export interface IVeterinariaConfiguracion {
  idVeterinaria: number;
  nombreVeterinaria?: string;
  urlServicio: string;
  tecnologia: 'REST' | 'SOAP';
  parametros?: string;
  apiKey?: string;
  ultimaFechaProcesada?: string;
}

export interface ILoteSincronizacion {
  idLote: number;
  idVeterinaria: number;
  nombreVeterinaria?: string;
  tipoLote: string;
  fechaDesde: string;
  fechaHasta: string;
  fechaEjecucion: string;
  estado: 'EXITO' | 'ERROR';
  registrosProcesados: number;
  registrosNuevos: number;
  registrosDeduplicados: number;
  registrosConfirmados: number;
  detalleError?: string;
}

export interface IResultadoSincronizacionVeterinaria {
  idVeterinaria: number;
  nombreVeterinaria?: string;
  tecnologia?: string;
  fechaDesde: string;
  fechaHasta: string;
  estado: 'EXITO' | 'ERROR';
  mascotasNuevas: number;
  mascotasDeduplicadas: number;
  nrmsConfirmados: number;
  atencionesProcesadas: number;
  mensajeError?: string;
}
