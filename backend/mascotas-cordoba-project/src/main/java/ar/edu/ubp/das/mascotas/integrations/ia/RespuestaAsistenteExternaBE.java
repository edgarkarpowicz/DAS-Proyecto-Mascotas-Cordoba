package ar.edu.ubp.das.mascotas.integrations.ia;

/**
 * Respuesta del servicio externo de IA generativa para el asistente virtual (RF21 renovado10).
 */
public class RespuestaAsistenteExternaBE {

    private int codigoResultado;
    private String respuesta;
    private String mensajeError;

    public int getCodigoResultado() {
        return codigoResultado;
    }

    public void setCodigoResultado(int codigoResultado) {
        this.codigoResultado = codigoResultado;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}
