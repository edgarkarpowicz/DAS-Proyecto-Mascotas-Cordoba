package ar.edu.ubp.das.mascotas.integrations.veterinarias;

/**
 * Asignación de NRM a informar de vuelta a una veterinaria (RF06).
 */
public class NrmAsignadoBE {

    private String idMascotaExterna;
    private Integer nroRegMunicipal;

    public NrmAsignadoBE() {
    }

    public NrmAsignadoBE(String idMascotaExterna, Integer nroRegMunicipal) {
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
