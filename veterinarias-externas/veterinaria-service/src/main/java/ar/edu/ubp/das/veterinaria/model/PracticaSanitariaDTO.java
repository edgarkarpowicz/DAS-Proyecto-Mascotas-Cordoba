package ar.edu.ubp.das.veterinaria.model;

public class PracticaSanitariaDTO {

    private String idMascotaExterna;
    private Integer nroRegMunicipal;
    private Integer nroAtencion;
    private Integer nroDetalle;
    private String fechaAtencion;
    private String tipoPractica;
    private String detalle;
    private String fechaVencimiento;
    private String profesional;

    public PracticaSanitariaDTO() {
    }

    public PracticaSanitariaDTO(
            String idMascotaExterna,
            Integer nroAtencion,
            Integer nroDetalle,
            String fechaAtencion,
            String tipoPractica,
            String detalle,
            String fechaVencimiento,
            String profesional) {
        this.idMascotaExterna = idMascotaExterna;
        this.nroAtencion = nroAtencion;
        this.nroDetalle = nroDetalle;
        this.fechaAtencion = fechaAtencion;
        this.tipoPractica = tipoPractica;
        this.detalle = detalle;
        this.fechaVencimiento = fechaVencimiento;
        this.profesional = profesional;
    }

    public PracticaSanitariaDTO copyWithNrm(Integer nrm) {
        PracticaSanitariaDTO copy = new PracticaSanitariaDTO(
                idMascotaExterna,
                nroAtencion,
                nroDetalle,
                fechaAtencion,
                tipoPractica,
                detalle,
                fechaVencimiento,
                profesional
        );
        copy.setNroRegMunicipal(nrm);
        return copy;
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

    public String getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(String fechaAtencion) {
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

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getProfesional() {
        return profesional;
    }

    public void setProfesional(String profesional) {
        this.profesional = profesional;
    }
}
