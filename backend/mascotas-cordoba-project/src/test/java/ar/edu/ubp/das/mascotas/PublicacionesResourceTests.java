package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.resources.adopciones.PublicacionesResource;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.adopciones.IPublicacionesService;
import ar.edu.ubp.das.mascotas.services.adopciones.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * El recurso REST solo adapta HTTP y delega en PublicacionesService.
 * Las reglas de negocio se prueban en PublicacionesServiceTests.
 */
@ExtendWith(MockitoExtension.class)
class PublicacionesResourceTests {

    @Mock
    private IPublicacionesService publicacionesService;

    @Mock
    private StorageService storageService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private PublicacionesResource publicacionesResource;

    @BeforeEach
    void setUp() {
        SesionUsuarioBE sesionRefugio = new SesionUsuarioBE();
        sesionRefugio.setToken("tok-refugio-1");
        sesionRefugio.setIdCiudadano(3);
        sesionRefugio.setCuil("20-25999000-5");
        sesionRefugio.setPerfil("REFUGIO");
        sesionRefugio.setIdRefugio(1);

        lenient().when(authorizationService.requireRefugio(any())).thenReturn(sesionRefugio);
    }

    @Test
    void testGetEstadosPublicacionDelegaEnElServicio() {
        when(publicacionesService.getEstadosPublicacion()).thenReturn(Arrays.asList(
                new EstadoPublicacionBE("Activa", "Visible"),
                new EstadoPublicacionBE("Pausada", "Suspendida"),
                new EstadoPublicacionBE("Finalizada", "Adoptado")
        ));

        ResponseEntity<List<EstadoPublicacionBE>> response = publicacionesResource.getEstadosPublicacion();

        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("Activa", response.getBody().get(0).getCodEstado());
    }

    @Test
    void testGetEspeciesDelegaEnElServicio() {
        when(publicacionesService.getEspecies()).thenReturn(List.of(new EspecieBE(1, "Perro"), new EspecieBE(2, "Gato")));

        ResponseEntity<List<EspecieBE>> response = publicacionesResource.getEspecies();

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Perro", response.getBody().get(0).getNomEspecie());
    }

    @Test
    void testListarPublicacionesDelegaEnElServicio() {
        PublicacionAdopcionBE pub = new PublicacionAdopcionBE();
        pub.setNroPublicacion(1);
        pub.setNombreMascota("Rocco");
        when(publicacionesService.getPublicacionesPorRefugio(1)).thenReturn(List.of(pub));

        ResponseEntity<List<PublicacionAdopcionBE>> response = publicacionesResource.getPublicaciones(1);

        assertNotNull(response.getBody());
        assertEquals("Rocco", response.getBody().get(0).getNombreMascota());
    }

    @Test
    void testSubirFotoExitosa() {
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "perro.jpg",
                "image/jpeg",
                "contenido-de-prueba".getBytes()
        );
        when(storageService.almacenarFoto(file)).thenReturn("/uploads/publicaciones/uuid_perro.jpg");

        ResponseEntity<Map<String, Object>> response = publicacionesResource.subirFoto(file);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().get("codigoRespuesta"));
        assertEquals("/uploads/publicaciones/uuid_perro.jpg", response.getBody().get("urlFoto"));
    }

    @Test
    void testCrearPublicacionDelegaEnElServicio() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNroRegMunicipal(100004);
        when(publicacionesService.crearPublicacion(req))
                .thenReturn(AdministrarPublicacionResponseBE.exitoso("Publicación de adopción creada exitosamente", 50));

        ResponseEntity<AdministrarPublicacionResponseBE> response = publicacionesResource.crearPublicacion(req);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getCodigoRespuesta());
        assertEquals(50, response.getBody().getNroPublicacion());
        verify(publicacionesService).crearPublicacion(req);
    }

    @Test
    void testActualizarEstadoDelegaEnElServicio() {
        CambioEstadoRequestBE req = new CambioEstadoRequestBE(1, "Pausada");
        when(publicacionesService.actualizarEstado(1, req))
                .thenReturn(AdministrarPublicacionResponseBE.actualizado("Estado de publicación actualizado a Pausada", 1));

        ResponseEntity<AdministrarPublicacionResponseBE> response = publicacionesResource.actualizarEstado(1, req);

        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCodigoRespuesta());
        assertTrue(response.getBody().getMensajeRespuesta().contains("Pausada"));
    }

    @Test
    void testErroresDeNegocioDelServicioLleganComoRespuestaHttp() {
        CambioEstadoRequestBE req = new CambioEstadoRequestBE(1, "Activa");
        when(publicacionesService.actualizarEstado(7, req)).thenThrow(
                new ResponseStatusException(HttpStatus.CONFLICT, "Una publicación finalizada no puede reactivarse ni cambiar de estado")
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesResource.actualizarEstado(7, req));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testGetMascotasDisponiblesDelegaEnElServicio() {
        MascotaDisponibleBE pet = new MascotaDisponibleBE();
        pet.setNroRegMunicipal(100006);
        pet.setNombre("Thor");
        when(publicacionesService.getMascotasDisponibles(1)).thenReturn(List.of(pet));

        ResponseEntity<List<MascotaDisponibleBE>> response = publicacionesResource.getMascotasDisponibles(1);

        assertNotNull(response.getBody());
        assertEquals("Thor", response.getBody().get(0).getNombre());
    }

}
