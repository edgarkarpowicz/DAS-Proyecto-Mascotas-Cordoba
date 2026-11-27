package ar.edu.ubp.das.mascotas.integrations.cidi;

/**
 * Resultado lógico de validación de identidad ciudadana en CiDi (RF15 renovado8).
 */
public class ValidacionIdentidadResultBE {

    private int codigoResultado;
    private String cuil;
    private String nombre;
    private String apellido;
    private String correo;
    private Integer nivelCuenta;
    private String mensaje;

    public int getCodigoResultado() {
        return codigoResultado;
    }

    public void setCodigoResultado(int codigoResultado) {
        this.codigoResultado = codigoResultado;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Integer getNivelCuenta() {
        return nivelCuenta;
    }

    public void setNivelCuenta(Integer nivelCuenta) {
        this.nivelCuenta = nivelCuenta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
