package ar.edu.ubp.das.mascotas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${security.rest.api-key:${security.api-key:}}")
    private String configuredApiKey;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Permitir solicitudes preflight CORS OPTIONS sin bloquear
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            validarApiKey(request);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // Delega la excepción al GlobalExceptionHandler (@RestControllerAdvice)
            resolver.resolveException(request, response, null, ex);
        }
    }

    /**
     * Valida la presencia y exactitud de la API Key requerida.
     * Lanza ResponseStatusException (UNAUTHORIZED) si la clave no coincide o no está provista.
     */
    private void validarApiKey(HttpServletRequest request) {
        // Si no está configurada la clave en el servidor, se permite el paso (modo desarrollo)
        if (configuredApiKey == null || configuredApiKey.trim().isEmpty()) {
            return;
        }

        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = request.getHeader("API-Key");
        }

        if (apiKey == null || apiKey.trim().isEmpty() || !configuredApiKey.equals(apiKey.trim())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API Key inválida o no provista");
        }
    }

}
