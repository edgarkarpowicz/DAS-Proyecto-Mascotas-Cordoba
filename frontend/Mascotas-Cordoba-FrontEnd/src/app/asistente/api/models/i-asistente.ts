export interface IConsultaAsistenteRequest {
  consulta: string;
  incluirContextoPersonal: boolean;
}

export interface IRespuestaAsistente {
  respuesta: string;
  personalizada: boolean;
  aviso?: string;
}
