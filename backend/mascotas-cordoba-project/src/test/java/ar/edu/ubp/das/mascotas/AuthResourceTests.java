package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.AuthRepository;
import ar.edu.ubp.das.mascotas.resources.AuthResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthResourceTests {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthResource authResource;

    @Test
    void testLoginExitoso() {
        UsuarioRefugioBE mockUsuario = new UsuarioRefugioBE();
        mockUsuario.setIdCiudadano(3);
        mockUsuario.setNombre("Roberto");
        mockUsuario.setApellido("Sánchez");
        mockUsuario.setCuil("20259990005");
        mockUsuario.setPerfil("REFUGIO");
        mockUsuario.setIdRefugio(1);
        mockUsuario.setNombreRefugio("Refugio Patitas Felices");

        when(authRepository.autenticarRefugio("20259990005", "pass123"))
                .thenReturn(Optional.of(mockUsuario));

        LoginRequestBE req = new LoginRequestBE("API_KEY", "20259990005", "pass123");
        ResponseEntity<LoginResponseBE> response = authResource.login(null, req);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getCodigoRespuesta());
        assertNotNull(response.getBody().getToken());
        assertEquals(32, response.getBody().getToken().length());
        assertEquals("Roberto", response.getBody().getUsuario().getNombre());
        assertEquals(1, response.getBody().getUsuario().getIdRefugio());
    }

    @Test
    void testLoginInvalido() {
        when(authRepository.autenticarRefugio("20259990005", "wrongpass"))
                .thenReturn(Optional.empty());

        LoginRequestBE req = new LoginRequestBE("API_KEY", "20259990005", "wrongpass");
        ResponseEntity<LoginResponseBE> response = authResource.login(null, req);

        assertNotNull(response.getBody());
        assertEquals(-1, response.getBody().getCodigoRespuesta());
        assertNull(response.getBody().getToken());
    }

    @Test
    void testLoginParametrosVacios() {
        LoginRequestBE req = new LoginRequestBE("API_KEY", "", "");
        ResponseEntity<LoginResponseBE> response = authResource.login(null, req);

        assertNotNull(response.getBody());
        assertEquals(-1, response.getBody().getCodigoRespuesta());
    }
}
