package ar.edu.ubp.das.mascotas.BE.auth;

import java.util.Optional;

/**
 * Perfiles de usuario soportados por la plataforma municipal (RF15).
 */
public enum PerfilUsuario {
    CIUDADANO,
    REFUGIO,
    VETERINARIA,
    MUNICIPALIDAD;

    public static Optional<PerfilUsuario> fromString(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(PerfilUsuario.valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}

