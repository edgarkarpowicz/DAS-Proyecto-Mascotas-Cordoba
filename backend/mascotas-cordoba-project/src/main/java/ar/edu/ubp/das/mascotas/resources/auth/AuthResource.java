package ar.edu.ubp.das.mascotas.resources.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.AuthRepository;
import ar.edu.ubp.das.mascotas.repositories.auth.SesionRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * RF15 - Autenticar Usuarios (CIUDADANO, REFUGIO, VETERINARIA, MUNICIPALIDAD)
     * Genera un token UUID persistido en base de datos para autorizar las operaciones posteriores.
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

        // Generar token único de sesión (32 caracteres hexadecimales) y persistirlo en BD
        String token = UUID.randomUUID().toString().replace("-", "");
        sesionRepository.crearSesion(token, usuario);

        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken(token);
        sesion.setIdCiudadano(usuario.getIdCiudadano());
        sesion.setCuil(usuario.getCuil());
        sesion.setPerfil(usuario.getPerfil());
        sesion.setIdRefugio(usuario.getIdRefugio());
        sesion.setIdVeterinaria(usuario.getIdVeterinaria());
        UserContextHolder.set(sesion);

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
     * RF15 - Revoca el token de sesión actual en la base de datos.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        sesionRepository.revocarSesion(sesion.getToken());

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
