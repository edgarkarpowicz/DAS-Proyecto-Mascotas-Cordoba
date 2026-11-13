package ar.edu.ubp.das.mascotas.config;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.SesionRepository;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

/**
 * Valida el token UUID enviado en Authorization: Bearer <token>,
 * carga el contexto de usuario en UserContextHolder y lo limpia al finalizar el request (RF15).
 */
@Component
@Order(2)
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String requestPath = SecurityHttpSupport.resolveRequestPath(request);

            // CORS, recursos estáticos y SOAP (/services/*) continúan su flujo sin token REST;
            // el finally garantiza igualmente la limpieza de UserContextHolder para SOAP y REST.
            if (SecurityHttpSupport.shouldBypassRestSecurity(request, requestPath)
                    || isPublicAuthEndpoint(request, requestPath)) {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<String> tokenOpt = SecurityHttpSupport.extractBearerToken(request);
            if (tokenOpt.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Token de sesión requerido en el encabezado Authorization: Bearer <token>"
                );
            }

            SesionUsuarioBE sesion = sesionRepository.validarSesion(tokenOpt.get())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Token de sesión inválido, revocado o expirado"
                    ));

            UserContextHolder.set(sesion);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            resolver.resolveException(request, response, null, ex);
        } finally {
            UserContextHolder.clear();
        }
    }

    private boolean isPublicAuthEndpoint(HttpServletRequest request, String requestPath) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && ("/auth/login".equals(requestPath) || "/error".equals(requestPath));
    }
}

