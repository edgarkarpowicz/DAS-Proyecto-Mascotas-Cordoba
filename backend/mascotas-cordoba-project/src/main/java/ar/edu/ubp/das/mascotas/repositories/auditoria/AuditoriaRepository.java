package ar.edu.ubp.das.mascotas.repositories.auditoria;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Registra eventos de trazabilidad con identidad del usuario en dbo.auditoria_operaciones (RF15 / RNF08).
 */
@Repository
public class AuditoriaRepository {

    private static final int MAX_CANAL = 20;
    private static final int MAX_OPERACION = 150;
    private static final int MAX_RESULTADO = 30;
    private static final int MAX_DETALLE = 500;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void registrarOperacion(
            SesionUsuarioBE sesion,
            String canal,
            String operacion,
            String resultado,
            String detalle) {

        Integer idCiudadano = sesion != null ? sesion.getIdCiudadano() : null;
        String cuil = sesion != null ? sesion.getCuil() : null;
        String perfil = sesion != null ? sesion.getPerfil() : null;

        jdbcTemplate.update(
                "exec dbo.registrar_auditoria_operacion ?, ?, ?, ?, ?, ?, ?",
                idCiudadano,
                cuil,
                perfil,
                truncar(canal, MAX_CANAL, "DESCONOCIDO"),
                truncar(operacion, MAX_OPERACION, "OPERACION"),
                truncar(resultado, MAX_RESULTADO, "DESCONOCIDO"),
                truncar(detalle, MAX_DETALLE, null)
        );
    }

    private String truncar(String valor, int maxLen, String fallback) {
        if (valor == null) {
            return fallback;
        }
        String trimmed = valor.trim();
        if (trimmed.isEmpty()) {
            return fallback;
        }
        return trimmed.length() > maxLen ? trimmed.substring(0, maxLen) : trimmed;
    }
}

