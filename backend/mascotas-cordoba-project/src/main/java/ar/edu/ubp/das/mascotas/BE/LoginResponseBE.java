package ar.edu.ubp.das.mascotas.BE;

public class LoginResponseBE {

    private int codigoRespuesta;
    private String mensajeRespuesta;
    private String token;
    private UsuarioRefugioBE usuario;

    public LoginResponseBE() {
    }

    public LoginResponseBE(int codigoRespuesta, String mensajeRespuesta, String token, UsuarioRefugioBE usuario) {
        this.codigoRespuesta = codigoRespuesta;
        this.mensajeRespuesta = mensajeRespuesta;
        this.token = token;
        this.usuario = usuario;
    }

    public static LoginResponseBE exitoso(String token, UsuarioRefugioBE usuario) {
        return new LoginResponseBE(1, "Usuario autenticado exitosamente", token, usuario);
    }

    public static LoginResponseBE error(String mensaje) {
        return new LoginResponseBE(-1, mensaje, null, null);
    }

    public int getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(int codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioRefugioBE getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioRefugioBE usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "LoginResponseBE{" +
                "codigoRespuesta=" + codigoRespuesta +
                ", mensajeRespuesta='" + mensajeRespuesta + '\'' +
                ", token='" + (token != null ? "***" : "null") + '\'' +
                ", usuario=" + (usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "null") +
                '}';
    }
}
