package ar.edu.ubp.das.mascotas.BE;

public class LoginRequestBE {

    private String apiKey;
    private String cuil;
    private String clave;

    public LoginRequestBE() {
    }

    public LoginRequestBE(String apiKey, String cuil, String clave) {
        this.apiKey = apiKey;
        this.cuil = cuil;
        this.clave = clave;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public String toString() {
        return "LoginRequestBE{" +
                "cuil='" + cuil + '\'' +
                ", apiKey='" + (apiKey != null ? "***" : "null") + '\'' +
                '}';
    }
}
