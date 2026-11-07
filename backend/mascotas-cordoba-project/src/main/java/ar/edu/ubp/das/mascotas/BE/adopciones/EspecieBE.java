package ar.edu.ubp.das.mascotas.BE.adopciones;

public class EspecieBE {

    private Integer nroValorDominio;
    private String nomEspecie;

    public EspecieBE() {
    }

    public EspecieBE(Integer nroValorDominio, String nomEspecie) {
        this.nroValorDominio = nroValorDominio;
        this.nomEspecie = nomEspecie;
    }

    public Integer getNroValorDominio() {
        return nroValorDominio;
    }

    public void setNroValorDominio(Integer nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }

    public String getNomEspecie() {
        return nomEspecie;
    }

    public void setNomEspecie(String nomEspecie) {
        this.nomEspecie = nomEspecie;
    }

}
