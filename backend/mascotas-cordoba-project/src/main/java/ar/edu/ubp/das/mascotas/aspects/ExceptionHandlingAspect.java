package ar.edu.ubp.das.mascotas.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class ExceptionHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingAspect.class);

    /** Evita registrar la misma excepción dos veces cuando un recurso delega en un servicio. */
    private static final ThreadLocal<Integer> PROFUNDIDAD = ThreadLocal.withInitial(() -> 0);

    @Around("execution(* ar.edu.ubp.das.mascotas.resources..*(..)) || execution(* ar.edu.ubp.das.mascotas.services..*(..))")
    public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        PROFUNDIDAD.set(PROFUNDIDAD.get() + 1);
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            if (PROFUNDIDAD.get() == 1) {
                String tipo = joinPoint.getSignature().getDeclaringTypeName();
                String metodo = joinPoint.getSignature().getName();
                if (e instanceof ResponseStatusException rse && rse.getStatusCode().is4xxClientError()) {
                    logger.warn("Error de negocio en {}.{}: {} ({})", tipo, metodo, rse.getReason(), rse.getStatusCode());
                } else {
                    logger.error("Error en el metodo: {}.{}. Detalle: {}", tipo, metodo, e.getMessage(), e);
                }
            }
            throw e;
        } finally {
            int restante = PROFUNDIDAD.get() - 1;
            if (restante <= 0) {
                PROFUNDIDAD.remove();
            } else {
                PROFUNDIDAD.set(restante);
            }
        }
    }

}
