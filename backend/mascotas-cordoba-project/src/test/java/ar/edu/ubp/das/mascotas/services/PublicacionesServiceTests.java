package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.adopciones.PublicacionesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicacionesServiceTests {

    @Mock
    private PublicacionesRepository publicacionesRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private PublicacionesService publicacionesService;

    private final int añoValido = Year.now().getValue() - 2;

    @BeforeEach
    void setUp() {
        SesionUsuarioBE sesionRefugio = new SesionUsuarioBE();
        sesionRefugio.setToken("tok-soap-refugio");
        sesionRefugio.setIdCiudadano(3);
        sesionRefugio.setCuil("20-25999000-5");
        sesionRefugio.setPerfil("REFUGIO");
        sesionRefugio.setIdRefugio(1);

        lenient().when(authorizationService.requireAuthenticated()).thenReturn(sesionRefugio);
        lenient().when(authorizationService.requireRefugio(any())).thenReturn(sesionRefugio);
        lenient().when(publicacionesRepository.esEstadoValido(anyString())).thenAnswer(inv ->
                List.of("Activa", "Pausada", "Finalizada").contains(inv.<String>getArgument(0)));
    }

    private NuevaPublicacionRequestBE nuevaMascota() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNombreMascota("Max");
        req.setSexo("Macho");
        req.setAñoNacimiento(añoValido);
        req.setEspecie("Perro");
        req.setRaza("Mestizo");
        req.setCaracteristicasMascota("Juguetón");
        req.setCondicionAdopcion("Patio cerrado");
        return req;
    }

    private PublicacionAdopcionBE publicacion(int nro, int nrm, String estado) {
        PublicacionAdopcionBE pub = new PublicacionAdopcionBE();
        pub.setNroPublicacion(nro);
        pub.setNroRegMunicipal(nrm);
        pub.setIdRefugio(1);
        pub.setEstadoPublicacion(estado);
        return pub;
    }

    private static HttpStatus status(ResponseStatusException ex) {
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    // ------------------------------------------------------------- consultas

    @Test
    void testGetEstadosYEspecies() {
        when(publicacionesRepository.getEstadosPublicacion()).thenReturn(List.of(new EstadoPublicacionBE("Activa", "Visible")));
        when(publicacionesRepository.getEspecies()).thenReturn(List.of(new EspecieBE(1, "Perro")));

        assertEquals("Activa", publicacionesService.getEstadosPublicacion().get(0).getCodEstado());
        assertEquals("Perro", publicacionesService.getEspecies().get(0).getNomEspecie());
    }

    @Test
    void testGetPublicacionesPorRefugioUsaElRefugioDeLaSesion() {
        PublicacionAdopcionBE pub = publicacion(10, 100004, "Activa");
        when(publicacionesRepository.getPublicacionesPorRefugio(1)).thenReturn(List.of(pub));

        assertEquals(1, publicacionesService.getPublicacionesPorRefugio(1).size());
    }

    @Test
    void testGetPublicacionesRefugioInvalidoEsBadRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesService.getPublicacionesPorRefugio(0));
        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        verify(publicacionesRepository, never()).getPublicacionesPorRefugio(anyInt());
    }

    // ---------------------------------------------------------------- alta

    @Test
    void testCrearPublicacionConMascotaNuevaRegistraMascotaYLuegoPublicacion() {
        when(publicacionesRepository.registrarMascota(eq(1), eq("Max"), eq("M"), eq(añoValido), isNull(), eq("Perro"), eq("Mestizo")))
                .thenReturn(100050);
        when(publicacionesRepository.contarPublicacionesActivas(100050, 0)).thenReturn(0);
        when(publicacionesRepository.insertarPublicacion(eq(1), eq(100050), isNull(), eq("Juguetón"), eq("Patio cerrado"), isNull(), eq("Activa")))
                .thenReturn(105);

        AdministrarPublicacionResponseBE response = publicacionesService.crearPublicacion(nuevaMascota());

        assertEquals(1, response.getCodigoRespuesta());
        assertEquals(105, response.getNroPublicacion());
        InOrder orden = inOrder(publicacionesRepository);
        orden.verify(publicacionesRepository).registrarMascota(anyInt(), anyString(), anyString(), anyInt(), any(), any(), any());
        orden.verify(publicacionesRepository).insertarPublicacion(anyInt(), anyInt(), any(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void testCrearPublicacionAsignaCuilYRefugioDeSesion() {
        when(publicacionesRepository.registrarMascota(anyInt(), anyString(), anyString(), anyInt(), any(), any(), any())).thenReturn(100050);
        when(publicacionesRepository.insertarPublicacion(anyInt(), anyInt(), any(), anyString(), anyString(), any(), anyString())).thenReturn(77);
        NuevaPublicacionRequestBE req = nuevaMascota();

        publicacionesService.crearPublicacion(req);

        assertEquals(1, req.getIdRefugio());
        assertEquals("20-25999000-5", req.getCuilPublicante());
    }

    @Test
    void testCrearPublicacionConMascotaRegistradaNoLaVuelveARegistrar() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNroRegMunicipal(100004);
        req.setCaracteristicasMascota("Cariñoso");
        req.setCondicionAdopcion("Patio cerrado");
        when(publicacionesRepository.mascotaPerteneceARefugio(100004, 1)).thenReturn(true);
        when(publicacionesRepository.contarPublicacionesActivas(100004, 0)).thenReturn(0);
        when(publicacionesRepository.insertarPublicacion(eq(1), eq(100004), any(), anyString(), anyString(), any(), eq("Activa"))).thenReturn(50);

        AdministrarPublicacionResponseBE response = publicacionesService.crearPublicacion(req);

        assertEquals(50, response.getNroPublicacion());
        verify(publicacionesRepository, never()).registrarMascota(anyInt(), anyString(), anyString(), anyInt(), any(), any(), any());
    }

    @Test
    void testCrearPublicacionMascotaDeOtroRefugioEsForbidden() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNroRegMunicipal(200001);
        req.setCaracteristicasMascota("Cariñoso");
        req.setCondicionAdopcion("Patio cerrado");
        when(publicacionesRepository.mascotaPerteneceARefugio(200001, 1)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.FORBIDDEN, status(ex));
        verify(publicacionesRepository, never()).insertarPublicacion(anyInt(), anyInt(), any(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void testCrearPublicacionMascotaConPublicacionActivaEsConflict() {
        NuevaPublicacionRequestBE req = new NuevaPublicacionRequestBE();
        req.setIdRefugio(1);
        req.setNroRegMunicipal(100004);
        req.setCaracteristicasMascota("Cariñoso");
        req.setCondicionAdopcion("Patio cerrado");
        when(publicacionesRepository.mascotaPerteneceARefugio(100004, 1)).thenReturn(true);
        when(publicacionesRepository.contarPublicacionesActivas(100004, 0)).thenReturn(1);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.CONFLICT, status(ex));
        verify(publicacionesRepository, never()).insertarPublicacion(anyInt(), anyInt(), any(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void testCrearPublicacionSinAñoNiFechaEsBadRequest() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setAñoNacimiento(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        assertTrue(ex.getReason().contains("año de nacimiento"));
        verifyNoMoreInteractionsConEscrituras();
    }

    @Test
    void testCrearPublicacionAñoFuturoEsBadRequest() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setAñoNacimiento(Year.now().getValue() + 1);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        assertTrue(ex.getReason().contains("futuro"));
    }

    @Test
    void testCrearPublicacionTomaElAñoDeLaFechaSiSoloLlegaFecha() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setAñoNacimiento(null);
        req.setFechaNacimiento(añoValido + "-05-10");
        when(publicacionesRepository.registrarMascota(anyInt(), anyString(), anyString(), eq(añoValido), any(), any(), any())).thenReturn(100050);
        when(publicacionesRepository.insertarPublicacion(anyInt(), anyInt(), any(), anyString(), anyString(), any(), anyString())).thenReturn(5);

        assertEquals(5, publicacionesService.crearPublicacion(req).getNroPublicacion());
    }

    @Test
    void testCrearPublicacionFechaNacimientoInvalidaEsBadRequest() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setFechaNacimiento("10/05/2024");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        assertTrue(ex.getReason().contains("YYYY-MM-DD"));
    }

    @Test
    void testCrearPublicacionSinCondicionesDeAdopcionEsBadRequest() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setCondicionAdopcion("   ");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        assertTrue(ex.getReason().contains("condiciones de adopción"));
        verifyNoMoreInteractionsConEscrituras();
    }

    @Test
    void testCrearPublicacionFinalizadaNoSePermite() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setEstadoPublicacion("Finalizada");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        verifyNoMoreInteractionsConEscrituras();
    }

    @Test
    void testCrearPublicacionEstadoInexistenteEsBadRequest() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setEstadoPublicacion("Inexistente");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        assertTrue(ex.getReason().contains("no válido"));
    }

    @Test
    void testCrearPublicacionSinRefugioEsBadRequest() {
        NuevaPublicacionRequestBE req = nuevaMascota();
        req.setIdRefugio(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(req));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        assertTrue(ex.getReason().contains("obligatorio"));
    }

    @Test
    void testCrearPublicacionPerfilNoAutorizadoLanzaForbidden() {
        when(authorizationService.requireRefugio(1)).thenThrow(
                new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil no autorizado")
        );

        assertThrows(ResponseStatusException.class, () -> publicacionesService.crearPublicacion(nuevaMascota()));
        verifyNoInteractions(publicacionesRepository);
    }

    // ------------------------------------------------------ cambio de estado

    @Test
    void testPausarPublicacionActiva() {
        when(publicacionesRepository.getPublicacion(1, 1)).thenReturn(Optional.of(publicacion(1, 100004, "Activa")));
        when(publicacionesRepository.actualizarEstado(1, 1, "Pausada")).thenReturn(true);

        AdministrarPublicacionResponseBE response = publicacionesService.actualizarEstado(1, new CambioEstadoRequestBE(1, "Pausada"));

        assertEquals(0, response.getCodigoRespuesta());
        assertTrue(response.getMensajeRespuesta().contains("Pausada"));
    }

    @Test
    void testReanudarPublicacionPausadaVerificaQueNoHayaOtraActiva() {
        when(publicacionesRepository.getPublicacion(2, 1)).thenReturn(Optional.of(publicacion(2, 100005, "Pausada")));
        when(publicacionesRepository.contarPublicacionesActivas(100005, 2)).thenReturn(0);
        when(publicacionesRepository.actualizarEstado(2, 1, "Activa")).thenReturn(true);

        publicacionesService.actualizarEstado(2, new CambioEstadoRequestBE(1, "Activa"));

        verify(publicacionesRepository).contarPublicacionesActivas(100005, 2);
        verify(publicacionesRepository).actualizarEstado(2, 1, "Activa");
    }

    @Test
    void testReanudarConOtraPublicacionActivaEsConflict() {
        when(publicacionesRepository.getPublicacion(2, 1)).thenReturn(Optional.of(publicacion(2, 100005, "Pausada")));
        when(publicacionesRepository.contarPublicacionesActivas(100005, 2)).thenReturn(1);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesService.actualizarEstado(2, new CambioEstadoRequestBE(1, "Activa")));

        assertEquals(HttpStatus.CONFLICT, status(ex));
        verify(publicacionesRepository, never()).actualizarEstado(anyInt(), anyInt(), anyString());
    }

    @Test
    void testNoSePuedeReactivarUnaPublicacionFinalizada() {
        when(publicacionesRepository.getPublicacion(3, 1)).thenReturn(Optional.of(publicacion(3, 100006, "Finalizada")));

        for (String destino : List.of("Activa", "Pausada")) {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> publicacionesService.actualizarEstado(3, new CambioEstadoRequestBE(1, destino)));
            assertEquals(HttpStatus.CONFLICT, status(ex));
            assertTrue(ex.getReason().contains("finalizada"));
        }
        verify(publicacionesRepository, never()).actualizarEstado(anyInt(), anyInt(), anyString());
    }

    @Test
    void testFinalizarDesdeActivaOPausada() {
        when(publicacionesRepository.getPublicacion(1, 1)).thenReturn(Optional.of(publicacion(1, 100004, "Activa")));
        when(publicacionesRepository.getPublicacion(2, 1)).thenReturn(Optional.of(publicacion(2, 100005, "Pausada")));
        when(publicacionesRepository.actualizarEstado(anyInt(), eq(1), eq("Finalizada"))).thenReturn(true);

        publicacionesService.actualizarEstado(1, new CambioEstadoRequestBE(1, "Finalizada"));
        publicacionesService.actualizarEstado(2, new CambioEstadoRequestBE(1, "Finalizada"));

        verify(publicacionesRepository, times(2)).actualizarEstado(anyInt(), eq(1), eq("Finalizada"));
    }

    @Test
    void testCambiarAlMismoEstadoEsBadRequest() {
        when(publicacionesRepository.getPublicacion(1, 1)).thenReturn(Optional.of(publicacion(1, 100004, "Activa")));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesService.actualizarEstado(1, new CambioEstadoRequestBE(1, "Activa")));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
    }

    @Test
    void testActualizarEstadoPublicacionInexistenteEsNotFound() {
        when(publicacionesRepository.getPublicacion(999, 1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesService.actualizarEstado(999, new CambioEstadoRequestBE(1, "Pausada")));

        assertEquals(HttpStatus.NOT_FOUND, status(ex));
        assertTrue(ex.getReason().contains("No se encontró"));
    }

    @Test
    void testActualizarEstadoInexistenteEsBadRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> publicacionesService.actualizarEstado(1, new CambioEstadoRequestBE(1, "Inexistente")));

        assertEquals(HttpStatus.BAD_REQUEST, status(ex));
        assertTrue(ex.getReason().contains("no válido"));
        verify(publicacionesRepository, never()).getPublicacion(anyInt(), anyInt());
    }

    // ------------------------------------------------------- transaccionalidad

    @Test
    void testLasOperacionesQueGrabanSonTransaccionales() throws NoSuchMethodException {
        assertNotNull(PublicacionesService.class
                .getMethod("crearPublicacion", NuevaPublicacionRequestBE.class)
                .getAnnotation(Transactional.class));
        assertNotNull(PublicacionesService.class
                .getMethod("actualizarEstado", int.class, CambioEstadoRequestBE.class)
                .getAnnotation(Transactional.class));
    }

    private void verifyNoMoreInteractionsConEscrituras() {
        verify(publicacionesRepository, never()).registrarMascota(anyInt(), anyString(), anyString(), anyInt(), any(), any(), any());
        verify(publicacionesRepository, never()).insertarPublicacion(anyInt(), anyInt(), any(), anyString(), anyString(), any(), anyString());
    }

}
