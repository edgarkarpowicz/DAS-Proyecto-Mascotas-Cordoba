package ar.edu.ubp.das.veterinaria.model;

import java.util.Collections;
import java.util.List;

public class PaginaPracticasDTO {

    private List<PracticaSanitariaDTO> items = Collections.emptyList();
    private int pagina;
    private int tamanioPagina;
    private long totalElementos;
    private int totalPaginas;

    public List<PracticaSanitariaDTO> getItems() {
        return items;
    }

    public void setItems(List<PracticaSanitariaDTO> items) {
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
        this.totalElementos = totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }
}
