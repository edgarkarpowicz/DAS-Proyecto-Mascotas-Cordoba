package ar.edu.ubp.das.mascotas.BE.integraciones;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registro histórico de una corrida por lotes con una clínica adherida (RF06 / RF09 / RNF06).
 */
public class LoteSincronizacionBE {

    private Long idLote;
    private Integer idVeterinaria;
    private String nombreVeterinaria;
    private String tipoLote;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private LocalDateTime fechaEjecucion;
    private String estado;
    private int registrosProcesados;
    private int registrosNuevos;
    private int registrosDeduplicados;
    private int registrosConfirmados;
    private String detalleError;

    public Long getIdLote() {
        return idLote;
    }

    public void setIdLote(Long idLote) {
        this.idLote = idLote;
    }

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public String getNombreVeterinaria() {
        return nombreVeterinaria;
    }

    public void setNombreVeterinaria(String nombreVeterinaria) {
        this.nombreVeterinaria = nombreVeterinaria;
    }

    public String getTipoLote() {
        return tipoLote;
    }

    public void setTipoLote(String tipoLote) {
        this.tipoLote = tipoLote;
    }

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

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getRegistrosProcesados() {
        return registrosProcesados;
    }

    public void setRegistrosProcesados(int registrosProcesados) {
        this.registrosProcesados = registrosProcesados;
    }

    public int getRegistrosNuevos() {
        return registrosNuevos;
    }

    public void setRegistrosNuevos(int registrosNuevos) {
        this.registrosNuevos = registrosNuevos;
    }

    public int getRegistrosDeduplicados() {
        return registrosDeduplicados;
    }

    public void setRegistrosDeduplicados(int registrosDeduplicados) {
        this.registrosDeduplicados = registrosDeduplicados;
    }

    public int getRegistrosConfirmados() {
        return registrosConfirmados;
    }

    public void setRegistrosConfirmados(int registrosConfirmados) {
        this.registrosConfirmados = registrosConfirmados;
    }

    public String getDetalleError() {
        return detalleError;
    }

    public void setDetalleError(String detalleError) {
        this.detalleError = detalleError;
    }
}
