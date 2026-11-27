package ar.edu.ubp.das.mascotas.BE.sanidad;

/**
 * Resultado de validación pública o por terceros de un carnet digital (RF11).
 */
public class ValidacionCarnetBE {

    private boolean autentico;
    private boolean vigente;
    private Integer nroRegMunicipal;
    private String mensaje;

    public boolean isAutentico() {
        return autentico;
    }

    public void setAutentico(boolean autentico) {
        this.autentico = autentico;
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
