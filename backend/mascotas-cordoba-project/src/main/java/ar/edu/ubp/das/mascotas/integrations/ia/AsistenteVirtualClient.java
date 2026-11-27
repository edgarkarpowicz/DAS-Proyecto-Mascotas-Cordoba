package ar.edu.ubp.das.mascotas.integrations.ia;

/**
 * Puerto de integración con el servicio externo de IA generativa (RF21).
 */
public interface AsistenteVirtualClient {

    RespuestaAsistenteExternaBE generarRespuestaAsistente(ConsultaAsistenteExternaBE request);
}
