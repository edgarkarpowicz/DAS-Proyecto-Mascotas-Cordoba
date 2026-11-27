package ar.edu.ubp.das.mascotas.repositories.portal;

import ar.edu.ubp.das.mascotas.BE.portal.ResumenProgramaBE;
import ar.edu.ubp.das.mascotas.repositories.common.BaseStoredProcRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a métricas públicas del Programa Municipal para el portal (RF16).
 */
@Repository
public class PortalRepository extends BaseStoredProcRepository {

    public ResumenProgramaBE getResumenPortal() {
        return queryProcOptional(
                "exec dbo.get_resumen_portal",
                ResumenProgramaBE.class,
                "Error de base de datos al consultar el resumen del portal"
        ).orElseGet(() -> new ResumenProgramaBE(0, 0, 0));
    }
}
