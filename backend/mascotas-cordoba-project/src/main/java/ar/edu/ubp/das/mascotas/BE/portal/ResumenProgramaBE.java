package ar.edu.ubp.das.mascotas.BE.portal;

/**
 * Métricas públicas en vivo del Programa Municipal de Bienestar Animal (RF16).
 */
public class ResumenProgramaBE {

    private int publicacionesActivas;
    private int veterinariasHabilitadas;
    private int refugiosAdheridos;

    public ResumenProgramaBE() {
    }

    public ResumenProgramaBE(int publicacionesActivas, int veterinariasHabilitadas, int refugiosAdheridos) {
        this.publicacionesActivas = publicacionesActivas;
        this.veterinariasHabilitadas = veterinariasHabilitadas;
        this.refugiosAdheridos = refugiosAdheridos;
    }

    public int getPublicacionesActivas() {
        return publicacionesActivas;
    }

    public void setPublicacionesActivas(int publicacionesActivas) {
        this.publicacionesActivas = publicacionesActivas;
    }

    public int getVeterinariasHabilitadas() {
        return veterinariasHabilitadas;
    }

    public void setVeterinariasHabilitadas(int veterinariasHabilitadas) {
        this.veterinariasHabilitadas = veterinariasHabilitadas;
    }

    public int getRefugiosAdheridos() {
        return refugiosAdheridos;
    }

    public void setRefugiosAdheridos(int refugiosAdheridos) {
        this.refugiosAdheridos = refugiosAdheridos;
    }
}
