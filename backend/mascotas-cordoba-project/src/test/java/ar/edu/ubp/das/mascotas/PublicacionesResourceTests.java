package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import ar.edu.ubp.das.mascotas.resources.adopciones.PublicacionesResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicacionesResourceTests {

    @Mock
    private PublicacionesRepository publicacionesRepository;

    @InjectMocks
    private PublicacionesResource publicacionesResource;

    @Test
    void testGetEstadosPublicacion() {
        EstadoPublicacionBE e1 = new EstadoPublicacionBE("Activa", "Visible");
        EstadoPublicacionBE e2 = new EstadoPublicacionBE("Pausada", "Suspendida");
        EstadoPublicacionBE e3 = new EstadoPublicacionBE("Finalizada", "Adoptado");

        when(publicacionesRepository.getEstadosPublicacion())
                .thenReturn(Arrays.asList(e1, e2, e3));

        ResponseEntity<List<EstadoPublicacionBE>> response = publicacionesResource.getEstadosPublicacion();

        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("Activa", response.getBody().get(0).getCodEstado());
    }

    @Test
    void testListarPublicaciones() {
        PublicacionAdopcionBE pub1 = new PublicacionAdopcionBE();
        pub1.setNroPublicacion(1);
        pub1.setNombreMascota("Rocco");
        pub1.setEstadoPublicacion("Activa");

        PublicacionAdopcionBE pub2 = new PublicacionAdopcionBE();
        pub2.setNroPublicacion(2);
        pub2.setNombreMascota("Mía");
        pub2.setEstadoPublicacion("Activa");

        when(publicacionesRepository.getPublicacionesPorRefugio(1))
                .thenReturn(Arrays.asList(pub1, pub2));

        ResponseEntity<List<PublicacionAdopcionBE>> response = publicacionesResource.getPublicaciones(1);

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Rocco", response.getBody().get(0).getNombreMascota());
    }

    @Test
    void testCrearPublicacionExitosa() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNroRegMunicipal(100004);
        req.setCaracteristicasMascota("Cariñoso y amigable");
        req.setCondicionAdopcion("Patio cerrado");

        when(publicacionesRepository.esEstadoValido("Activa"))
                .thenReturn(true);
        when(publicacionesRepository.crearPublicacion(any(NuevaPublicacionRequestBE.class)))
                .thenReturn(50);

        ResponseEntity<AdministrarPublicacionResponseBE> response = publicacionesResource.crearPublicacion(null, req);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getCodigoRespuesta());
        assertEquals(50, response.getBody().getNroPublicacion());
        assertTrue(response.getBody().getMensajeRespuesta().contains("exitosamente"));
    }

    @Test
    void testCrearPublicacionInvalidaLanzaException() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesResource.crearPublicacion(null, req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("obligatorio"));
    }

    @Test
    void testCrearPublicacionEstadoInvalidoLanzaException() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNroRegMunicipal(100004);
        req.setEstadoPublicacion("EstadoFicticio");

        when(publicacionesRepository.esEstadoValido("EstadoFicticio"))
                .thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesResource.crearPublicacion(null, req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Estado no válido"));
    }

    @Test
    void testActualizarEstadoExitoso() {
        CambioEstadoRequestBE req = new CambioEstadoRequestBE(1, "Pausada");

        when(publicacionesRepository.esEstadoValido("Pausada"))
                .thenReturn(true);
        when(publicacionesRepository.actualizarEstado(1, 1, "Pausada"))
                .thenReturn(true);

        ResponseEntity<AdministrarPublicacionResponseBE> response = publicacionesResource.actualizarEstado(1, req);

        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCodigoRespuesta());
        assertEquals(1, response.getBody().getNroPublicacion());
        assertTrue(response.getBody().getMensajeRespuesta().contains("Pausada"));
    }

    @Test
    void testActualizarEstadoInvalidoLanzaException() {
        CambioEstadoRequestBE req = new CambioEstadoRequestBE(1, "Inexistente");

        when(publicacionesRepository.esEstadoValido("Inexistente"))
                .thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesResource.actualizarEstado(1, req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Estado no válido"));
    }

}
