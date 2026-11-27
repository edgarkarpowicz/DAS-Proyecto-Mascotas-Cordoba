package ar.edu.ubp.das.mascotas.BE.mascotas;

/**
 * Solicitud de alta en el Registro Único Municipal de Mascotas (RF06).
 */
public class RegistrarMascotaRequestBE {

    private String nombre;
    private String sexo;
    private Integer añoNacimiento;
    private String especie;
    private String raza;
    private String microchip;
    private Boolean castrado;
    private String cuilResponsable;
    private Integer idRefugio;

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

    public String getCuilResponsable() {
        return cuilResponsable;
    }

    public void setCuilResponsable(String cuilResponsable) {
        this.cuilResponsable = cuilResponsable;
    }

    public Integer getIdRefugio() {
        return idRefugio;
    }

    public void setIdRefugio(Integer idRefugio) {
        this.idRefugio = idRefugio;
    }
}
