package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import ar.edu.ubp.das.mascotas.repositories.auth.SesionRepository;
import ar.edu.ubp.das.mascotas.resources.auth.AuthResource;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthResourceTests {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private AuthResource authResource;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testLoginExitosoPersisteTokenEnSesion() {
        UsuarioRefugioBE mockUsuario = new UsuarioRefugioBE();
        mockUsuario.setIdCiudadano(3);
        mockUsuario.setNombre("Roberto");
        mockUsuario.setApellido("Sánchez");
        mockUsuario.setCuil("20-25999000-5");
        mockUsuario.setPerfil("REFUGIO");
        mockUsuario.setIdRefugio(1);
        mockUsuario.setNombreRefugio("Refugio Patitas Felices");

        when(authRepository.autenticarUsuario("20-25999000-5", "pass123"))
                .thenReturn(Optional.of(mockUsuario));

        LoginRequestBE req = new LoginRequestBE("API_KEY", "20-25999000-5", "pass123");
        ResponseEntity<LoginResponseBE> response = authResource.login(null, req);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getCodigoRespuesta());
        String token = response.getBody().getToken();
        assertNotNull(token);
        assertEquals(32, token.length());
        assertEquals("Roberto", response.getBody().getUsuario().getNombre());
        assertEquals("REFUGIO", response.getBody().getUsuario().getPerfil());
        assertEquals(1, response.getBody().getUsuario().getIdRefugio());

        verify(sesionRepository, times(1)).crearSesion(eq(token), eq(mockUsuario));
        assertNotNull(UserContextHolder.get());
        assertEquals("20-25999000-5", UserContextHolder.get().getCuil());
    }

    @Test
    void testLoginInvalidoLanzaException() {
        when(authRepository.autenticarUsuario("20259990005", "wrongpass"))
                .thenReturn(Optional.empty());

        LoginRequestBE req = new LoginRequestBE("API_KEY", "20259990005", "wrongpass");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authResource.login(null, req));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Credenciales inválidas"));
        verify(sesionRepository, never()).crearSesion(any(), any());
    }

    @Test
    void testLoginParametrosVaciosLanzaException() {
        LoginRequestBE req = new LoginRequestBE("API_KEY", "", "");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authResource.login(null, req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("obligatorios"));
        verifyNoInteractions(authRepository, sesionRepository);
    }

    @Test
    void testGetUsuarioActualRetornaIdentidadDeSesion() {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("tok-123");
        sesion.setIdCiudadano(6);
        sesion.setNombre("Laura");
        sesion.setApellido("Benítez");
        sesion.setCuil("27-22333444-1");
        sesion.setPerfil("MUNICIPALIDAD");

        when(authorizationService.requireAuthenticated()).thenReturn(sesion);

        ResponseEntity<UsuarioRefugioBE> response = authResource.getUsuarioActual();
        assertNotNull(response.getBody());
        assertEquals("MUNICIPALIDAD", response.getBody().getPerfil());
        assertEquals("27-22333444-1", response.getBody().getCuil());
    }

    @Test
    void testLogoutRevocaTokenActual() {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("abc123sessiontoken");
        sesion.setIdCiudadano(3);
        sesion.setPerfil("REFUGIO");

        when(authorizationService.requireAuthenticated()).thenReturn(sesion);

        ResponseEntity<Map<String, Object>> response = authResource.logout();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().get("codigoRespuesta"));
        verify(sesionRepository, times(1)).revocarSesion("abc123sessiontoken");
    }

}
