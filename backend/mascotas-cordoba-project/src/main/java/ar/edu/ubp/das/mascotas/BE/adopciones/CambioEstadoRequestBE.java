package ar.edu.ubp.das.mascotas.BE.adopciones;

public class CambioEstadoRequestBE {

    private Integer idRefugio;
    private String nuevoEstado;

    public CambioEstadoRequestBE() {
    }

    public CambioEstadoRequestBE(Integer idRefugio, String nuevoEstado) {
        this.idRefugio = idRefugio;
        this.nuevoEstado = nuevoEstado;
    }

    public Integer getIdRefugio() {
        return idRefugio;
    }

    public void setIdRefugio(Integer idRefugio) {
        this.idRefugio = idRefugio;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
}
