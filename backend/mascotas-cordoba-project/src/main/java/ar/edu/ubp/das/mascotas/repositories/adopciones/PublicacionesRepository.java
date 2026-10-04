package ar.edu.ubp.das.mascotas.repositories.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.MascotaDisponibleBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.NuevaPublicacionRequestBE;
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

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al consultar publicaciones del refugio en la base de datos: " + ex.getMessage(),
                    ex
            );
        }
    }

    /**
     * Inserta una nueva publicación de adopción.
     * Si la mascota no existía previamente, el SP ejecuta el alta en el Registro Único (RF06).
     */
    public int crearPublicacion(NuevaPublicacionRequestBE req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("ins_publicacion_adopcion")
                    .declareParameters(
                            new SqlParameter("id_refugio", Types.INTEGER),
                            new SqlParameter("nro_reg_municipal", Types.INTEGER),
                            new SqlParameter("nombre_mascota", Types.VARCHAR),
                            new SqlParameter("sexo", Types.CHAR),
                            new SqlParameter("año_nacimiento", Types.INTEGER),
                            new SqlParameter("especie", Types.VARCHAR),
                            new SqlParameter("raza", Types.VARCHAR),
                            new SqlParameter("fecha_publicacion", Types.DATE),
                            new SqlParameter("caracteristicas_mascota", Types.VARCHAR),
                            new SqlParameter("condicion_adopcion", Types.VARCHAR),
                            new SqlParameter("foto", Types.VARCHAR),
                            new SqlParameter("estado_publicacion", Types.VARCHAR),
                            new SqlOutParameter("nro_publicacion", Types.INTEGER)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("id_refugio", req.getIdRefugio());
            params.put("nro_reg_municipal", req.getNroRegMunicipal());
            params.put("nombre_mascota", req.getNombreMascota());
            params.put("sexo", req.getSexo() != null ? req.getSexo() : "M");
            params.put("año_nacimiento", req.getAñoNacimiento());
            params.put("especie", req.getEspecie());
            params.put("raza", req.getRaza());
            params.put("fecha_publicacion", req.getFechaPublicacion());
            params.put("caracteristicas_mascota", req.getCaracteristicasMascota());

            // Acepta condicionAdopcion o condicionSanitaria según el contrato
            String condicion = req.getCondicionAdopcion();
            if (condicion == null || condicion.trim().isEmpty()) {
                condicion = req.getCondicionSanitaria();
            }
            params.put("condicion_adopcion", condicion != null ? condicion : "Compromiso de cuidado y tenencia responsable");

            params.put("foto", req.getFoto());
            params.put("estado_publicacion", req.getEstadoPublicacion() != null ? req.getEstadoPublicacion() : "Activa");

            Map<String, Object> resultado = jdbcCall.execute(params);
            Number nroPub = (Number) resultado.get("nro_publicacion");
            if (nroPub == null || nroPub.intValue() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No se pudo registrar la publicación de adopción"
                );
            }
            return nroPub.intValue();
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al crear publicación: " + ex.getMessage(),
                    ex
            );
        }
    }

    /**
     * Actualiza el estado de una publicación ('Activa', 'Pausada', 'Finalizada').
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

            Map<String, Object> resultado = jdbcCall.execute(params);
            Number filas = (Number) resultado.get("filas_afectadas");
            return filas != null && filas.intValue() > 0;
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de base de datos al actualizar estado de la publicación: " + ex.getMessage(),
                    ex
            );
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
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al consultar mascotas disponibles en la base de datos: " + ex.getMessage(),
                    ex
            );
        }
    }

}
