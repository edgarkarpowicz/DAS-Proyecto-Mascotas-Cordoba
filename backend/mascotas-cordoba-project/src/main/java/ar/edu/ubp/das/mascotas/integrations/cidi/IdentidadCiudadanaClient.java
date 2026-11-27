package ar.edu.ubp.das.mascotas.integrations.cidi;

/**
 * Puerto de integración con Ciudadano Digital (CiDi) para autenticación ciudadana (RF15).
 */
public interface IdentidadCiudadanaClient {

    ValidacionIdentidadResultBE validarIdentidadCiudadana(String tokenExterno);
}
