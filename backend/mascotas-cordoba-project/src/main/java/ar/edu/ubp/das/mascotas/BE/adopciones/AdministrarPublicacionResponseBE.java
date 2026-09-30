package ar.edu.ubp.das.mascotas.BE.adopciones;

public class AdministrarPublicacionResponseBE {

    private int codigoRespuesta;
    private String mensajeRespuesta;
    private Integer nroPublicacion;

    public AdministrarPublicacionResponseBE() {
    }

    public AdministrarPublicacionResponseBE(int codigoRespuesta, String mensajeRespuesta, Integer nroPublicacion) {
        this.codigoRespuesta = codigoRespuesta;
        this.mensajeRespuesta = mensajeRespuesta;
        this.nroPublicacion = nroPublicacion;
    }

    public static AdministrarPublicacionResponseBE exitoso(String mensaje, Integer nroPublicacion) {
        return new AdministrarPublicacionResponseBE(1, mensaje, nroPublicacion);
    }

    public static AdministrarPublicacionResponseBE actualizado(String mensaje, Integer nroPublicacion) {
        return new AdministrarPublicacionResponseBE(0, mensaje, nroPublicacion);
    }

    public static AdministrarPublicacionResponseBE error(String mensaje) {
        return new AdministrarPublicacionResponseBE(-1, mensaje, null);
    }

    public int getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(int codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }

    public Integer getNroPublicacion() {
        return nroPublicacion;
    }

    public void setNroPublicacion(Integer nroPublicacion) {
        this.nroPublicacion = nroPublicacion;
    }

    @Override
    public String toString() {
        return "AdministrarPublicacionResponseBE{" +
                "codigoRespuesta=" + codigoRespuesta +
                ", mensajeRespuesta='" + mensajeRespuesta + '\'' +
                ", nroPublicacion=" + nroPublicacion +
                '}';
    }
}
