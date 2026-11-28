package ar.edu.ubp.das.veterinaria.model;

public class NrmAcuseDTO {

    private String idMascotaExterna;
    private Integer nroRegMunicipal;
    private boolean confirmado;
    private String mensaje;

    public NrmAcuseDTO() {
    }

    public NrmAcuseDTO(String idMascotaExterna, Integer nroRegMunicipal, boolean confirmado, String mensaje) {
        this.idMascotaExterna = idMascotaExterna;
        this.nroRegMunicipal = nroRegMunicipal;
        this.confirmado = confirmado;
        this.mensaje = mensaje;
    }

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
