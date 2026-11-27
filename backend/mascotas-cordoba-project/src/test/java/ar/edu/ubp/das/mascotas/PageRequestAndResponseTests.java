package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageRequestAndResponseTests {

    @Test
    void testPageRequestNormalizaValoresPorDefectoYLimites() {
        PageRequestBE defaults = PageRequestBE.of(null, null);
        assertEquals(PageRequestBE.DEFAULT_PAGE, defaults.getPagina());
        assertEquals(PageRequestBE.DEFAULT_SIZE, defaults.getTamanioPagina());
        assertEquals(0, defaults.getOffset());

        PageRequestBE clamped = PageRequestBE.of(0, 500);
        assertEquals(1, clamped.getPagina());
        assertEquals(PageRequestBE.MAX_SIZE, clamped.getTamanioPagina());

        PageRequestBE pageThree = PageRequestBE.of(3, 15);
        assertEquals(30, pageThree.getOffset());
    }

    @Test
    void testPageResponseCalculaTotalPaginasCorrectamente() {
        PageRequestBE req = PageRequestBE.of(2, 10);
        PageResponseBE<String> page = PageResponseBE.of(List.of("a", "b"), req, 25L);

        assertEquals(2, page.getPagina());
        assertEquals(10, page.getTamanioPagina());
        assertEquals(25L, page.getTotalElementos());
        assertEquals(3, page.getTotalPaginas());
        assertEquals(2, page.getItems().size());

        PageResponseBE<String> empty = PageResponseBE.of(List.of(), req, 0L);
        assertEquals(0, empty.getTotalPaginas());
    }
}
