package ar.edu.ubp.das.veterinaria.model;

public class NrmAsignadoDTO {

    private String idMascotaExterna;
    private Integer nroRegMunicipal;

    public NrmAsignadoDTO() {
    }

    public NrmAsignadoDTO(String idMascotaExterna, Integer nroRegMunicipal) {
        this.idMascotaExterna = idMascotaExterna;
        this.nroRegMunicipal = nroRegMunicipal;
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
}
