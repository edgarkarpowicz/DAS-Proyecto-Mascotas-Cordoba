package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.config.AuthTokenFilter;
import ar.edu.ubp.das.mascotas.repositories.auth.SesionRepository;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTests {

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private HandlerExceptionResolver resolver;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testLoginEsPublicoSinToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authTokenFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(sesionRepository, resolver);
        assertNull(UserContextHolder.get());
    }

    @Test
    void testEndpointProtegidoSinHeaderAuthorizationDelega401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/1/publicaciones");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authTokenFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(resolver).resolveException(eq(request), eq(response), isNull(), captor.capture());
        ResponseStatusException ex = assertInstanceOf(ResponseStatusException.class, captor.getValue());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testTokenInvalidoORevocadoDelega401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/1/publicaciones");
        request.addHeader("Authorization", "Bearer deadbeef1234");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(sesionRepository.validarSesion("deadbeef1234")).thenReturn(Optional.empty());

        authTokenFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(resolver).resolveException(eq(request), eq(response), isNull(), captor.capture());
        ResponseStatusException ex = assertInstanceOf(ResponseStatusException.class, captor.getValue());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertNull(UserContextHolder.get());
    }

    @Test
    void testTokenValidoCargaYLimpiaUserContextHolder() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/1/publicaciones");
        request.addHeader("Authorization", "Bearer tok-valido-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken("tok-valido-123");
        sesion.setIdCiudadano(3);
        sesion.setCuil("20-25999000-5");
        sesion.setPerfil("REFUGIO");
        sesion.setIdRefugio(1);

        when(sesionRepository.validarSesion("tok-valido-123")).thenReturn(Optional.of(sesion));

        AtomicReference<SesionUsuarioBE> capturadaEnChain = new AtomicReference<>();
        doAnswer(invocation -> {
            capturadaEnChain.set(UserContextHolder.get());
            return null;
        }).when(filterChain).doFilter(request, response);

        authTokenFilter.doFilter(request, response, filterChain);

        assertNotNull(capturadaEnChain.get());
        assertEquals("20-25999000-5", capturadaEnChain.get().getCuil());
        assertEquals("REFUGIO", capturadaEnChain.get().getPerfil());
        assertNull(UserContextHolder.get(), "Debe limpiar el ThreadLocal al terminar el request");
    }

    @Test
    void testServiciosSoapLimpiaContextoEnFinally() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/services/publicaciones");
        MockHttpServletResponse response = new MockHttpServletResponse();

        doAnswer(invocation -> {
            SesionUsuarioBE soapSesion = new SesionUsuarioBE();
            soapSesion.setIdCiudadano(3);
            soapSesion.setPerfil("REFUGIO");
            UserContextHolder.set(soapSesion);
            return null;
        }).when(filterChain).doFilter(request, response);

        authTokenFilter.doFilter(request, response, filterChain);

        assertNull(UserContextHolder.get());
    }
}

