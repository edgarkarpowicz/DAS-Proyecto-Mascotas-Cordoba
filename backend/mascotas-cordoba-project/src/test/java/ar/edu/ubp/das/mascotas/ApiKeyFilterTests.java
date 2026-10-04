package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.config.ApiKeyFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyFilterTests {

    @InjectMocks
    private ApiKeyFilter apiKeyFilter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HandlerExceptionResolver resolver;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apiKeyFilter, "configuredApiKey", "test-secret-key-123");
    }

    @Test
    void testPreflightOptionsPermitido() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/refugios/1/publicaciones");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(resolver, never()).resolveException(any(), any(), any(), any());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testApiKeyValidaPermitido() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/1/publicaciones");
        request.addHeader("X-API-Key", "test-secret-key-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(resolver, never()).resolveException(any(), any(), any(), any());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testApiKeyInvalidaDelegaAResolver401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/1/publicaciones");
        request.addHeader("X-API-Key", "wrong-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(resolver, times(1)).resolveException(eq(request), eq(response), isNull(), captor.capture());

        assertInstanceOf(ResponseStatusException.class, captor.getValue());
        ResponseStatusException ex = (ResponseStatusException) captor.getValue();
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("API Key inválida o no provista", ex.getReason());
    }

    @Test
    void testApiKeyAusenteDelegaAResolver401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/1/publicaciones");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(resolver, times(1)).resolveException(eq(request), eq(response), isNull(), captor.capture());

        assertInstanceOf(ResponseStatusException.class, captor.getValue());
        ResponseStatusException ex = (ResponseStatusException) captor.getValue();
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("API Key inválida o no provista", ex.getReason());
    }

    @Test
    void testApiKeyNoConfiguradaPermitePaso() throws ServletException, IOException {
        ReflectionTestUtils.setField(apiKeyFilter, "configuredApiKey", "");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/1/publicaciones");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(resolver, never()).resolveException(any(), any(), any(), any());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

}
