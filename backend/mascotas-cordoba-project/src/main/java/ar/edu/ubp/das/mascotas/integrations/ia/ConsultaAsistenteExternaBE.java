package ar.edu.ubp.das.mascotas.integrations.ia;

/**
 * Solicitud hacia el servicio externo de IA generativa con contexto armado vía RAG (RF21 renovado10).
 */
public class ConsultaAsistenteExternaBE {

    private String consulta;
    private String contexto;
    private Integer longitudMaxima;

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public Integer getLongitudMaxima() {
        return longitudMaxima;
    }

    public void setLongitudMaxima(Integer longitudMaxima) {
        this.longitudMaxima = longitudMaxima;
    }
}
