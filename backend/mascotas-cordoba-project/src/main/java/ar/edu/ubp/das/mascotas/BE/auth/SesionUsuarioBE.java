package ar.edu.ubp.das.mascotas.BE.auth;

/**
 * Contexto de sesión autenticada extraído del JWT de autorización (RF15).
 */
public class SesionUsuarioBE {

    private String token;
    private Integer idCiudadano;
    private String apellido;
    private String nombre;
    private String cuil;
    private String correo;
    private String perfil;
    private Integer idRefugio;
    private Integer idVeterinaria;
    private String nombreRefugio;
    private String nombreVeterinaria;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIdCiudadano() {
        return idCiudadano;
    }

    public void setIdCiudadano(Integer idCiudadano) {
        this.idCiudadano = idCiudadano;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Integer getIdRefugio() {
        return idRefugio;
    }

    public void setIdRefugio(Integer idRefugio) {
        this.idRefugio = idRefugio;
    }

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public String getNombreRefugio() {
        return nombreRefugio;
    }

    public void setNombreRefugio(String nombreRefugio) {
        this.nombreRefugio = nombreRefugio;
    }

    public String getNombreVeterinaria() {
        return nombreVeterinaria;
    }

    public void setNombreVeterinaria(String nombreVeterinaria) {
        this.nombreVeterinaria = nombreVeterinaria;
    }

    public boolean esPerfil(PerfilUsuario esperado) {
        return esperado != null && esperado.name().equalsIgnoreCase(perfil);
    }

    public UsuarioRefugioBE toUsuarioRefugioBE() {
        UsuarioRefugioBE usuario = new UsuarioRefugioBE();
        usuario.setIdCiudadano(idCiudadano);
        usuario.setApellido(apellido);
        usuario.setNombre(nombre);
        usuario.setCuil(cuil);
        usuario.setCorreo(correo);
        usuario.setPerfil(perfil);
        usuario.setIdRefugio(idRefugio);
        usuario.setNombreRefugio(nombreRefugio);
        usuario.setIdVeterinaria(idVeterinaria);
        usuario.setNombreVeterinaria(nombreVeterinaria);
        return usuario;
    }

    @Override
    public String toString() {
        return "SesionUsuarioBE{" +
                "token='***'" +
                ", idCiudadano=" + idCiudadano +
                ", cuil='" + cuil + '\'' +
                ", perfil='" + perfil + '\'' +
                ", idRefugio=" + idRefugio +
                ", idVeterinaria=" + idVeterinaria +
                '}';
    }
}

