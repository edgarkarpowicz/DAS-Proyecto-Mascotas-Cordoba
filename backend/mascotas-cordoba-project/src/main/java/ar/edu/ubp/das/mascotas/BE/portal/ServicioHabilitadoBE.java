package ar.edu.ubp.das.mascotas.BE.portal;

/**
 * Ítem de navegación y capacidad funcional retornado por el portal municipal (RF16).
 */
public class ServicioHabilitadoBE {

    private String codigo;
    private String titulo;
    private String descripcion;
    private String ruta;
    private boolean disponible;
    private boolean requiereAutenticacion;
    private String rfReferencia;

    public ServicioHabilitadoBE() {
    }

    public ServicioHabilitadoBE(
            String codigo,
            String titulo,
            String descripcion,
            String ruta,
            boolean disponible,
            boolean requiereAutenticacion,
            String rfReferencia) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ruta = ruta;
        this.disponible = disponible;
        this.requiereAutenticacion = requiereAutenticacion;
        this.rfReferencia = rfReferencia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isRequiereAutenticacion() {
        return requiereAutenticacion;
    }

    public void setRequiereAutenticacion(boolean requiereAutenticacion) {
        this.requiereAutenticacion = requiereAutenticacion;
    }

    public String getRfReferencia() {
        return rfReferencia;
    }

    public void setRfReferencia(String rfReferencia) {
        this.rfReferencia = rfReferencia;
    }
}
