package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.config.AuthTokenInterceptor;
import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenInterceptorTests {

    @Mock
    private JwtTokenService jwtTokenService;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testGetWsdlPermitidoSinToken() {
        AuthTokenInterceptor interceptor = new AuthTokenInterceptor(jwtTokenService);
        SoapMessage message = mock(SoapMessage.class);
        when(message.get(Message.HTTP_REQUEST_METHOD)).thenReturn("GET");

        assertDoesNotThrow(() -> interceptor.handleMessage(message));
        verifyNoInteractions(jwtTokenService);
    }

    @Test
    void testSinTokenLanzaSoapFault() {
        AuthTokenInterceptor interceptor = new AuthTokenInterceptor(jwtTokenService);
        SoapMessage message = mock(SoapMessage.class);
        when(message.getHeaders()).thenReturn(null);

        assertThrows(SoapFault.class, () -> interceptor.handleMessage(message));
    }

    @Test
    void testTokenSoapHeaderValidoEstableceContexto() {
        AuthTokenInterceptor interceptor = new AuthTokenInterceptor(jwtTokenService);
        SoapMessage message = mock(SoapMessage.class);

        Header tokenHeader = new Header(new QName("http://example.com", "token"), "tok-soap-999");
        when(message.getHeaders()).thenReturn(List.of(tokenHeader));

        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("tok-soap-999");
        sesion.setIdCiudadano(3);
        sesion.setPerfil("REFUGIO");
        sesion.setIdRefugio(1);
        when(jwtTokenService.validarToken("tok-soap-999")).thenReturn(Optional.of(sesion));

        assertDoesNotThrow(() -> interceptor.handleMessage(message));
        assertNotNull(UserContextHolder.get());
        assertEquals("REFUGIO", UserContextHolder.get().getPerfil());
    }

    @Test
    void testBearerProtocolHeaderValidoEstableceContexto() {
        AuthTokenInterceptor interceptor = new AuthTokenInterceptor(jwtTokenService);
        SoapMessage message = mock(SoapMessage.class);

        when(message.get(Message.HTTP_REQUEST_METHOD)).thenReturn("POST");
        when(message.getHeaders()).thenReturn(List.of());
        when(message.get(Message.PROTOCOL_HEADERS))
                .thenReturn(Map.of("Authorization", List.of("Bearer tok-http-888")));

        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("tok-http-888");
        sesion.setIdCiudadano(6);
        sesion.setPerfil("MUNICIPALIDAD");
        when(jwtTokenService.validarToken("tok-http-888")).thenReturn(Optional.of(sesion));

        assertDoesNotThrow(() -> interceptor.handleMessage(message));
        assertEquals("MUNICIPALIDAD", UserContextHolder.get().getPerfil());

        interceptor.handleFault(message);
        assertNull(UserContextHolder.get());
    }
}
