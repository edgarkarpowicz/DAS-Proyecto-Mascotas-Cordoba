package ar.edu.ubp.das.mascotas.BE.veterinarias;

/**
 * Datos públicos de una veterinaria adherida y habilitada (RF17).
 */
public class VeterinariaPublicaBE {

    private Integer idVeterinaria;
    private String nombre;
    private String domicilio;
    private String telefono;
    private String correo;
    private String horarios;
    private String serviciosPrestados;

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
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

    public String getServiciosPrestados() {
        return serviciosPrestados;
    }

    public void setServiciosPrestados(String serviciosPrestados) {
        this.serviciosPrestados = serviciosPrestados;
    }
}
