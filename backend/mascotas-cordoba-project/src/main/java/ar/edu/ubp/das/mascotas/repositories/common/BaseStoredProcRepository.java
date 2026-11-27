package ar.edu.ubp.das.mascotas.repositories.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilidades base para repositorios JDBC que invocan stored procedures de SQL Server.
 * Cachea los {@link SimpleJdbcCall} compilados por nombre de procedimiento para evitar
 * repetir configuración e introspección de metadatos en cada invocación.
 */
public abstract class BaseStoredProcRepository {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private final ConcurrentHashMap<String, SimpleJdbcCall> procCallCache = new ConcurrentHashMap<>();

    protected <T> List<T> queryProcList(String sqlExec, Class<T> type, String errorMessage, Object... args) {
        try {
            return jdbcTemplate.query(sqlExec, new BeanPropertyRowMapper<>(type), args);
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos(errorMessage, ex);
        }
    }

    protected <T> Optional<T> queryProcOptional(String sqlExec, Class<T> type, String errorMessage, Object... args) {
        return queryProcList(sqlExec, type, errorMessage, args).stream().findFirst();
    }

    protected int queryForInt(String sql, String errorMessage, Object... args) {
        try {
            Integer value = jdbcTemplate.queryForObject(sql, Integer.class, args);
            return value != null ? value : 0;
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos(errorMessage, ex);
        }
    }

    protected Map<String, Object> executeProc(
            String procedureName,
            SqlParameter[] declaredParameters,
            Map<String, Object> inParams,
            String errorMessage) {
        try {
            SimpleJdbcCall call = procCallCache.computeIfAbsent(procedureName, name ->
                    new SimpleJdbcCall(jdbcTemplate)
                            .withProcedureName(name)
                            .withoutProcedureColumnMetaDataAccess()
                            .declareParameters(declaredParameters)
            );
            return call.execute(inParams);
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos(errorMessage, ex);
        }
    }

    protected static ResponseStatusException errorBaseDeDatos(String mensaje, DataAccessException ex) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, mensaje + ": " + ex.getMessage(), ex);
    }
}
