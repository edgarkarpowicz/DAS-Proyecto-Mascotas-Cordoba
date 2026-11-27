package ar.edu.ubp.das.mascotas.BE.refugios;

/**
 * Datos públicos de un refugio adherido (RF18).
 */
public class RefugioPublicoBE {

    private Integer idRefugio;
    private String nombre;
    private String domicilio;
    private String telefono;
    private String correo;
    private String horarios;
    private int publicacionesActivas;

    public Integer getIdRefugio() {
        return idRefugio;
    }

    public void setIdRefugio(Integer idRefugio) {
        this.idRefugio = idRefugio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }

    public int getPublicacionesActivas() {
        return publicacionesActivas;
    }

    public void setPublicacionesActivas(int publicacionesActivas) {
        this.publicacionesActivas = publicacionesActivas;
    }
}
