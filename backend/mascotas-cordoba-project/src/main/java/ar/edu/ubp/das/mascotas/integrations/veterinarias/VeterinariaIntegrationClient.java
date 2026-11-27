package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;

import java.util.List;

/**
 * Puerto de integración con sistemas externos de gestión veterinaria (RF06 / RF09 / RNF09).
 */
public interface VeterinariaIntegrationClient {

    TecnologiaIntegracion getTecnologia();

    PageResponseBE<MascotaExternaBE> obtenerMascotasSinNrm(
            VeterinariaConfiguracionBE config,
            LoteRangoFechasRequestBE filtro
    );

    List<NrmAcuseBE> informarNrm(
            VeterinariaConfiguracionBE config,
            List<NrmAsignadoBE> asignaciones
    );

    PageResponseBE<PracticaSanitariaExternaBE> obtenerAtencionesSanitarias(
            VeterinariaConfiguracionBE config,
            LoteRangoFechasRequestBE filtro
    );
}
