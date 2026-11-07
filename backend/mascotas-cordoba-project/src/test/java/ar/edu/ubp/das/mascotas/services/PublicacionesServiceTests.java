package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import ar.edu.ubp.das.mascotas.services.adopciones.PublicacionesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicacionesServiceTests {

    @Mock
    private PublicacionesRepository publicacionesRepository;

    @InjectMocks
    private PublicacionesService publicacionesService;

    @Test
    void testGetEstadosPublicacion() {
        EstadoPublicacionBE e1 = new EstadoPublicacionBE("Activa", "Visible");
        when(publicacionesRepository.getEstadosPublicacion()).thenReturn(List.of(e1));

        List<EstadoPublicacionBE> res = publicacionesService.getEstadosPublicacion();

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Activa", res.get(0).getCodEstado());
    }

    @Test
    void testGetEspecies() {
        EspecieBE esp = new EspecieBE(1, "Perro");
        when(publicacionesRepository.getEspecies()).thenReturn(List.of(esp));

        List<EspecieBE> res = publicacionesService.getEspecies();

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Perro", res.get(0).getNomEspecie());
    }

    @Test
    void testGetPublicacionesPorRefugio() {
        PublicacionAdopcionBE pub = new PublicacionAdopcionBE();
        pub.setNroPublicacion(10);
        pub.setNombreMascota("Rocco");

        when(publicacionesRepository.getPublicacionesPorRefugio(1)).thenReturn(List.of(pub));

        List<PublicacionAdopcionBE> res = publicacionesService.getPublicacionesPorRefugio(1);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Rocco", res.get(0).getNombreMascota());
    }

    @Test
    void testGetPublicacionesPorRefugioInvalido() {
        List<PublicacionAdopcionBE> res = publicacionesService.getPublicacionesPorRefugio(0);
        assertNotNull(res);
        assertTrue(res.isEmpty());
        verifyNoInteractions(publicacionesRepository);
    }

    @Test
    void testGetMascotasDisponibles() {
        MascotaDisponibleBE pet = new MascotaDisponibleBE();
        pet.setNroRegMunicipal(100006);
        pet.setNombre("Thor");

        when(publicacionesRepository.getMascotasDisponibles(2)).thenReturn(List.of(pet));

        List<MascotaDisponibleBE> res = publicacionesService.getMascotasDisponibles(2);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Thor", res.get(0).getNombre());
    }

    @Test
    void testCrearPublicacionExitosa() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNombreMascota("Max");
        req.setEstadoPublicacion("Activa");

        when(publicacionesRepository.esEstadoValido("Activa")).thenReturn(true);
        when(publicacionesRepository.crearPublicacion(req)).thenReturn(105);

        AdministrarPublicacionResponseBE response = publicacionesService.crearPublicacion(req);

        assertNotNull(response);
        assertEquals(1, response.getCodigoRespuesta());
        assertEquals(105, response.getNroPublicacion());
        assertTrue(response.getMensajeRespuesta().contains("exitosamente"));
    }

    @Test
    void testCrearPublicacionRefugioInvalido() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(null);

        AdministrarPublicacionResponseBE response = publicacionesService.crearPublicacion(req);

        assertNotNull(response);
        assertEquals(-1, response.getCodigoRespuesta());
        assertTrue(response.getMensajeRespuesta().contains("obligatorio"));
    }

    @Test
    void testCrearPublicacionEstadoInvalido() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNombreMascota("Max");
        req.setEstadoPublicacion("Inexistente");

        when(publicacionesRepository.esEstadoValido("Inexistente")).thenReturn(false);

        AdministrarPublicacionResponseBE response = publicacionesService.crearPublicacion(req);

        assertNotNull(response);
        assertEquals(-1, response.getCodigoRespuesta());
        assertTrue(response.getMensajeRespuesta().contains("no válido"));
    }

    @Test
    void testActualizarEstadoExitoso() {
        CambioEstadoRequestBE req = new CambioEstadoRequestBE();
        req.setIdRefugio(1);
        req.setNuevoEstado("Pausada");

        when(publicacionesRepository.esEstadoValido("Pausada")).thenReturn(true);
        when(publicacionesRepository.actualizarEstado(1, 1, "Pausada")).thenReturn(true);

        AdministrarPublicacionResponseBE response = publicacionesService.actualizarEstado(1, req);

        assertNotNull(response);
        assertEquals(0, response.getCodigoRespuesta());
        assertEquals(1, response.getNroPublicacion());
        assertTrue(response.getMensajeRespuesta().contains("actualizado"));
    }

    @Test
    void testActualizarEstadoNoEncontrado() {
        CambioEstadoRequestBE req = new CambioEstadoRequestBE();
        req.setIdRefugio(1);
        req.setNuevoEstado("Pausada");

        when(publicacionesRepository.esEstadoValido("Pausada")).thenReturn(true);
        when(publicacionesRepository.actualizarEstado(999, 1, "Pausada")).thenReturn(false);

        AdministrarPublicacionResponseBE response = publicacionesService.actualizarEstado(999, req);

        assertNotNull(response);
        assertEquals(-1, response.getCodigoRespuesta());
        assertTrue(response.getMensajeRespuesta().contains("No se encontró"));
    }

}
