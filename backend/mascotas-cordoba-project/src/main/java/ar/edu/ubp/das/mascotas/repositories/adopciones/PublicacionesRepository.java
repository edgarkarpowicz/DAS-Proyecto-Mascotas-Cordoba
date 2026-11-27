package ar.edu.ubp.das.mascotas.repositories.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.EspecieBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.EstadoPublicacionBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.MascotaDisponibleBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Acceso a datos de publicaciones de adopción. No contiene reglas de negocio:
 * las validaciones, las transiciones de estado y el orden de las grabaciones
 * los decide PublicacionesService, que invoca estos métodos dentro de una
 * transacción cuando graba más de una cosa.
 */
@Repository
public class PublicacionesRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtiene el listado de publicaciones de adopción de un refugio.
     */
    public List<PublicacionAdopcionBE> getPublicacionesPorRefugio(int idRefugio) {
        try {
            return jdbcTemplate.query(
                    "exec dbo.get_publicaciones_refugio ?",
                    new BeanPropertyRowMapper<>(PublicacionAdopcionBE.class),
                    idRefugio
            );
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error al consultar publicaciones del refugio en la base de datos", ex);
        }
    }

    /**
     * Obtiene una publicación del refugio con su estado actual.
     */
    public Optional<PublicacionAdopcionBE> getPublicacion(int nroPublicacion, int idRefugio) {
        try {
            List<PublicacionAdopcionBE> resultado = jdbcTemplate.query(
                    "exec dbo.get_publicacion_refugio ?, ?",
                    new BeanPropertyRowMapper<>(PublicacionAdopcionBE.class),
                    nroPublicacion,
                    idRefugio
            );
            return resultado.stream().findFirst();
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error al consultar la publicación en la base de datos", ex);
        }
    }

    /**
     * Da de alta una mascota del refugio en el Registro Único (RF06) y devuelve
     * el número de registro municipal asignado.
     */
    public int registrarMascota(int idRefugio, String nombre, String sexo, int añoNacimiento,
                                String especie, String raza) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("ins_mascota_refugio")
                    .declareParameters(
                            new SqlParameter("id_refugio", Types.INTEGER),
                            new SqlParameter("nombre", Types.VARCHAR),
                            new SqlParameter("sexo", Types.CHAR),
                            new SqlParameter("año_nacimiento", Types.INTEGER),
                            new SqlParameter("especie", Types.VARCHAR),
                            new SqlParameter("raza", Types.VARCHAR),
                            new SqlOutParameter("nro_reg_municipal", Types.INTEGER)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("id_refugio", idRefugio);
            params.put("nombre", nombre);
            params.put("sexo", sexo);
            params.put("año_nacimiento", añoNacimiento);
            params.put("especie", especie);
            params.put("raza", raza);

            Number nrm = (Number) jdbcCall.execute(params).get("nro_reg_municipal");
            if (nrm == null || nrm.intValue() <= 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo registrar la mascota");
            }
            return nrm.intValue();
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al registrar la mascota", ex);
        }
    }

    /**
     * Inserta una publicación de adopción para una mascota ya registrada y
     * devuelve el número de publicación.
     */
    public int insertarPublicacion(int idRefugio, int nroRegMunicipal, LocalDate fechaPublicacion,
                                   String caracteristicasMascota, String condicionAdopcion,
                                   String foto, String estadoPublicacion) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("ins_publicacion_adopcion")
                    .declareParameters(
                            new SqlParameter("id_refugio", Types.INTEGER),
                            new SqlParameter("nro_reg_municipal", Types.INTEGER),
                            new SqlParameter("fecha_publicacion", Types.DATE),
                            new SqlParameter("caracteristicas_mascota", Types.VARCHAR),
                            new SqlParameter("condicion_adopcion", Types.VARCHAR),
                            new SqlParameter("foto", Types.VARCHAR),
                            new SqlParameter("estado_publicacion", Types.VARCHAR),
                            new SqlOutParameter("nro_publicacion", Types.INTEGER)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("id_refugio", idRefugio);
            params.put("nro_reg_municipal", nroRegMunicipal);
            params.put("fecha_publicacion", fechaPublicacion != null ? Date.valueOf(fechaPublicacion) : null);
            params.put("caracteristicas_mascota", caracteristicasMascota);
            params.put("condicion_adopcion", condicionAdopcion);
            params.put("foto", foto);
            params.put("estado_publicacion", estadoPublicacion);

            Number nroPub = (Number) jdbcCall.execute(params).get("nro_publicacion");
            if (nroPub == null || nroPub.intValue() <= 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo registrar la publicación de adopción");
            }
            return nroPub.intValue();
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al crear publicación", ex);
        }
    }

    /**
     * Actualiza el estado de una publicación del refugio. Devuelve false si no
     * existe o pertenece a otro refugio.
     */
    public boolean actualizarEstado(int nroPublicacion, int idRefugio, String nuevoEstado) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("upd_estado_publicacion_adopcion")
                    .declareParameters(
                            new SqlParameter("nro_publicacion", Types.INTEGER),
                            new SqlParameter("id_refugio", Types.INTEGER),
                            new SqlParameter("nuevo_estado", Types.VARCHAR),
                            new SqlOutParameter("filas_afectadas", Types.INTEGER)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("nro_publicacion", nroPublicacion);
            params.put("id_refugio", idRefugio);
            params.put("nuevo_estado", nuevoEstado);

            Number filas = (Number) jdbcCall.execute(params).get("filas_afectadas");
            return filas != null && filas.intValue() > 0;
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al actualizar estado de la publicación", ex);
        }
    }

    /**
     * Cuenta las publicaciones Activas de una mascota, sin contar la indicada
     * (0 para no excluir ninguna). Bloquea las filas leídas hasta el fin de la
     * transacción para que dos operaciones simultáneas no dejen dos activas.
     */
    public int contarPublicacionesActivas(int nroRegMunicipal, int nroPublicacionExcluida) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM dbo.publicaciones_adopcion WITH (UPDLOCK, HOLDLOCK) " +
                    "WHERE nro_reg_municipal = ? AND estado_publicacion = 'Activa' AND nro_publicacion <> ?",
                    Integer.class,
                    nroRegMunicipal,
                    nroPublicacionExcluida
            );
            return count != null ? count : 0;
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al verificar publicaciones activas", ex);
        }
    }

    /**
     * Indica si la mascota está registrada bajo cuidado del refugio.
     */
    public boolean mascotaPerteneceARefugio(int nroRegMunicipal, int idRefugio) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM dbo.mascotas WHERE nro_reg_municipal = ? AND id_refugio = ? AND vive = 1",
                    Integer.class,
                    nroRegMunicipal,
                    idRefugio
            );
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al verificar la mascota del refugio", ex);
        }
    }

    /**
     * Retorna las mascotas bajo cuidado del refugio que no tienen publicaciones Activas.
     */
    public List<MascotaDisponibleBE> getMascotasDisponibles(int idRefugio) {
        try {
            return jdbcTemplate.query(
                    "exec dbo.get_mascotas_disponibles_refugio ?",
                    new BeanPropertyRowMapper<>(MascotaDisponibleBE.class),
                    idRefugio
            );
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error al consultar mascotas disponibles en la base de datos", ex);
        }
    }

    /**
     * Retorna todos los estados válidos configurados en la base de datos para publicaciones de adopción.
     */
    public List<EstadoPublicacionBE> getEstadosPublicacion() {
        try {
            return jdbcTemplate.query(
                    "exec dbo.get_estados_publicacion",
                    new BeanPropertyRowMapper<>(EstadoPublicacionBE.class)
            );
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al obtener los estados de publicación", ex);
        }
    }

    /**
     * Retorna el catálogo dinámico de especies configuradas en el dominio de rasgos (cod_rasgo = 1).
     */
    public List<EspecieBE> getEspecies() {
        try {
            return jdbcTemplate.query(
                    "exec dbo.get_especies_mascotas",
                    new BeanPropertyRowMapper<>(EspecieBE.class)
            );
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al obtener el catálogo de especies", ex);
        }
    }

    /**
     * Verifica contra la base de datos si un código de estado existe.
     */
    public boolean esEstadoValido(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return false;
        }
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM dbo.estados_publicacion WHERE cod_estado = ?",
                    Integer.class,
                    estado.trim()
            );
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            throw errorBaseDeDatos("Error de base de datos al validar el estado de la publicación", ex);
        }
    }

    private static ResponseStatusException errorBaseDeDatos(String mensaje, DataAccessException ex) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, mensaje + ": " + ex.getMessage(), ex);
    }

}
