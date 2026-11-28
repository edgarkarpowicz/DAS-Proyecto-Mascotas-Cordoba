package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.asistente.ConsultaAsistenteRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.RegistrarMascotaRequestBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.RegistrarAtencionRequestBE;
import ar.edu.ubp.das.mascotas.repositories.mascotas.MascotasRepository;
import ar.edu.ubp.das.mascotas.repositories.sanidad.SanidadRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import ar.edu.ubp.das.mascotas.services.asistente.AsistenteService;
import ar.edu.ubp.das.mascotas.services.mascotas.MascotasService;
import ar.edu.ubp.das.mascotas.services.sanidad.SanidadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MandatoryRfScaffoldsTests {

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testPlaceholdersProtegidosValidaPerfilYLanza501() {
        AuthorizationService authService = new AuthorizationService();

        MascotasService mascotasService = new MascotasService();
        ReflectionTestUtils.setField(mascotasService, "mascotasRepository", mock(MascotasRepository.class));
        ReflectionTestUtils.setField(mascotasService, "authorizationService", authService);

        SanidadService sanidadService = new SanidadService();
        ReflectionTestUtils.setField(sanidadService, "sanidadRepository", mock(SanidadRepository.class));
        ReflectionTestUtils.setField(sanidadService, "authorizationService", authService);

        UserContextHolder.set(buildSesion(PerfilUsuario.CIUDADANO));
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                () -> mascotasService.registrarMascota(new RegistrarMascotaRequestBE())).getStatusCode());
        assertEquals(HttpStatus.NOT_IMPLEMENTED, assertThrows(ResponseStatusException.class,
                () -> mascotasService.consultarMisMascotas(new PageRequestBE())).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                () -> sanidadService.registrarAtencion(new RegistrarAtencionRequestBE())).getStatusCode());
        assertEquals(HttpStatus.NOT_IMPLEMENTED, assertThrows(ResponseStatusException.class,
                () -> sanidadService.generarCarnet(100001)).getStatusCode());
        assertEquals(HttpStatus.NOT_IMPLEMENTED, assertThrows(ResponseStatusException.class,
                () -> sanidadService.consultarCarnet(100001)).getStatusCode());

        UserContextHolder.set(buildSesion(PerfilUsuario.VETERINARIA));
        assertEquals(HttpStatus.NOT_IMPLEMENTED, assertThrows(ResponseStatusException.class,
                () -> mascotasService.registrarMascota(new RegistrarMascotaRequestBE())).getStatusCode());
        assertEquals(HttpStatus.NOT_IMPLEMENTED, assertThrows(ResponseStatusException.class,
                () -> sanidadService.registrarAtencion(new RegistrarAtencionRequestBE())).getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlaceholdersPublicosLanzan501SinExigirSesion() {
        UserContextHolder.clear();

        SanidadService sanidadService = new SanidadService();
        ReflectionTestUtils.setField(sanidadService, "sanidadRepository", mock(SanidadRepository.class));
        ReflectionTestUtils.setField(sanidadService, "authorizationService", new AuthorizationService());
        assertEquals(HttpStatus.NOT_IMPLEMENTED, assertThrows(ResponseStatusException.class,
                () -> sanidadService.validarCarnet("ABC-123")).getStatusCode());

        AsistenteService asistenteService = new AsistenteService();
        ReflectionTestUtils.setField(asistenteService, "authorizationService", new AuthorizationService());
        ReflectionTestUtils.setField(asistenteService, "asistenteClientProvider", mock(ObjectProvider.class));
        assertEquals(HttpStatus.NOT_IMPLEMENTED, assertThrows(ResponseStatusException.class,
                () -> asistenteService.consultar(new ConsultaAsistenteRequestBE())).getStatusCode());
    }

    private SesionUsuarioBE buildSesion(PerfilUsuario perfil) {
        SesionUsuarioBE s = new SesionUsuarioBE();
        s.setIdCiudadano(1);
        s.setCuil("20-12345678-9");
        s.setPerfil(perfil.name());
        return s;
    }
}
