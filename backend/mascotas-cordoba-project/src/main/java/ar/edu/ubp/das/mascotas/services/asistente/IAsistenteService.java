package ar.edu.ubp.das.mascotas.services.asistente;

import ar.edu.ubp.das.mascotas.BE.asistente.ConsultaAsistenteRequestBE;
import ar.edu.ubp.das.mascotas.BE.asistente.RespuestaAsistenteBE;

/**
 * Contrato de negocio del Asistente Virtual con RAG e IA generativa (RF21).
 */
public interface IAsistenteService {

    RespuestaAsistenteBE consultar(ConsultaAsistenteRequestBE request);
}
