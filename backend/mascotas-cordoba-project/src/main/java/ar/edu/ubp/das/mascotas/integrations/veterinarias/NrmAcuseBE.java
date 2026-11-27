package ar.edu.ubp.das.mascotas.integrations.veterinarias;

/**
 * Acuse devuelto por la veterinaria por cada NRM informado (RF06).
 */
public class NrmAcuseBE {

    private String idMascotaExterna;
    private Integer nroRegMunicipal;
    private boolean confirmado;
    private String mensaje;

    public String getIdMascotaExterna() {
        return idMascotaExterna;
    }

    public void setIdMascotaExterna(String idMascotaExterna) {
        this.idMascotaExterna = idMascotaExterna;
    }

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
