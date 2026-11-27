package ar.edu.ubp.das.mascotas.integrations.veterinarias;

/**
 * DTO externo de mascota pendiente de NRM recibido desde veterinarias (RF06 renovado10).
 */
public class MascotaExternaBE {

    private String idMascotaExterna;
    private String nombre;
    private String sexo;
    private Integer añoNacimiento;
    private Integer edadAproximada;
    private String especie;
    private String raza;
    private Boolean castrado;
    private String microchip;
    private String cuilResponsable;

    public String getIdMascotaExterna() {
        return idMascotaExterna;
    }

    public void setIdMascotaExterna(String idMascotaExterna) {
        this.idMascotaExterna = idMascotaExterna;
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

    public Integer getEdadAproximada() {
        return edadAproximada;
    }

    public void setEdadAproximada(Integer edadAproximada) {
        this.edadAproximada = edadAproximada;
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

    public Boolean getCastrado() {
        return castrado;
    }

    public void setCastrado(Boolean castrado) {
        this.castrado = castrado;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public String getCuilResponsable() {
        return cuilResponsable;
    }

    public void setCuilResponsable(String cuilResponsable) {
        this.cuilResponsable = cuilResponsable;
    }
}
