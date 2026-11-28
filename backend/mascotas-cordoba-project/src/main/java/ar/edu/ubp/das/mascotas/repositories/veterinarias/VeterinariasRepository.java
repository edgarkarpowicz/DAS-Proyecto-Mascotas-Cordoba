package ar.edu.ubp.das.mascotas.repositories.veterinarias;

import ar.edu.ubp.das.mascotas.BE.veterinarias.VeterinariaPublicaBE;
import ar.edu.ubp.das.mascotas.repositories.common.BaseStoredProcRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a datos públicos de veterinarias adheridas (RF17).
 */
@Repository
public class VeterinariasRepository extends BaseStoredProcRepository {

    public List<VeterinariaPublicaBE> getVeterinariasHabilitadas(String filtro, int offset, int limit) {
        return queryProcList(
                "exec dbo.get_veterinarias_habilitadas ?, ?, ?",
                VeterinariaPublicaBE.class,
                "Error de base de datos al consultar veterinarias habilitadas",
                filtro,
                offset,
                limit
        );
    }

    public int countVeterinariasHabilitadas(String filtro) {
        return queryForInt(
                "exec dbo.count_veterinarias_habilitadas ?",
                "Error de base de datos al contar veterinarias habilitadas",
                filtro
        );
    }
}
