package ar.edu.ubp.das.mascotas.config;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Utilidades compartidas por los filtros HTTP de seguridad para inspección de ruta y encabezados.
 */
final class SecurityHttpSupport {

    private static final String BEARER_PREFIX = "Bearer ";

    private SecurityHttpSupport() {
    }

    static String resolveRequestPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        if (requestPath == null) {
            return "";
        }
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        return requestPath;
    }

    static boolean shouldBypassRestSecurity(HttpServletRequest request, String requestPath) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return requestPath.startsWith("/services") || requestPath.startsWith("/uploads");
    }

    static Optional<String> extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }
        String trimmed = header.trim();
        if (trimmed.length() <= BEARER_PREFIX.length()
                || !trimmed.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return Optional.empty();
        }
        String token = trimmed.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }
}

