package ar.edu.ubp.das.mascotas.BE.auth;

import java.util.Optional;

/**
 * Representación unificada de cualquier usuario autenticado de la plataforma
 * (CIUDADANO, REFUGIO, VETERINARIA o MUNICIPALIDAD) para RF15 y sus módulos.
 */
public class UsuarioBE {

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
    private Integer idVeterinaria;
    private String nombreVeterinaria;

    public UsuarioBE() {
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

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public String getNombreVeterinaria() {
        return nombreVeterinaria;
    }

    public void setNombreVeterinaria(String nombreVeterinaria) {
        this.nombreVeterinaria = nombreVeterinaria;
    }

    public Optional<PerfilUsuario> resolvePerfil() {
        return PerfilUsuario.fromString(perfil);
    }

    public boolean esPerfil(PerfilUsuario esperado) {
        return esperado != null && esperado.name().equalsIgnoreCase(perfil);
    }

    protected void copyFieldsFrom(UsuarioBE source) {
        if (source == null) {
            return;
        }
        setIdCiudadano(source.getIdCiudadano());
        setApellido(source.getApellido());
        setNombre(source.getNombre());
        setCuil(source.getCuil());
        setCorreo(source.getCorreo());
        setPerfil(source.getPerfil() != null ? source.getPerfil().trim().toUpperCase() : null);
        setIdRefugio(source.getIdRefugio());
        setNombreRefugio(source.getNombreRefugio());
        setRazonSocialRefugio(source.getRazonSocialRefugio());
        setHabilitacionMunicipal(source.getHabilitacionMunicipal());
        setIdVeterinaria(source.getIdVeterinaria());
        setNombreVeterinaria(source.getNombreVeterinaria());
    }

    @Override
    public String toString() {
        return "UsuarioBE{" +
                "idCiudadano=" + idCiudadano +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cuil='" + cuil + '\'' +
                ", perfil='" + perfil + '\'' +
                ", idRefugio=" + idRefugio +
                ", idVeterinaria=" + idVeterinaria +
                '}';
    }
}
