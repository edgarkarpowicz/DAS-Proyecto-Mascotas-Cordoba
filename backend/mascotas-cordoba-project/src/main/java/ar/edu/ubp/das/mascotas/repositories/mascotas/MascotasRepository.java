package ar.edu.ubp.das.mascotas.repositories.mascotas;

import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.repositories.common.BaseStoredProcRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Acceso a datos del Registro Único de Mascotas (RF06 / RF20).
 */
@Repository
public class MascotasRepository extends BaseStoredProcRepository {

    private static final SqlParameter[] INS_MASCOTA_UNICA_PARAMS = {
            new SqlParameter("nombre", Types.VARCHAR),
            new SqlParameter("sexo", Types.CHAR),
            new SqlParameter("año_nacimiento", Types.INTEGER),
            new SqlParameter("microchip", Types.VARCHAR),
            new SqlParameter("castrado", Types.BIT),
            new SqlParameter("id_responsable", Types.INTEGER),
            new SqlParameter("id_refugio", Types.INTEGER),
            new SqlParameter("especie", Types.VARCHAR),
            new SqlParameter("raza", Types.VARCHAR),
            new SqlOutParameter("nro_reg_municipal", Types.INTEGER)
    };

    public List<MascotaBE> getMascotasCiudadano(int idCiudadano, int offset, int limit) {
        return queryProcList(
                "exec dbo.get_mascotas_ciudadano ?, ?, ?",
                MascotaBE.class,
                "Error de base de datos al consultar las mascotas del ciudadano",
                idCiudadano,
                offset,
                limit
        );
    }

    public int countMascotasCiudadano(int idCiudadano) {
        return queryForInt(
                "exec dbo.count_mascotas_ciudadano ?",
                "Error de base de datos al contar las mascotas del ciudadano",
                idCiudadano
        );
    }

    public Optional<Integer> buscarCiudadanoPorCuil(String cuil) {
        try {
            List<Integer> ids = jdbcTemplate.queryForList(
                    "exec dbo.buscar_ciudadano_por_cuil ?",
                    Integer.class,
                    cuil
            );
            return ids.stream().findFirst();
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al buscar ciudadano por CUIL", ex);
        }
    }

    public Optional<MascotaBE> buscarPorMicrochip(String microchip) {
        return queryProcOptional(
                "exec dbo.buscar_mascota_por_microchip ?",
                MascotaBE.class,
                "Error de base de datos al buscar mascota por microchip",
                microchip
        );
    }

    public Optional<MascotaBE> buscarPorCoincidencia(
            Integer idResponsable,
            Integer idRefugio,
            String nombre,
            String sexo,
            int añoNacimiento,
            String especie) {
        return queryProcOptional(
                "exec dbo.buscar_mascota_coincidencia ?, ?, ?, ?, ?, ?",
                MascotaBE.class,
                "Error de base de datos al verificar duplicados de mascota",
                idResponsable,
                idRefugio,
                nombre,
                sexo,
                añoNacimiento,
                especie
        );
    }

    public int insertarMascotaRegistroUnico(
            String nombre,
            String sexo,
            int añoNacimiento,
            String microchip,
            boolean castrado,
            Integer idResponsable,
            Integer idRefugio,
            String especie,
            String raza) {
        Map<String, Object> params = new HashMap<>();
        params.put("nombre", nombre);
        params.put("sexo", sexo);
        params.put("año_nacimiento", añoNacimiento);
        params.put("microchip", microchip);
        params.put("castrado", castrado);
        params.put("id_responsable", idResponsable);
        params.put("id_refugio", idRefugio);
        params.put("especie", especie);
        params.put("raza", raza);

        Map<String, Object> out = executeProc(
                "ins_mascota_registro_unico",
                INS_MASCOTA_UNICA_PARAMS,
                params,
                "Error de base de datos al registrar mascota en el Registro Único"
        );
        Number nrm = (Number) out.get("nro_reg_municipal");
        if (nrm == null || nrm.intValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo asignar NRM a la mascota");
        }
        return nrm.intValue();
    }
}
