package ar.edu.ubp.das.mascotas.BE.integraciones;

import java.time.LocalDate;

/**
 * Resumen devuelto por una ejecución de sincronización por lotes con una clínica.
 */
public class ResultadoSincronizacionVeterinariaBE {

    private Integer idVeterinaria;
    private String nombreVeterinaria;
    private String tecnologia;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private String estado;
    private int mascotasNuevas;
    private int mascotasDeduplicadas;
    private int nrmsConfirmados;
    private int atencionesProcesadas;
    private String mensajeError;

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

    public String getTecnologia() {
        return tecnologia;
    }

    public void setTecnologia(String tecnologia) {
        this.tecnologia = tecnologia;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getMascotasNuevas() {
        return mascotasNuevas;
    }

    public void setMascotasNuevas(int mascotasNuevas) {
        this.mascotasNuevas = mascotasNuevas;
    }

    public int getMascotasDeduplicadas() {
        return mascotasDeduplicadas;
    }

    public void setMascotasDeduplicadas(int mascotasDeduplicadas) {
        this.mascotasDeduplicadas = mascotasDeduplicadas;
    }

    public int getNrmsConfirmados() {
        return nrmsConfirmados;
    }

    public void setNrmsConfirmados(int nrmsConfirmados) {
        this.nrmsConfirmados = nrmsConfirmados;
    }

    public int getAtencionesProcesadas() {
        return atencionesProcesadas;
    }

    public void setAtencionesProcesadas(int atencionesProcesadas) {
        this.atencionesProcesadas = atencionesProcesadas;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}
