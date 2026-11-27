package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.resources.auth.AuthResource;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.auth.IAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * El recurso REST solo adapta HTTP y delega en AuthService.
 * Las reglas de autenticación se prueban en AuthServiceTests.
 */
@ExtendWith(MockitoExtension.class)
class AuthResourceTests {

    @Mock
    private IAuthService authService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private AuthResource authResource;

    @Test
    void testLoginDelegaEnElServicio() {
        UsuarioRefugioBE usuario = new UsuarioRefugioBE();
        usuario.setNombre("Roberto");
        usuario.setPerfil("REFUGIO");
        usuario.setIdRefugio(1);
        LoginRequestBE req = new LoginRequestBE("20-25999000-5", "pass123");
        when(authService.login(req)).thenReturn(LoginResponseBE.exitoso("header.payload.signature", usuario));

        ResponseEntity<LoginResponseBE> response = authResource.login(req);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getCodigoRespuesta());
        assertEquals("header.payload.signature", response.getBody().getToken());
        assertEquals("REFUGIO", response.getBody().getUsuario().getPerfil());
    }

    @Test
    void testLoginInvalidoPropagaElErrorDelServicio() {
        LoginRequestBE req = new LoginRequestBE("20259990005", "wrongpass");
        when(authService.login(req)).thenThrow(
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o usuario inhabilitado"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authResource.login(req));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
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
    void testLogoutRevocaElTokenDeLaSesionMedianteElServicio() {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("abc123sessiontoken");
        sesion.setIdCiudadano(3);
        sesion.setPerfil("REFUGIO");
        when(authorizationService.requireAuthenticated()).thenReturn(sesion);

        ResponseEntity<Map<String, Object>> response = authResource.logout();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().get("codigoRespuesta"));
        verify(authService, times(1)).logout("abc123sessiontoken");
    }

}
