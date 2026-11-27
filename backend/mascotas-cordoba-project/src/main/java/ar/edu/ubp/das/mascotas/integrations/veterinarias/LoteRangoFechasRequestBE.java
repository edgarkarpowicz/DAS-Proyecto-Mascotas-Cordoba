package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import java.time.LocalDate;

/**
 * Filtro por rango de fechas y paginación para consulta por lotes (RF06 / RF09 renovado10).
 */
public class LoteRangoFechasRequestBE {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Integer pagina;
    private Integer tamanioPagina;

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }

    public Integer getTamanioPagina() {
        return tamanioPagina;
    }

    public void setTamanioPagina(Integer tamanioPagina) {
        this.tamanioPagina = tamanioPagina;
    }
}
