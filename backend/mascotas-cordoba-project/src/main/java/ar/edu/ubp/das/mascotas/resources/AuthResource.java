package ar.edu.ubp.das.mascotas.resources;

import ar.edu.ubp.das.mascotas.BE.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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

        if (request == null || request.getCuil() == null || request.getClave() == null ||
                request.getCuil().trim().isEmpty() || request.getClave().trim().isEmpty()) {
            return ResponseEntity.ok(
                    LoginResponseBE.error("El CUIL y la clave son obligatorios para iniciar sesión")
            );
        }

        Optional<UsuarioRefugioBE> usuarioOpt = authRepository.autenticarRefugio(
                request.getCuil().trim(),
                request.getClave().trim()
        );

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.ok(
                    LoginResponseBE.error("Credenciales inválidas o el usuario no pertenece a un refugio habilitado")
            );
        }

        UsuarioRefugioBE usuario = usuarioOpt.get();

        // Generar token único de sesión (32 caracteres hexadecimales)
        String token = UUID.randomUUID().toString().replace("-", "");

        return ResponseEntity.ok(
                LoginResponseBE.exitoso(token, usuario)
        );
    }

}
