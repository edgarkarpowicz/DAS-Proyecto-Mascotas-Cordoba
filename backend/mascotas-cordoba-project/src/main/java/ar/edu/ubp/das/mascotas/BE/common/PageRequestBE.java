package ar.edu.ubp.das.mascotas.BE.common;

/**
 * Contrato estándar de solicitud paginada compartido por listados internos e integraciones.
 */
public class PageRequestBE {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private int pagina;
    private int tamanioPagina;

    public PageRequestBE() {
        this(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    public PageRequestBE(int pagina, int tamanioPagina) {
        this.pagina = Math.max(DEFAULT_PAGE, pagina);
        if (tamanioPagina <= 0) {
            this.tamanioPagina = DEFAULT_SIZE;
        } else {
            this.tamanioPagina = Math.min(MAX_SIZE, tamanioPagina);
        }
    }

    public static PageRequestBE of(Integer pagina, Integer tamanioPagina) {
        int page = pagina != null ? pagina : DEFAULT_PAGE;
        int size = tamanioPagina != null ? tamanioPagina : DEFAULT_SIZE;
        return new PageRequestBE(page, size);
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = Math.max(DEFAULT_PAGE, pagina);
    }

    public int getTamanioPagina() {
        return tamanioPagina;
    }

    public void setTamanioPagina(int tamanioPagina) {
        if (tamanioPagina <= 0) {
            this.tamanioPagina = DEFAULT_SIZE;
        } else {
            this.tamanioPagina = Math.min(MAX_SIZE, tamanioPagina);
        }
    }

    public int getOffset() {
        return (pagina - 1) * tamanioPagina;
    }
}
