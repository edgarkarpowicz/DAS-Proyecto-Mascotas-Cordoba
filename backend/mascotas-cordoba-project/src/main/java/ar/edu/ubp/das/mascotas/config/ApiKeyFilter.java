package ar.edu.ubp.das.mascotas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${security.rest.api-key:${security.api-key:}}")
    private String configuredApiKey;

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

        // Si la clave no está configurada, permitir el paso (desarrollo flexible)
        if (configuredApiKey == null || configuredApiKey.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null) {
            apiKey = request.getHeader("API-Key");
        }

        if (!configuredApiKey.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"codigoRespuesta\": -1, \"mensajeRespuesta\": \"API Key inválida o no provista\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

}
