package ar.edu.ubp.das.mascotas.BE.auth;

public class LoginRequestBE {

    private String cuil;
    private String clave;

    public LoginRequestBE() {
    }

    public LoginRequestBE(String cuil, String clave) {
        this.cuil = cuil;
        this.clave = clave;
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
                '}';
    }
}
