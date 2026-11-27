package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import java.time.LocalDate;

/**
 * Configuración técnica de integración para una veterinaria adherida (RF04).
 */
public class VeterinariaConfiguracionBE {

    private Integer idVeterinaria;
    private String urlServicio;
    private TecnologiaIntegracion tecnologia;
    private String parametros;
    private String apiKey;
    private LocalDate ultimaFechaProcesada;

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public String getUrlServicio() {
        return urlServicio;
    }

    public void setUrlServicio(String urlServicio) {
        this.urlServicio = urlServicio;
    }

    public TecnologiaIntegracion getTecnologia() {
        return tecnologia;
    }

    public void setTecnologia(TecnologiaIntegracion tecnologia) {
        this.tecnologia = tecnologia;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public LocalDate getUltimaFechaProcesada() {
        return ultimaFechaProcesada;
    }

    public void setUltimaFechaProcesada(LocalDate ultimaFechaProcesada) {
        this.ultimaFechaProcesada = ultimaFechaProcesada;
    }
}
