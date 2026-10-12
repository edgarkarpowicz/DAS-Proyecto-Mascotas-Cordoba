package ar.edu.ubp.das.mascotas.services.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@WebService(
        endpointInterface = "ar.edu.ubp.das.mascotas.services.auth.IAuthService",
        targetNamespace = "http://auth.services.mascotas.das.ubp.edu.ar/"
)
public class AuthService implements IAuthService {

    @Autowired
    private AuthRepository authRepository;

    @Override
    public LoginResponseBE login(LoginRequestBE request) {
        if (request == null ||
                request.getCuil() == null || request.getCuil().trim().isEmpty() ||
                request.getClave() == null || request.getClave().trim().isEmpty()) {
            return LoginResponseBE.error("El CUIL y la clave son obligatorios para iniciar sesión");
        }

        Optional<UsuarioRefugioBE> usuarioOpt = authRepository.autenticarRefugio(
                request.getCuil().trim(),
                request.getClave().trim()
        );

        if (usuarioOpt.isEmpty()) {
            return LoginResponseBE.error("Credenciales inválidas o el usuario no pertenece a un refugio habilitado");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        return LoginResponseBE.exitoso(token, usuarioOpt.get());
    }

}
