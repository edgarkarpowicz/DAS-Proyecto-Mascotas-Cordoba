package ar.edu.ubp.das.mascotas.repositories.sanidad;

import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.AtencionSanitariaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.CarnetSanitarioBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.TipoAtencionBE;
import ar.edu.ubp.das.mascotas.repositories.common.BaseStoredProcRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de atenciones sanitarias y carnet digital (RF09 / RF10 / RF11).
 */
@Repository
public class SanidadRepository extends BaseStoredProcRepository {

    public List<TipoAtencionBE> getTiposAtencion() {
        return queryProcList(
                "exec dbo.get_tipos_atencion_sanitaria",
                TipoAtencionBE.class,
                "Error al consultar el catálogo de prácticas sanitarias"
        );
    }

    public Optional<MascotaBE> findMascotaByNrm(int nroRegMunicipal) {
        return queryProcOptional(
                "exec dbo.get_mascota_por_nrm ?",
                MascotaBE.class,
                "Error al consultar la mascota por NRM",
                nroRegMunicipal
        );
    }

    public List<AtencionSanitariaBE> getAtencionesPorMascota(int nroRegMunicipal) {
        return queryProcList(
                "exec dbo.get_informacion_sanitaria_mascota ?",
                AtencionSanitariaBE.class,
                "Error al consultar el historial sanitario de la mascota",
                nroRegMunicipal
        );
    }

    public AtencionSanitariaBE registrarAtencion(int nroRegMunicipal,
                                                 LocalDate fechaAtencion,
                                                 int codTipoAtencion,
                                                 String detalleAtencion,
                                                 LocalDate fechaVencimiento,
                                                 int idVeterinaria,
                                                 Integer idProfesional,
                                                 String profesionalTexto,
                                                 Integer nroAtencionExterna,
                                                 Integer nroDetalleExterno) {
        return queryProcOptional(
                "exec dbo.ins_informacion_sanitaria ?, ?, ?, ?, ?, ?, ?, ?, ?, ?",
                AtencionSanitariaBE.class,
                "Error al registrar la práctica sanitaria en la base de datos",
                nroRegMunicipal,
                Date.valueOf(fechaAtencion),
                codTipoAtencion,
                detalleAtencion,
                fechaVencimiento != null ? Date.valueOf(fechaVencimiento) : null,
                idVeterinaria,
                idProfesional,
                profesionalTexto,
                nroAtencionExterna,
                nroDetalleExterno
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "El procedimiento almacenado no devolvió el registro sanitario insertado"
        ));
    }

    public Optional<CarnetSanitarioBE> emitirCarnet(int nroRegMunicipal, String codigoValidacion, Integer idEmisor) {
        return queryProcOptional(
                "exec dbo.emitir_carnet_sanitario ?, ?, ?",
                CarnetSanitarioBE.class,
                "Error al emitir el carnet sanitario digital",
                nroRegMunicipal,
                codigoValidacion,
                idEmisor
        );
    }

    public Optional<CarnetSanitarioBE> findCarnetActivoByNrm(int nroRegMunicipal) {
        return queryProcOptional(
                "exec dbo.get_carnet_activo_por_nrm ?",
                CarnetSanitarioBE.class,
                "Error al consultar el carnet sanitario vigente",
                nroRegMunicipal
        );
    }

    public Optional<CarnetSanitarioBE> findCarnetByCodigo(String codigoValidacion) {
        return queryProcOptional(
                "exec dbo.get_carnet_por_codigo ?",
                CarnetSanitarioBE.class,
                "Error al validar el código del carnet sanitario",
                codigoValidacion
        );
    }
}
