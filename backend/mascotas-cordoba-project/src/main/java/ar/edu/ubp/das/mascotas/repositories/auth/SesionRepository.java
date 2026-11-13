package ar.edu.ubp.das.mascotas.repositories.auth;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Persiste y valida tokens UUID de sesión en dbo.sesiones_usuario (RF15).
 */
@Repository
public class SesionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${security.session.expiration-minutes:480}")
    private int duracionMinutos = 480;

    public void crearSesion(String token, UsuarioRefugioBE usuario) {
        if (token == null || token.isBlank() || usuario == null || usuario.getIdCiudadano() == null) {
            throw new IllegalArgumentException("El token y los datos del usuario autenticado son obligatorios");
        }
        try {
            jdbcTemplate.update(
                    "exec dbo.crear_sesion_usuario ?, ?, ?, ?, ?, ?, ?",
                    token.trim(),
                    usuario.getIdCiudadano(),
                    usuario.getCuil(),
                    usuario.getPerfil(),
                    usuario.getIdRefugio(),
                    usuario.getIdVeterinaria(),
                    duracionMinutos
            );
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al registrar sesión de usuario: " + ex.getMessage(),
                    ex
            );
        }
    }

    public Optional<SesionUsuarioBE> validarSesion(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            List<SesionUsuarioBE> resultados = jdbcTemplate.query(
                    "exec dbo.validar_sesion_usuario ?",
                    new BeanPropertyRowMapper<>(SesionUsuarioBE.class),
                    token.trim()
            );
            if (resultados.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(resultados.get(0));
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al validar sesión de usuario: " + ex.getMessage(),
                    ex
            );
        }
    }

    public void revocarSesion(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            jdbcTemplate.update("exec dbo.revocar_sesion_usuario ?", token.trim());
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al revocar sesión de usuario: " + ex.getMessage(),
                    ex
            );
        }
    }
}

