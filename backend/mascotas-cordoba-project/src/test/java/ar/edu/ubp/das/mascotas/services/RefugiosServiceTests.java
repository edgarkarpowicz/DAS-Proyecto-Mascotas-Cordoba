package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.refugios.RefugioPublicoBE;
import ar.edu.ubp.das.mascotas.repositories.refugios.RefugiosRepository;
import ar.edu.ubp.das.mascotas.services.refugios.RefugiosService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefugiosServiceTests {

    @Mock
    private RefugiosRepository refugiosRepository;

    @InjectMocks
    private RefugiosService refugiosService;

    @Test
    void testConsultarRefugiosRetornaPaginaConConteoPublicaciones() {
        RefugioPublicoBE refugio = new RefugioPublicoBE();
        refugio.setIdRefugio(1);
        refugio.setNombre("Refugio Patitas Felices");
        refugio.setPublicacionesActivas(2);

        when(refugiosRepository.countRefugiosPublicos(null)).thenReturn(1);
        when(refugiosRepository.getRefugiosPublicos(null, 0, 20)).thenReturn(List.of(refugio));

        PageResponseBE<RefugioPublicoBE> page = refugiosService.consultarRefugios(null, new PageRequestBE());

        assertEquals(1, page.getTotalElementos());
        assertEquals(2, page.getItems().get(0).getPublicacionesActivas());
    }

    @Test
    void testConsultarRefugiosVacioNoConsultaLista() {
        when(refugiosRepository.countRefugiosPublicos("inexistente")).thenReturn(0);

        PageResponseBE<RefugioPublicoBE> page = refugiosService.consultarRefugios("inexistente", new PageRequestBE());

        assertTrue(page.getItems().isEmpty());
        verify(refugiosRepository, never()).getRefugiosPublicos(anyString(), anyInt(), anyInt());
    }

    @Test
    void testConsultarPublicacionesActivasValidaIdRefugio() {
        ResponseStatusException badReq = assertThrows(ResponseStatusException.class,
                () -> refugiosService.consultarPublicacionesActivas(0));
        assertEquals(HttpStatus.BAD_REQUEST, badReq.getStatusCode());

        PublicacionAdopcionBE pub = new PublicacionAdopcionBE();
        pub.setNroPublicacion(1);
        pub.setEstadoPublicacion("Activa");
        when(refugiosRepository.getPublicacionesActivasPorRefugio(1)).thenReturn(List.of(pub));

        List<PublicacionAdopcionBE> activas = refugiosService.consultarPublicacionesActivas(1);
        assertEquals(1, activas.size());
        assertEquals("Activa", activas.get(0).getEstadoPublicacion());
    }
}
