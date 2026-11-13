package ar.edu.ubp.das.mascotas.security;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTests {

    private final AuthorizationService authorizationService = new AuthorizationService();

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testRequireAuthenticatedSinSesionLanza401() {
        UserContextHolder.clear();

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                authorizationService::requireAuthenticated
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testRequirePerfilPermiteCuatroPerfilesCuandoCorresponde() {
        for (PerfilUsuario perfil : PerfilUsuario.values()) {
            SesionUsuarioBE sesion = buildSesion(perfil.name(), perfil == PerfilUsuario.REFUGIO ? 1 : null);
            UserContextHolder.set(sesion);

            SesionUsuarioBE res = authorizationService.requirePerfil(perfil);
            assertEquals(perfil.name(), res.getPerfil());
        }
    }

    @Test
    void testRequireRefugioRechazaCiudadanoVeterinariaYMunicipalidadCon403() {
        for (PerfilUsuario noRefugio : new PerfilUsuario[]{
                PerfilUsuario.CIUDADANO, PerfilUsuario.VETERINARIA, PerfilUsuario.MUNICIPALIDAD}) {
            UserContextHolder.set(buildSesion(noRefugio.name(), null));

            ResponseStatusException ex = assertThrows(
                    ResponseStatusException.class,
                    () -> authorizationService.requireRefugio(1)
            );
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        }
    }

    @Test
    void testRequireRefugioRechazaOtroRefugioCon403() {
        UserContextHolder.set(buildSesion("REFUGIO", 1));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.requireRefugio(2)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("otro refugio"));
    }

    @Test
    void testRequireRefugioPermiteMismoRefugioORefugioNull() {
        UserContextHolder.set(buildSesion("REFUGIO", 1));

        assertEquals(1, authorizationService.requireRefugio(1).getIdRefugio());
        assertEquals(1, authorizationService.requireRefugio(null).getIdRefugio());
    }

    private SesionUsuarioBE buildSesion(String perfil, Integer idRefugio) {
        SesionUsuarioBE s = new SesionUsuarioBE();
        s.setToken("tok-123");
        s.setIdCiudadano(10);
        s.setCuil("20-25999000-5");
        s.setPerfil(perfil);
        s.setIdRefugio(idRefugio);
        return s;
    }
}

