package ar.edu.ubp.das.mascotas.repositories.refugios;

import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import ar.edu.ubp.das.mascotas.BE.refugios.RefugioPublicoBE;
import ar.edu.ubp.das.mascotas.repositories.common.BaseStoredProcRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a datos públicos de refugios adheridos y sus publicaciones activas (RF18).
 */
@Repository
public class RefugiosRepository extends BaseStoredProcRepository {

    public List<RefugioPublicoBE> getRefugiosPublicos(String filtro, int offset, int limit) {
        return queryProcList(
                "exec dbo.get_refugios_publicos ?, ?, ?",
                RefugioPublicoBE.class,
                "Error de base de datos al consultar refugios adheridos",
                filtro,
                offset,
                limit
        );
    }

    public int countRefugiosPublicos(String filtro) {
        return queryForInt(
                "exec dbo.count_refugios_publicos ?",
                "Error de base de datos al contar refugios adheridos",
                filtro
        );
    }

    public List<PublicacionAdopcionBE> getPublicacionesActivasPorRefugio(int idRefugio) {
        return queryProcList(
                "exec dbo.get_publicaciones_activas_refugio ?",
                PublicacionAdopcionBE.class,
                "Error de base de datos al consultar publicaciones activas del refugio",
                idRefugio
        );
    }
}
