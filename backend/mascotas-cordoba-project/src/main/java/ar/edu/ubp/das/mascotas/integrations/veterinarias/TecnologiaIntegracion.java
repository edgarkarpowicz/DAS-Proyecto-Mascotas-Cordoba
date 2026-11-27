package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import java.util.Optional;

/**
 * Protocolos soportados para invocar el servicio de una veterinaria (RF04 / RNF09).
 */
public enum TecnologiaIntegracion {
    REST,
    SOAP;

    public static Optional<TecnologiaIntegracion> fromString(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(TecnologiaIntegracion.valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
