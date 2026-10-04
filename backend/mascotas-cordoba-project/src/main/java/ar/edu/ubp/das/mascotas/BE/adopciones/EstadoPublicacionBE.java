package ar.edu.ubp.das.mascotas.BE.adopciones;

public class EstadoPublicacionBE {

    private String codEstado;
    private String descripcion;

    public EstadoPublicacionBE() {
    }

    public EstadoPublicacionBE(String codEstado, String descripcion) {
        this.codEstado = codEstado;
        this.descripcion = descripcion;
    }

    public String getCodEstado() {
        return codEstado;
    }

    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
