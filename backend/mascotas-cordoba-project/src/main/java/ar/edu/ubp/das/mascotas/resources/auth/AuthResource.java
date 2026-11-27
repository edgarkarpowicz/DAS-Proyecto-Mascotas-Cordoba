package ar.edu.ubp.das.mascotas.resources.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioBE;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.auth.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Recurso REST de autenticación (RF15). Solo adapta HTTP: la validación, la
 * autenticación y la emisión o revocación del JWT están en AuthService, el
 * mismo servicio que expone el endpoint SOAP.
 */
@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private IAuthService authService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * RF15 - Autenticar Usuarios (CIUDADANO, REFUGIO, VETERINARIA, MUNICIPALIDAD)
     * Emite un JWT firmado (HS256) con la identidad, perfil y contexto organizacional del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseBE> login(@RequestBody LoginRequestBE request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * RF15 - Retorna la identidad y perfil del usuario autenticado por el token Bearer actual.
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioBE> getUsuarioActual() {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        return ResponseEntity.ok(sesion.toUsuarioBE());
    }

    /**
     * RF15 - Revoca el JWT actual al cerrar sesión.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        authService.logout(sesion.getToken());

        Map<String, Object> response = new HashMap<>();
        response.put("codigoRespuesta", 1);
        response.put("mensajeRespuesta", "Sesión cerrada exitosamente");
        return ResponseEntity.ok(response);
    }

}
