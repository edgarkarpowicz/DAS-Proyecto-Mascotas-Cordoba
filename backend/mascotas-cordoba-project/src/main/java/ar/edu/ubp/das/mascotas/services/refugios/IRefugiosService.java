package ar.edu.ubp.das.mascotas.services.refugios;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.refugios.RefugioPublicoBE;

/**
 * Contrato público para consulta de refugios adheridos (RF18).
 */
public interface IRefugiosService {

    PageResponseBE<RefugioPublicoBE> consultarRefugios(String filtro, PageRequestBE pageRequest);
}
