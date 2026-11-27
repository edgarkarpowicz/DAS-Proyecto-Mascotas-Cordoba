package ar.edu.ubp.das.mascotas.BE.asistente;

/**
 * Respuesta devuelta al cliente web por el asistente virtual (RF21).
 */
public class RespuestaAsistenteBE {

    private String respuesta;
    private boolean personalizada;
    private String aviso;

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public boolean isPersonalizada() {
        return personalizada;
    }

    public void setPersonalizada(boolean personalizada) {
        this.personalizada = personalizada;
    }

    public String getAviso() {
        return aviso;
    }

    public void setAviso(String aviso) {
        this.aviso = aviso;
    }
}
