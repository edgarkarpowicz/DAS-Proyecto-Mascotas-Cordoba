package ar.edu.ubp.das.mascotas.repositories.integraciones;

import ar.edu.ubp.das.mascotas.BE.integraciones.LoteSincronizacionBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.NrmAsignadoBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaConfiguracionBE;
import ar.edu.ubp.das.mascotas.repositories.common.BaseStoredProcRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Persistencia de configuración técnica, mapeo de NRMs y bitácora de lotes de veterinarias.
 */
@Repository
public class SincronizacionRepository extends BaseStoredProcRepository {

    public List<VeterinariaConfiguracionBE> getVeterinariasIntegradas() {
        return queryProcList(
                "exec dbo.get_veterinarias_integradas",
                VeterinariaConfiguracionBE.class,
                "Error al consultar las configuraciones de integración de veterinarias"
        );
    }

    public Optional<Integer> getMapeoMascota(int idVeterinaria, String idMascotaExterna) {
        try {
            List<Integer> list = jdbcTemplate.queryForList(
                    "exec dbo.get_mapeo_mascota_veterinaria ?, ?",
                    Integer.class,
                    idVeterinaria,
                    idMascotaExterna
            );
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error al consultar mapeo de mascota externa", ex);
        }
    }

    public void upsertMapeo(int idVeterinaria, String idMascotaExterna, int nroRegMunicipal, boolean confirmado) {
        try {
            jdbcTemplate.update(
                    "exec dbo.upsert_mapeo_mascota_veterinaria ?, ?, ?, ?",
                    idVeterinaria,
                    idMascotaExterna,
                    nroRegMunicipal,
                    confirmado
            );
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error al registrar mapeo NRM de veterinaria", ex);
        }
    }

    public List<NrmAsignadoBE> getNrmsPendientes(int idVeterinaria) {
        return queryProcList(
                "exec dbo.get_nrms_pendientes_veterinaria ?",
                NrmAsignadoBE.class,
                "Error al consultar NRMs pendientes de confirmación",
                idVeterinaria
        );
    }

    public void marcarNrmConfirmado(int idVeterinaria, String idMascotaExterna) {
        try {
            jdbcTemplate.update(
                    "exec dbo.marcar_nrm_confirmado_veterinaria ?, ?",
                    idVeterinaria,
                    idMascotaExterna
            );
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error al confirmar acuse de NRM en base de datos", ex);
        }
    }

    public void registrarLote(int idVeterinaria,
                              String tipoLote,
                              LocalDate fechaDesde,
                              LocalDate fechaHasta,
                              String estado,
                              int registrosProcesados,
                              int registrosNuevos,
                              int registrosDeduplicados,
                              int registrosConfirmados,
                              String detalleError) {
        try {
            jdbcTemplate.update(
                    "exec dbo.ins_lote_veterinaria ?, ?, ?, ?, ?, ?, ?, ?, ?, ?",
                    idVeterinaria,
                    tipoLote,
                    Date.valueOf(fechaDesde),
                    Date.valueOf(fechaHasta),
                    estado,
                    registrosProcesados,
                    registrosNuevos,
                    registrosDeduplicados,
                    registrosConfirmados,
                    detalleError
            );
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error al registrar resultado del lote de sincronización", ex);
        }
    }

    public List<LoteSincronizacionBE> getUltimosLotes(int top) {
        return queryProcList(
                "exec dbo.get_ultimos_lotes_veterinaria ?",
                LoteSincronizacionBE.class,
                "Error al consultar el historial de lotes de sincronización",
                top
        );
    }
}
