package ar.edu.ubp.das.mascotas.services.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * RF15 - Autenticación de usuarios.
 *
 * Capa de negocio que usan el recurso REST (AuthResource) y el endpoint SOAP:
 * valida los datos de ingreso, autentica contra el padrón, emite el JWT con el
 * perfil y el contexto organizacional, y revoca el token al cerrar sesión.
 */
@Service
@WebService(
        endpointInterface = "ar.edu.ubp.das.mascotas.services.auth.IAuthService",
        targetNamespace = "http://auth.services.mascotas.das.ubp.edu.ar/"
)
public class AuthService implements IAuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public LoginResponseBE login(LoginRequestBE request) {
        if (request == null || esVacio(request.getCuil()) || esVacio(request.getClave())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El CUIL y la clave son obligatorios para iniciar sesión");
        }

        UsuarioRefugioBE usuario = authRepository.autenticarUsuario(
                request.getCuil().trim(),
                request.getClave().trim()
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas o usuario inhabilitado"));

        String token = jwtTokenService.generarToken(usuario);
        // El usuario recién autenticado queda como contexto para la auditoría de esta operación.
        UserContextHolder.set(jwtTokenService.construirSesion(token, usuario));
        return LoginResponseBE.exitoso(token, usuario);
    }

    @Override
    public LoginResponseBE logout(String token) {
        if (esVacio(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El token de sesión es obligatorio para cerrar sesión");
        }
        String limpio = token.trim();
        SesionUsuarioBE sesion = jwtTokenService.validarToken(limpio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Token de sesión inválido, revocado o expirado"));
        jwtTokenService.revocarToken(limpio);
        return LoginResponseBE.exitoso(null, sesion.toUsuarioRefugioBE());
    }

    private static boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

}
