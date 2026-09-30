package ar.edu.ubp.das.mascotas.BE.adopciones;

public class NuevaPublicacionRequestBE {

    private String apiKey;
    private String token;
    private Integer idRefugio;
    private Integer nroRegMunicipal;

    // Datos si se registra una mascota nueva en el momento (RF06)
    private String nombreMascota;
    private String sexo;
    private Integer añoNacimiento;
    private String especie;
    private String raza;

    // Datos de la publicación
    private String fechaPublicacion;
    private String cuilPublicante;
    private String caracteristicasMascota;
    private String condicionSanitaria;
    private String condicionAdopcion;
    private String foto;
    private String estadoPublicacion;

    public NuevaPublicacionRequestBE() {
        this.estadoPublicacion = "Activa";
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIdRefugio() {
        return idRefugio;
    }

    public void setIdRefugio(Integer idRefugio) {
        this.idRefugio = idRefugio;
    }

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
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

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getCuilPublicante() {
        return cuilPublicante;
    }

    public void setCuilPublicante(String cuilPublicante) {
        this.cuilPublicante = cuilPublicante;
    }

    public String getCaracteristicasMascota() {
        return caracteristicasMascota;
    }

    public void setCaracteristicasMascota(String caracteristicasMascota) {
        this.caracteristicasMascota = caracteristicasMascota;
    }

    public String getCondicionSanitaria() {
        return condicionSanitaria;
    }

    public void setCondicionSanitaria(String condicionSanitaria) {
        this.condicionSanitaria = condicionSanitaria;
    }

    public String getCondicionAdopcion() {
        return condicionAdopcion;
    }

    public void setCondicionAdopcion(String condicionAdopcion) {
        this.condicionAdopcion = condicionAdopcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getEstadoPublicacion() {
        return estadoPublicacion;
    }

    public void setEstadoPublicacion(String estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }
}
