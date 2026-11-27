package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import java.time.LocalDate;

/**
 * Registro plano de práctica sanitaria devuelto por veterinarias (RF09 renovado10).
 */
public class PracticaSanitariaExternaBE {

    private String idMascotaExterna;
    private Integer nroRegMunicipal;
    private Integer nroAtencion;
    private Integer nroDetalle;
    private LocalDate fechaAtencion;
    private String tipoPractica;
    private String detalle;
    private LocalDate fechaVencimiento;
    private String profesional;

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

    public Integer getNroAtencion() {
        return nroAtencion;
    }

    public void setNroAtencion(Integer nroAtencion) {
        this.nroAtencion = nroAtencion;
    }

    public Integer getNroDetalle() {
        return nroDetalle;
    }

    public void setNroDetalle(Integer nroDetalle) {
        this.nroDetalle = nroDetalle;
    }

    public LocalDate getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDate fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public String getTipoPractica() {
        return tipoPractica;
    }

    public void setTipoPractica(String tipoPractica) {
        this.tipoPractica = tipoPractica;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getProfesional() {
        return profesional;
    }

    public void setProfesional(String profesional) {
        this.profesional = profesional;
    }
}
