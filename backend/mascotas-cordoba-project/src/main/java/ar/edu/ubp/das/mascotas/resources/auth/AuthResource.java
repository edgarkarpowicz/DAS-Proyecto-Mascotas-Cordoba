package ar.edu.ubp.das.mascotas.resources.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthResource {

    @Autowired
    private AuthRepository authRepository;

    /**
     * RF15 - Autenticar Usuarios (Perfil Refugio)
     * Operación: AutenticarUsuario()
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseBE> login(
            @RequestHeader(value = "X-API-Key", required = false) String apiKeyHeader,
            @RequestBody LoginRequestBE request) {

        validarLoginRequest(request);

        UsuarioRefugioBE usuario = authRepository.autenticarRefugio(
                request.getCuil().trim(),
                request.getClave().trim()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas o el usuario no pertenece a un refugio habilitado"
        ));

        // Generar token único de sesión (32 caracteres hexadecimales)
        String token = UUID.randomUUID().toString().replace("-", "");

        return ResponseEntity.ok(
                LoginResponseBE.exitoso(token, usuario)
        );
    }

    private void validarLoginRequest(LoginRequestBE request) {
        if (request == null ||
                request.getCuil() == null || request.getCuil().trim().isEmpty() ||
                request.getClave() == null || request.getClave().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El CUIL y la clave son obligatorios para iniciar sesión");
        }
    }

}
