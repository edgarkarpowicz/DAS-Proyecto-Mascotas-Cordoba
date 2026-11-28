package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.veterinarias.VeterinariaPublicaBE;
import ar.edu.ubp.das.mascotas.repositories.veterinarias.VeterinariasRepository;
import ar.edu.ubp.das.mascotas.services.veterinarias.VeterinariasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeterinariasServiceTests {

    @Mock
    private VeterinariasRepository veterinariasRepository;

    @InjectMocks
    private VeterinariasService veterinariasService;

    @Test
    void testConsultarVeterinariasRetornaPaginaFiltradaYNormalizada() {
        VeterinariaPublicaBE vet = new VeterinariaPublicaBE();
        vet.setIdVeterinaria(1);
        vet.setNombre("San Antonio Vet");

        when(veterinariasRepository.countVeterinariasHabilitadas("castración")).thenReturn(1);
        when(veterinariasRepository.getVeterinariasHabilitadas("castración", 0, 10)).thenReturn(List.of(vet));

        PageResponseBE<VeterinariaPublicaBE> page = veterinariasService.consultarVeterinarias(
                "  castración  ",
                PageRequestBE.of(1, 10)
        );

        assertEquals(1, page.getTotalElementos());
        assertEquals(1, page.getTotalPaginas());
        assertEquals(1, page.getItems().size());
        assertEquals("San Antonio Vet", page.getItems().get(0).getNombre());
    }

    @Test
    void testConsultarVeterinariasSinResultadosEvitaConsultaDeItems() {
        when(veterinariasRepository.countVeterinariasHabilitadas(null)).thenReturn(0);

        PageResponseBE<VeterinariaPublicaBE> page = veterinariasService.consultarVeterinarias(
                "   ",
                PageRequestBE.of(2, 10)
        );

        assertEquals(0, page.getTotalElementos());
        assertEquals(0, page.getTotalPaginas());
        assertTrue(page.getItems().isEmpty());
        verify(veterinariasRepository, never()).getVeterinariasHabilitadas(anyString(), anyInt(), anyInt());
    }
}
