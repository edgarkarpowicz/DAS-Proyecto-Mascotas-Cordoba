package ar.edu.ubp.das.mascotas.services.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.veterinarias.VeterinariaPublicaBE;

/**
 * Contrato público para consulta de veterinarias adheridas (RF17).
 */
public interface IVeterinariasService {

    PageResponseBE<VeterinariaPublicaBE> consultarVeterinarias(String filtro, PageRequestBE pageRequest);
}
