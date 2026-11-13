package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.adopciones.AdministrarPublicacionResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.aspects.AuditAspect;
import ar.edu.ubp.das.mascotas.repositories.auditoria.AuditoriaRepository;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditAspectTests {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @InjectMocks
    private AuditAspect auditAspect;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testRegistraIdentidadUsuarioEnOperacionExitosa() {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setIdCiudadano(3);
        sesion.setCuil("20-25999000-5");
        sesion.setPerfil("REFUGIO");
        UserContextHolder.set(sesion);

        JoinPoint jp = mockJoinPoint("ar.edu.ubp.das.mascotas.resources.adopciones.PublicacionesResource", "actualizarEstado");

        auditAspect.logAfter(jp, ResponseEntity.ok(AdministrarPublicacionResponseBE.actualizado("Actualizada", 1)));

        verify(auditoriaRepository, times(1)).registrarOperacion(
                eq(sesion),
                eq("REST"),
                eq("PublicacionesResource.actualizarEstado"),
                eq("EXITO"),
                eq("Actualizada")
        );
    }

    @Test
    void testRegistraIdentidadDesdeLoginResponse() {
        UserContextHolder.clear();
        UsuarioRefugioBE usuario = new UsuarioRefugioBE();
        usuario.setIdCiudadano(6);
        usuario.setCuil("27-22333444-1");
        usuario.setPerfil("MUNICIPALIDAD");

        JoinPoint jp = mockJoinPoint("ar.edu.ubp.das.mascotas.services.auth.AuthService", "login");
        auditAspect.logAfter(jp, LoginResponseBE.exitoso("secret-token", usuario));

        verify(auditoriaRepository, times(1)).registrarOperacion(
                argThat(s -> s != null && Integer.valueOf(6).equals(s.getIdCiudadano()) && "MUNICIPALIDAD".equals(s.getPerfil())),
                eq("SOAP"),
                eq("AuthService.login"),
                eq("EXITO"),
                any()
        );
    }

    @Test
    void testRegistraDenegadoEnAfterThrowing403() {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setIdCiudadano(1);
        sesion.setCuil("20-28555666-3");
        sesion.setPerfil("CIUDADANO");
        UserContextHolder.set(sesion);

        JoinPoint jp = mockJoinPoint("ar.edu.ubp.das.mascotas.resources.adopciones.PublicacionesResource", "crearPublicacion");
        auditAspect.logAfterThrowing(jp, new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil no autorizado"));

        verify(auditoriaRepository, times(1)).registrarOperacion(
                eq(sesion),
                eq("REST"),
                eq("PublicacionesResource.crearPublicacion"),
                eq("DENEGADO"),
                contains("Perfil no autorizado")
        );
    }

    private JoinPoint mockJoinPoint(String fqcn, String methodName) {
        JoinPoint jp = mock(JoinPoint.class);
        Signature sig = mock(Signature.class);
        when(jp.getSignature()).thenReturn(sig);
        when(sig.getDeclaringTypeName()).thenReturn(fqcn);
        try {
            Class<?> clazz = Class.forName(fqcn);
            lenient().when(sig.getDeclaringType()).thenReturn(clazz);
        } catch (ClassNotFoundException ignored) {
        }
        when(sig.getName()).thenReturn(methodName);
        return jp;
    }
}

