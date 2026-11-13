package ar.edu.ubp.das.mascotas.aspects;

import ar.edu.ubp.das.mascotas.BE.adopciones.AdministrarPublicacionResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auditoria.AuditoriaRepository;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

/**
 * Aspecto de trazabilidad (RF15 / RNF08): registra identidad del usuario,
 * canal (REST/SOAP), operación y resultado tanto en logs como en dbo.auditoria_operaciones.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired(required = false)
    private AuditoriaRepository auditoriaRepository;

    @Pointcut("execution(* ar.edu.ubp.das.mascotas.resources..*(..)) || execution(* ar.edu.ubp.das.mascotas.services..*(..))")
    public void resourceMethods() {
    }

    @Before("resourceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        SesionUsuarioBE sesion = UserContextHolder.get();
        String identidad = sesion != null
                ? sesion.getCuil() + " [" + sesion.getPerfil() + "]"
                : "ANONIMO";
        logger.info("Audit - Usuario={} - Entrando en {}.{} con argumentos: {}",
                identidad,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                sanitizeArgs(joinPoint));
    }

    @AfterReturning(pointcut = "resourceMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        SesionUsuarioBE sesion = resolveSesion(result);
        String canal = resolveCanal(joinPoint);
        String operacion = resolveOperacion(joinPoint);
        String resultado = resolveResultado(result);
        String detalle = resolveDetalle(result);

        String identidad = sesion != null
                ? sesion.getCuil() + " [" + sesion.getPerfil() + "]"
                : "ANONIMO";
        logger.info("Audit - Usuario={} - Canal={} - Operacion={} - Resultado={}",
                identidad, canal, operacion, resultado);

        persistirAuditoria(sesion, canal, operacion, resultado, detalle);
    }

    @AfterThrowing(pointcut = "resourceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        SesionUsuarioBE sesion = UserContextHolder.get();
        String canal = resolveCanal(joinPoint);
        String operacion = resolveOperacion(joinPoint);
        String resultado = resolveErrorResultado(ex);
        String detalle = ex.getMessage();

        String identidad = sesion != null
                ? sesion.getCuil() + " [" + sesion.getPerfil() + "]"
                : "ANONIMO";
        logger.warn("Audit - Usuario={} - Canal={} - Operacion={} - Resultado={} - Motivo={}",
                identidad, canal, operacion, resultado, detalle);

        persistirAuditoria(sesion, canal, operacion, resultado, detalle);
    }

    private void persistirAuditoria(
            SesionUsuarioBE sesion,
            String canal,
            String operacion,
            String resultado,
            String detalle) {
        if (auditoriaRepository == null) {
            return;
        }
        try {
            auditoriaRepository.registrarOperacion(sesion, canal, operacion, resultado, detalle);
        } catch (Exception ex) {
            logger.warn("Audit - No se pudo persistir evento en dbo.auditoria_operaciones: {}", ex.getMessage());
        }
    }

    private SesionUsuarioBE resolveSesion(Object result) {
        SesionUsuarioBE sesion = UserContextHolder.get();
        if (sesion != null) {
            return sesion;
        }
        Object body = unwrapBody(result);
        if (body instanceof LoginResponseBE loginResp && loginResp.getUsuario() != null) {
            UsuarioRefugioBE u = loginResp.getUsuario();
            SesionUsuarioBE transientSesion = new SesionUsuarioBE();
            transientSesion.setIdCiudadano(u.getIdCiudadano());
            transientSesion.setCuil(u.getCuil());
            transientSesion.setPerfil(u.getPerfil());
            transientSesion.setIdRefugio(u.getIdRefugio());
            transientSesion.setIdVeterinaria(u.getIdVeterinaria());
            return transientSesion;
        }
        return null;
    }

    private Object unwrapBody(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getBody();
        }
        return result;
    }

    private String resolveCanal(JoinPoint joinPoint) {
        String pkg = joinPoint.getSignature().getDeclaringTypeName();
        return pkg.contains(".services.") ? "SOAP" : "REST";
    }

    private String resolveOperacion(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();
    }

    private String resolveResultado(Object result) {
        Object body = unwrapBody(result);
        if (body instanceof LoginResponseBE loginResp) {
            return loginResp.getCodigoRespuesta() >= 0 ? "EXITO" : "RECHAZADO";
        }
        if (body instanceof AdministrarPublicacionResponseBE pubResp) {
            return pubResp.getCodigoRespuesta() >= 0 ? "EXITO" : "RECHAZADO";
        }
        return "EXITO";
    }

    private String resolveDetalle(Object result) {
        Object body = unwrapBody(result);
        if (body instanceof LoginResponseBE loginResp) {
            return loginResp.getMensajeRespuesta();
        }
        if (body instanceof AdministrarPublicacionResponseBE pubResp) {
            return pubResp.getMensajeRespuesta();
        }
        return null;
    }

    private String resolveErrorResultado(Throwable ex) {
        if (ex instanceof ResponseStatusException rse) {
            int code = rse.getStatusCode().value();
            if (code == 401) {
                return "NO_AUTENTICADO";
            }
            if (code == 403) {
                return "DENEGADO";
            }
        }
        return "ERROR";
    }

    private String sanitizeArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "[]";
        }
        String methodName = joinPoint.getSignature().getName();
        if ("logout".equalsIgnoreCase(methodName)) {
            return "[token=***]";
        }
        return Arrays.toString(Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }
                    String str = String.valueOf(arg);
                    if (str.matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+")) {
                        return "***";
                    }
                    return arg;
                })
                .toArray());
    }

}
