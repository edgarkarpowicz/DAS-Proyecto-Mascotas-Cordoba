package ar.edu.ubp.das.mascotas.BE.mascotas;

/**
 * Modelo del Registro Único Municipal de Mascotas (RF06 / RF20).
 */
public class MascotaBE {

    private Integer nroRegMunicipal;
    private String nombre;
    private String sexo;
    private Integer añoNacimiento;
    private String especie;
    private String raza;
    private String microchip;
    private Boolean castrado;
    private Boolean vive;
    private Integer idResponsable;
    private Integer idRefugio;
    private boolean nrmReutilizado;
    private int cantidadAtenciones;
    private String estadoSanitario;

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Integer getAñoNacimiento() {
        return añoNacimiento;
    }

    public void setAñoNacimiento(Integer añoNacimiento) {
        this.añoNacimiento = añoNacimiento;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public Boolean getCastrado() {
        return castrado;
    }

    public void setCastrado(Boolean castrado) {
        this.castrado = castrado;
    }

    public Boolean getVive() {
        return vive;
    }

    public void setVive(Boolean vive) {
        this.vive = vive;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Integer getIdRefugio() {
        return idRefugio;
    }

    public void setIdRefugio(Integer idRefugio) {
        this.idRefugio = idRefugio;
    }

    public boolean isNrmReutilizado() {
        return nrmReutilizado;
    }

    public void setNrmReutilizado(boolean nrmReutilizado) {
        this.nrmReutilizado = nrmReutilizado;
    }

    public int getCantidadAtenciones() {
        return cantidadAtenciones;
    }

    public void setCantidadAtenciones(int cantidadAtenciones) {
        this.cantidadAtenciones = cantidadAtenciones;
    }

    public String getEstadoSanitario() {
        return estadoSanitario;
    }

    public void setEstadoSanitario(String estadoSanitario) {
        this.estadoSanitario = estadoSanitario;
    }
}
