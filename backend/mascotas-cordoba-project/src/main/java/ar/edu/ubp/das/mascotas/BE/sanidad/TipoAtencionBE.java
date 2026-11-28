package ar.edu.ubp.das.mascotas.BE.sanidad;

/**
 * Elemento de catálogo oficial de tipos de prácticas sanitarias (RF09).
 */
public class TipoAtencionBE {

    private Integer codTipoAtencion;
    private String descTipoAtencion;

    public Integer getCodTipoAtencion() {
        return codTipoAtencion;
    }

    public void setCodTipoAtencion(Integer codTipoAtencion) {
        this.codTipoAtencion = codTipoAtencion;
    }

    public String getDescTipoAtencion() {
        return descTipoAtencion;
    }

    public void setDescTipoAtencion(String descTipoAtencion) {
        this.descTipoAtencion = descTipoAtencion;
    }
}
