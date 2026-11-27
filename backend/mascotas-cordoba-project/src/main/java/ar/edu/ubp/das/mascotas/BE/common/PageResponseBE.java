package ar.edu.ubp.das.mascotas.BE.common;

import java.util.Collections;
import java.util.List;

/**
 * Contrato estándar de respuesta paginada compartido por todos los módulos.
 */
public class PageResponseBE<T> {

    private List<T> items;
    private int pagina;
    private int tamanioPagina;
    private long totalElementos;
    private int totalPaginas;

    public PageResponseBE() {
        this.items = Collections.emptyList();
    }

    public PageResponseBE(List<T> items, int pagina, int tamanioPagina, long totalElementos, int totalPaginas) {
        this.items = items != null ? items : Collections.emptyList();
        this.pagina = pagina;
        this.tamanioPagina = tamanioPagina;
        this.totalElementos = Math.max(0L, totalElementos);
        this.totalPaginas = Math.max(0, totalPaginas);
    }

    public static <T> PageResponseBE<T> of(List<T> items, PageRequestBE pageRequest, long totalElementos) {
        PageRequestBE req = pageRequest != null ? pageRequest : new PageRequestBE();
        long safeTotal = Math.max(0L, totalElementos);
        int pages = safeTotal == 0L ? 0 : (int) Math.ceil((double) safeTotal / req.getTamanioPagina());
        return new PageResponseBE<>(items, req.getPagina(), req.getTamanioPagina(), safeTotal, pages);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items != null ? items : Collections.emptyList();
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getTamanioPagina() {
        return tamanioPagina;
    }

    public void setTamanioPagina(int tamanioPagina) {
        this.tamanioPagina = tamanioPagina;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(long totalElementos) {
        this.totalElementos = Math.max(0L, totalElementos);
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = Math.max(0, totalPaginas);
    }
}
