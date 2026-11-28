package ar.edu.ubp.das.mascotas.services.refugios;

import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.refugios.RefugioPublicoBE;

import java.util.List;

/**
 * Contrato público para consulta de refugios adheridos y sus adopciones activas (RF18).
 */
public interface IRefugiosService {

    PageResponseBE<RefugioPublicoBE> consultarRefugios(String filtro, PageRequestBE pageRequest);

    List<PublicacionAdopcionBE> consultarPublicacionesActivas(int idRefugio);
}
