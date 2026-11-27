package ar.edu.ubp.das.mascotas.BE.asistente;

/**
 * Consulta enviada por un visitante o usuario autenticado al asistente virtual (RF21).
 */
public class ConsultaAsistenteRequestBE {

    private String consulta;
    private boolean incluirContextoPersonal;

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public boolean isIncluirContextoPersonal() {
        return incluirContextoPersonal;
    }

    public void setIncluirContextoPersonal(boolean incluirContextoPersonal) {
        this.incluirContextoPersonal = incluirContextoPersonal;
    }
}
