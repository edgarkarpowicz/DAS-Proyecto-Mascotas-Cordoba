package ar.edu.ubp.das.mascotas.services.mascotas;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.RegistrarMascotaRequestBE;

/**
 * Contrato de negocio para Registro Único (RF06) y Mis Mascotas (RF20).
 */
public interface IMascotasService {

    MascotaBE registrarMascota(RegistrarMascotaRequestBE request);

    PageResponseBE<MascotaBE> consultarMisMascotas(PageRequestBE pageRequest);
}
