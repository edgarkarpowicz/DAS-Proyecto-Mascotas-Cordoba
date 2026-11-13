package ar.edu.ubp.das.mascotas.services.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        if (request == null ||
                request.getCuil() == null || request.getCuil().trim().isEmpty() ||
                request.getClave() == null || request.getClave().trim().isEmpty()) {
            return LoginResponseBE.error("El CUIL y la clave son obligatorios para iniciar sesión");
        }

        Optional<UsuarioRefugioBE> usuarioOpt = authRepository.autenticarUsuario(
                request.getCuil().trim(),
                request.getClave().trim()
        );

        if (usuarioOpt.isEmpty()) {
            return LoginResponseBE.error("Credenciales inválidas o usuario inhabilitado");
        }

        UsuarioRefugioBE usuario = usuarioOpt.get();
        String token = jwtTokenService.generarToken(usuario);
        return LoginResponseBE.exitoso(token, usuario);
    }

    @Override
    public LoginResponseBE logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return LoginResponseBE.error("El token de sesión es obligatorio para cerrar sesión");
        }
        String trimmed = token.trim();
        Optional<SesionUsuarioBE> sesionOpt = jwtTokenService.validarToken(trimmed);
        if (sesionOpt.isEmpty()) {
            return LoginResponseBE.error("Token de sesión inválido, revocado o expirado");
        }
        jwtTokenService.revocarToken(trimmed);
        return LoginResponseBE.exitoso(null, sesionOpt.get().toUsuarioRefugioBE());
    }

}
