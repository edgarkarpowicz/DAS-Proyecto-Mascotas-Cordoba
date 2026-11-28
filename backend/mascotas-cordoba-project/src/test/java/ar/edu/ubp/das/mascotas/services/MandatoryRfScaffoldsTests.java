package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.asistente.ConsultaAsistenteRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.RegistrarAtencionRequestBE;
import ar.edu.ubp.das.mascotas.repositories.sanidad.SanidadRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import ar.edu.ubp.das.mascotas.services.asistente.AsistenteService;
import ar.edu.ubp.das.mascotas.services.sanidad.SanidadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class MandatoryRfScaffoldsTests {

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testSanidadRechazaPerfilNoAutorizadoYAsistentePermaneceEn501() {
        AuthorizationService authService = new AuthorizationService();

        SanidadService sanidadService = new SanidadService();
        ReflectionTestUtils.setField(sanidadService, "sanidadRepository", mock(SanidadRepository.class));
        ReflectionTestUtils.setField(sanidadService, "authorizationService", authService);

        UserContextHolder.set(buildSesion(PerfilUsuario.CIUDADANO));
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                () -> sanidadService.registrarAtencion(new RegistrarAtencionRequestBE())).getStatusCode());

        UserContextHolder.clear();
        AsistenteService asistenteService = new AsistenteService();
        ReflectionTestUtils.setField(asistenteService, "authorizationService", authService);
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
