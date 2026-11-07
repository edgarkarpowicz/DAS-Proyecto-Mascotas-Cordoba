package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import ar.edu.ubp.das.mascotas.services.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLoginExitoso() {
        LoginRequestBE req = new LoginRequestBE();
        req.setCuil("20-25999000-5");
        req.setClave("pass123");

        UsuarioRefugioBE usuario = new UsuarioRefugioBE();
        usuario.setIdCiudadano(3);
        usuario.setNombre("Roberto");
        usuario.setApellido("Sánchez");
        usuario.setIdRefugio(1);
        usuario.setNombreRefugio("Refugio Patitas Felices");

        when(authRepository.autenticarRefugio("20-25999000-5", "pass123"))
                .thenReturn(Optional.of(usuario));

        LoginResponseBE response = authService.login(req);

        assertNotNull(response);
        assertEquals(1, response.getCodigoRespuesta());
        assertEquals("Usuario autenticado exitosamente", response.getMensajeRespuesta());
        assertNotNull(response.getToken());
        assertNotNull(response.getUsuario());
        assertEquals(1, response.getUsuario().getIdRefugio());
    }

    @Test
    void testLoginDatosFaltantes() {
        LoginRequestBE req = new LoginRequestBE();
        req.setCuil("");
        req.setClave("");

        LoginResponseBE response = authService.login(req);

        assertNotNull(response);
        assertEquals(-1, response.getCodigoRespuesta());
        assertTrue(response.getMensajeRespuesta().contains("obligatorios"));
        verifyNoInteractions(authRepository);
    }

    @Test
    void testLoginCredencialesInvalidas() {
        LoginRequestBE req = new LoginRequestBE();
        req.setCuil("20-99999999-9");
        req.setClave("claveErronea");

        when(authRepository.autenticarRefugio("20-99999999-9", "claveErronea"))
                .thenReturn(Optional.empty());

        LoginResponseBE response = authService.login(req);

        assertNotNull(response);
        assertEquals(-1, response.getCodigoRespuesta());
        assertTrue(response.getMensajeRespuesta().contains("inválidas"));
        assertNull(response.getUsuario());
    }

}
