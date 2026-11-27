package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.aspects.ExceptionHandlingAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionHandlingAspectTests {

    private final ExceptionHandlingAspect aspect = new ExceptionHandlingAspect();

    @Test
    void testLlamadaAnidadaRethrowsYDejaProfundidadLimpia() throws Throwable {
        ProceedingJoinPoint recurso = mockJoinPoint("PublicacionesResource", "actualizarEstado");
        ProceedingJoinPoint servicio = mockJoinPoint("PublicacionesService", "actualizarEstado");
        ResponseStatusException error = new ResponseStatusException(HttpStatus.CONFLICT, "Finalizada");

        when(servicio.proceed()).thenThrow(error);
        when(recurso.proceed()).thenAnswer(inv -> {
            try {
                return aspect.handleExceptions(servicio);
            } catch (Throwable t) {
                throw t;
            }
        });

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,
                () -> aspect.handleExceptions(recurso));
        assertSame(error, thrown);

        // Verificar que la profundidad quedó en 0 ejecutando otra llamada que falla sin quedar bloqueada
        ProceedingJoinPoint siguiente = mockJoinPoint("AuthResource", "login");
        when(siguiente.proceed()).thenReturn("ok");
        assertEquals("ok", aspect.handleExceptions(siguiente));
    }

    private ProceedingJoinPoint mockJoinPoint(String typeName, String methodName) {
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);
        Signature sig = mock(Signature.class);
        when(jp.getSignature()).thenReturn(sig);
        lenient().when(sig.getDeclaringTypeName()).thenReturn(typeName);
        lenient().when(sig.getName()).thenReturn(methodName);
        return jp;
    }

}
