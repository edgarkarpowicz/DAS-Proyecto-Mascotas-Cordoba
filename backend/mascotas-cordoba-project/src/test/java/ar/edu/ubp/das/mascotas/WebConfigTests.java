package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.config.AuthTokenFilter;
import ar.edu.ubp.das.mascotas.config.WebConfig;
import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class WebConfigTests {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private HandlerExceptionResolver resolver;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Test
    void testCorsFilterSeRegistraConMaximaPrioridad() {
        WebConfig webConfig = new WebConfig();
        FilterRegistrationBean<CorsFilter> registration = webConfig.corsFilterRegistration();

        assertNotNull(registration.getFilter());
        assertEquals(Ordered.HIGHEST_PRECEDENCE, registration.getOrder());
        assertTrue(registration.getUrlPatterns().contains("/*"));
    }

    @Test
    void testCorsHeadersPresentesEnPreflightOptions() throws ServletException, IOException {
        CorsFilter corsFilter = new WebConfig().corsFilterRegistration().getFilter();
        assertNotNull(corsFilter);

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/refugios/1/publicaciones");
        request.addHeader(HttpHeaders.ORIGIN, "http://localhost:4200");
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET");
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,x-api-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());

        assertEquals(200, response.getStatus());
        assertEquals("http://localhost:4200", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("true", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void testCorsHeadersPresentesCuandoAuthTokenFilterResponde401() throws ServletException, IOException {
        doAnswer(invocation -> {
            MockHttpServletResponse resp = invocation.getArgument(1, MockHttpServletResponse.class);
            ResponseStatusException ex = invocation.getArgument(3, ResponseStatusException.class);
            resp.setStatus(ex.getStatusCode().value());
            return new ModelAndView();
        }).when(resolver).resolveException(any(), any(), any(), any());

        CorsFilter corsFilter = new WebConfig().corsFilterRegistration().getFilter();
        assertNotNull(corsFilter);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/refugios/publicaciones/estados");
        request.addHeader(HttpHeaders.ORIGIN, "http://localhost:4200");
        MockHttpServletResponse response = new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain(
                new HttpServlet() {
                    @Override
                    public void service(ServletRequest req, ServletResponse res) {
                        fail("El controlador no debe ejecutarse sin token");
                    }
                },
                corsFilter,
                authTokenFilter
        );
        chain.doFilter(request, response);

        assertEquals(401, response.getStatus());
        assertEquals("http://localhost:4200", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("true", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }
}
