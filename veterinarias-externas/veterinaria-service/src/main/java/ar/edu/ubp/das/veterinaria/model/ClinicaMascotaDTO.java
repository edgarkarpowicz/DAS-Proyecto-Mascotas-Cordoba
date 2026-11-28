package ar.edu.ubp.das.veterinaria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * Mascota administrada por la clínica veterinaria externa.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ClinicaMascotaDTO {

    private String idMascotaExterna;
    @XmlTransient
    @JsonIgnore
    private Integer nroRegMunicipal;
    @XmlTransient
    @JsonIgnore
    private String fechaRegistro;
    private String nombre;
    private String sexo;
    @JsonProperty("añoNacimiento")
    @XmlElement(name = "anioNacimiento")
    private Integer anioNacimiento;
    private Integer edadAproximada;
    private String especie;
    private String raza;
    private Boolean castrado;
    private String microchip;
    private String cuilResponsable;

    public ClinicaMascotaDTO() {
    }

    public ClinicaMascotaDTO(
            String idMascotaExterna,
            String fechaRegistro,
            String nombre,
            String sexo,
            Integer anioNacimiento,
            String especie,
            String raza,
            Boolean castrado,
            String microchip,
            String cuilResponsable) {
        this.idMascotaExterna = idMascotaExterna;
        this.fechaRegistro = fechaRegistro;
        this.nombre = nombre;
        this.sexo = sexo;
        this.anioNacimiento = anioNacimiento;
        this.especie = especie;
        this.raza = raza;
        this.castrado = castrado;
        this.microchip = microchip;
        this.cuilResponsable = cuilResponsable;
    }

    public String getIdMascotaExterna() {
        return idMascotaExterna;
    }

    public void setIdMascotaExterna(String idMascotaExterna) {
        this.idMascotaExterna = idMascotaExterna;
    }

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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

    public Integer getAnioNacimiento() {
        return anioNacimiento;
    }

    public void setAnioNacimiento(Integer anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
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
