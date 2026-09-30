package ar.edu.ubp.das.mascotas.BE.adopciones;

public class MascotaDisponibleBE {

    private Integer nroRegMunicipal;
    private String nombre;
    private String sexo;
    private Integer añoNacimiento;
    private Integer edadAproximada;
    private String especie;
    private String raza;

    public MascotaDisponibleBE() {
    }

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
}
