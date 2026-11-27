package ar.edu.ubp.das.mascotas.BE.sanidad;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Carnet Sanitario Digital oficial emitido para una mascota (RF10 / RF11).
 */
public class CarnetSanitarioBE {

    private Integer nroRegMunicipal;
    private String codigoValidacion;
    private String nombreMascota;
    private String responsableNombre;
    private LocalDate fechaEmision;
    private boolean vigente;
    private List<AtencionSanitariaBE> atenciones = Collections.emptyList();

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public String getCodigoValidacion() {
        return codigoValidacion;
    }

    public void setCodigoValidacion(String codigoValidacion) {
        this.codigoValidacion = codigoValidacion;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getResponsableNombre() {
        return responsableNombre;
    }

    public void setResponsableNombre(String responsableNombre) {
        this.responsableNombre = responsableNombre;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }

    public List<AtencionSanitariaBE> getAtenciones() {
        return atenciones;
    }

    public void setAtenciones(List<AtencionSanitariaBE> atenciones) {
        this.atenciones = atenciones != null ? atenciones : Collections.emptyList();
    }
}
