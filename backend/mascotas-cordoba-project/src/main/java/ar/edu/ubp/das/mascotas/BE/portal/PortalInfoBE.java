package ar.edu.ubp.das.mascotas.BE.portal;

import ar.edu.ubp.das.mascotas.BE.auth.UsuarioBE;

import java.util.Collections;
import java.util.List;

/**
 * Datos de inicio del Portal del Programa Municipal de Bienestar Animal (RF16).
 */
public class PortalInfoBE {

    private String nombrePrograma;
    private String descripcionPrograma;
    private boolean autenticado;
    private String perfil;
    private UsuarioBE usuario;
    private ResumenProgramaBE resumen;
    private List<ServicioHabilitadoBE> serviciosPublicos = Collections.emptyList();
    private List<ServicioHabilitadoBE> serviciosHabilitados = Collections.emptyList();

    public String getNombrePrograma() {
        return nombrePrograma;
    }

    public void setNombrePrograma(String nombrePrograma) {
        this.nombrePrograma = nombrePrograma;
    }

    public String getDescripcionPrograma() {
        return descripcionPrograma;
    }

    public void setDescripcionPrograma(String descripcionPrograma) {
        this.descripcionPrograma = descripcionPrograma;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public UsuarioBE getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioBE usuario) {
        this.usuario = usuario;
    }

    public ResumenProgramaBE getResumen() {
        return resumen;
    }

    public void setResumen(ResumenProgramaBE resumen) {
        this.resumen = resumen;
    }

    public List<ServicioHabilitadoBE> getServiciosPublicos() {
        return serviciosPublicos;
    }

    public void setServiciosPublicos(List<ServicioHabilitadoBE> serviciosPublicos) {
        this.serviciosPublicos = serviciosPublicos != null ? serviciosPublicos : Collections.emptyList();
    }

    public List<ServicioHabilitadoBE> getServiciosHabilitados() {
        return serviciosHabilitados;
    }

    public void setServiciosHabilitados(List<ServicioHabilitadoBE> serviciosHabilitados) {
        this.serviciosHabilitados = serviciosHabilitados != null ? serviciosHabilitados : Collections.emptyList();
    }
}
