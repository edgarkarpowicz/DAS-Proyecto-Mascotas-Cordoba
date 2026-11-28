package ar.edu.ubp.das.mascotas.services.sanidad;

import ar.edu.ubp.das.mascotas.BE.sanidad.AtencionSanitariaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.CarnetSanitarioBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.RegistrarAtencionRequestBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.ValidacionCarnetBE;

import java.util.List;

/**
 * Contrato de negocio para información sanitaria (RF09) y carnet digital (RF10 / RF11).
 */
public interface ISanidadService {

    AtencionSanitariaBE registrarAtencion(RegistrarAtencionRequestBE request);

    List<AtencionSanitariaBE> consultarAtenciones(int nroRegMunicipal);

    CarnetSanitarioBE generarCarnet(int nroRegMunicipal);

    CarnetSanitarioBE consultarCarnet(int nroRegMunicipal);

    ValidacionCarnetBE validarCarnet(String codigoValidacion);
}
