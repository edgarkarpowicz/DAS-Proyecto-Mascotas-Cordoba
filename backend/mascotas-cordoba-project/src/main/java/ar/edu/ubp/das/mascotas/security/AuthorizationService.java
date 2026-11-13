package ar.edu.ubp.das.mascotas.security;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

/**
 * Reglas centralizadas de identificación, control por perfil y aislamiento por contexto de sesión (RF15).
 */
@Service
public class AuthorizationService {

    public SesionUsuarioBE requireAuthenticated() {
        SesionUsuarioBE sesion = UserContextHolder.get();
        if (sesion == null || sesion.getIdCiudadano() == null || sesion.getPerfil() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión con un token válido para acceder a esta operación"
            );
        }
        return sesion;
    }

    public SesionUsuarioBE requirePerfil(PerfilUsuario... permitidos) {
        SesionUsuarioBE sesion = requireAuthenticated();
        boolean autorizado = permitidos != null && Arrays.stream(permitidos).anyMatch(sesion::esPerfil);
        if (!autorizado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El perfil '" + sesion.getPerfil() + "' no está autorizado para esta operación"
            );
        }
        return sesion;
    }

    public SesionUsuarioBE requireRefugio(Integer idRefugioSolicitado) {
        SesionUsuarioBE sesion = requirePerfil(PerfilUsuario.REFUGIO);
        if (sesion.getIdRefugio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario autenticado no tiene un refugio asociado en su sesión"
            );
        }
        if (idRefugioSolicitado != null && !idRefugioSolicitado.equals(sesion.getIdRefugio())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No está autorizado a operar sobre publicaciones de otro refugio"
            );
        }
        return sesion;
    }
}

