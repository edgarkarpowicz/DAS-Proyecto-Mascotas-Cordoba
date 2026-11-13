package ar.edu.ubp.das.mascotas.resources.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * RF15 - Autenticar Usuarios (CIUDADANO, REFUGIO, VETERINARIA, MUNICIPALIDAD)
     * Emite un JWT firmado (HS256) con la identidad, perfil y contexto organizacional del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseBE> login(
            @RequestHeader(value = "X-API-Key", required = false) String apiKeyHeader,
            @RequestBody LoginRequestBE request) {

        validarLoginRequest(request);

        UsuarioRefugioBE usuario = authRepository.autenticarUsuario(
                request.getCuil().trim(),
                request.getClave().trim()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas o usuario inhabilitado"
        ));

        String token = jwtTokenService.generarToken(usuario);
        UserContextHolder.set(jwtTokenService.construirSesion(token, usuario));

        return ResponseEntity.ok(
                LoginResponseBE.exitoso(token, usuario)
        );
    }

    /**
     * RF15 - Retorna la identidad y perfil del usuario autenticado por el token Bearer actual.
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioRefugioBE> getUsuarioActual() {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        return ResponseEntity.ok(sesion.toUsuarioRefugioBE());
    }

    /**
     * RF15 - Revoca en memoria el identificador jti del JWT actual al cerrar sesión.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        jwtTokenService.revocarToken(sesion.getToken());

        Map<String, Object> response = new HashMap<>();
        response.put("codigoRespuesta", 1);
        response.put("mensajeRespuesta", "Sesión cerrada exitosamente");
        return ResponseEntity.ok(response);
    }

    private void validarLoginRequest(LoginRequestBE request) {
        if (request == null ||
                request.getCuil() == null || request.getCuil().trim().isEmpty() ||
                request.getClave() == null || request.getClave().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El CUIL y la clave son obligatorios para iniciar sesión");
        }
    }

}
