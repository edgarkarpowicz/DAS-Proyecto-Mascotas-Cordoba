package ar.edu.ubp.das.mascotas.BE.auth;

/**
 * Contexto de sesión autenticada extraído del JWT de autorización (RF15).
 * Extiende {@link UsuarioBE} para evitar duplicar atributos de identidad y perfil.
 */
public class SesionUsuarioBE extends UsuarioBE {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static SesionUsuarioBE fromUsuario(String token, UsuarioBE source) {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken(token);
        sesion.copyFieldsFrom(source);
        return sesion;
    }

    public UsuarioBE toUsuarioBE() {
        UsuarioBE usuario = new UsuarioBE();
        usuario.copyFieldsFrom(this);
        return usuario;
    }

    @Override
    public String toString() {
        return "SesionUsuarioBE{" +
                "token='***'" +
                ", idCiudadano=" + getIdCiudadano() +
                ", cuil='" + getCuil() + '\'' +
                ", perfil='" + getPerfil() + '\'' +
                ", idRefugio=" + getIdRefugio() +
                ", idVeterinaria=" + getIdVeterinaria() +
                '}';
    }
}
