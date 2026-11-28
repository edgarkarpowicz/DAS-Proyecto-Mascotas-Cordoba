package ar.edu.ubp.das.mascotas.BE.sanidad;

import java.time.LocalDate;

/**
 * Solicitud de carga de práctica sanitaria del programa (RF09).
 */
public class RegistrarAtencionRequestBE {

    private Integer nroRegMunicipal;
    private LocalDate fechaAtencion;
    private Integer codTipoAtencion;
    private String detalleAtencion;
    private LocalDate fechaVencimiento;
    private Integer idVeterinaria;
    private Integer idProfesional;
    private String profesional;

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public LocalDate getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDate fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public Integer getCodTipoAtencion() {
        return codTipoAtencion;
    }

    public void setCodTipoAtencion(Integer codTipoAtencion) {
        this.codTipoAtencion = codTipoAtencion;
    }

    public String getDetalleAtencion() {
        return detalleAtencion;
    }

    public void setDetalleAtencion(String detalleAtencion) {
        this.detalleAtencion = detalleAtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public Integer getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(Integer idProfesional) {
        this.idProfesional = idProfesional;
    }

    public String getProfesional() {
        return profesional;
    }

    public void setProfesional(String profesional) {
        this.profesional = profesional;
    }
}
