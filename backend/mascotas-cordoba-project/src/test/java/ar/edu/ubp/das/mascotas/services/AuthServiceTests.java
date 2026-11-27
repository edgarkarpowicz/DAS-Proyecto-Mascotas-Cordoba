package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import ar.edu.ubp.das.mascotas.services.auth.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testLoginExitosoEmiteJwtYEstableceContexto() {
        LoginRequestBE req = new LoginRequestBE();
        req.setCuil(" 20-25999000-5 ");
        req.setClave("pass123");

        UsuarioBE usuario = new UsuarioBE();
        usuario.setIdCiudadano(3);
        usuario.setNombre("Roberto");
        usuario.setApellido("Sánchez");
        usuario.setCuil("20-25999000-5");
        usuario.setPerfil("REFUGIO");
        usuario.setIdRefugio(1);
        usuario.setNombreRefugio("Refugio Patitas Felices");

        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("hdr.pay.sig");
        sesion.setCuil("20-25999000-5");
        sesion.setPerfil("REFUGIO");

        when(authRepository.autenticarUsuario("20-25999000-5", "pass123")).thenReturn(Optional.of(usuario));
        when(jwtTokenService.generarToken(usuario)).thenReturn("hdr.pay.sig");
        when(jwtTokenService.construirSesion("hdr.pay.sig", usuario)).thenReturn(sesion);

        LoginResponseBE response = authService.login(req);

        assertEquals(1, response.getCodigoRespuesta());
        assertEquals("Usuario autenticado exitosamente", response.getMensajeRespuesta());
        assertEquals("hdr.pay.sig", response.getToken());
        assertEquals(1, response.getUsuario().getIdRefugio());
        assertEquals("20-25999000-5", UserContextHolder.get().getCuil());
    }

    @Test
    void testLoginDatosFaltantesEsBadRequest() {
        LoginRequestBE req = new LoginRequestBE();
        req.setCuil("");
        req.setClave("");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("obligatorios"));
        verifyNoInteractions(authRepository, jwtTokenService);
    }

    @Test
    void testLoginCredencialesInvalidasEsUnauthorized() {
        LoginRequestBE req = new LoginRequestBE();
        req.setCuil("20-99999999-9");
        req.setClave("claveErronea");

        when(authRepository.autenticarUsuario("20-99999999-9", "claveErronea")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Credenciales inválidas"));
        verify(jwtTokenService, never()).generarToken(any());
        assertNull(UserContextHolder.get());
    }

    @Test
    void testLogoutExitosoRevocaElToken() {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("tok-soap-123");
        sesion.setIdCiudadano(5);
        sesion.setPerfil("VETERINARIA");

        when(jwtTokenService.validarToken("tok-soap-123")).thenReturn(Optional.of(sesion));

        LoginResponseBE response = authService.logout(" tok-soap-123 ");

        assertEquals(1, response.getCodigoRespuesta());
        verify(jwtTokenService, times(1)).revocarToken("tok-soap-123");
    }

    @Test
    void testLogoutTokenInvalidoEsUnauthorized() {
        when(jwtTokenService.validarToken("tok-invalido")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.logout("tok-invalido"));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(jwtTokenService, never()).revocarToken(any());
    }

    @Test
    void testLogoutSinTokenEsBadRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.logout("  "));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(jwtTokenService);
    }

}
