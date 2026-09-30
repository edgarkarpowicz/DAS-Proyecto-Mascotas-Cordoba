package ar.edu.ubp.das.mascotas.BE.auth;

public class UsuarioRefugioBE {

    private Integer idCiudadano;
    private String apellido;
    private String nombre;
    private String cuil;
    private String correo;
    private String perfil;
    private Integer idRefugio;
    private String nombreRefugio;
    private String razonSocialRefugio;
    private String habilitacionMunicipal;

    public UsuarioRefugioBE() {
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

    public String getNombreRefugio() {
        return nombreRefugio;
    }

    public void setNombreRefugio(String nombreRefugio) {
        this.nombreRefugio = nombreRefugio;
    }

    public String getRazonSocialRefugio() {
        return razonSocialRefugio;
    }

    public void setRazonSocialRefugio(String razonSocialRefugio) {
        this.razonSocialRefugio = razonSocialRefugio;
    }

    public String getHabilitacionMunicipal() {
        return habilitacionMunicipal;
    }

    public void setHabilitacionMunicipal(String habilitacionMunicipal) {
        this.habilitacionMunicipal = habilitacionMunicipal;
    }

    @Override
    public String toString() {
        return "UsuarioRefugioBE{" +
                "idCiudadano=" + idCiudadano +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cuil='" + cuil + '\'' +
                ", perfil='" + perfil + '\'' +
                ", idRefugio=" + idRefugio +
                ", nombreRefugio='" + nombreRefugio + '\'' +
                '}';
    }
}
