package ar.edu.ubp.das.mascotas.services.mascotas;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.RegistrarMascotaRequestBE;
import ar.edu.ubp.das.mascotas.repositories.mascotas.MascotasRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * RF06 (Registrar Mascota) y RF20 (Consultar Mis Mascotas).
 * Estructura base lista para implementar reglas de deduplicación, asignación de NRM
 * y consulta filtrada por ciudadano autenticado.
 */
@Service
public class MascotasService implements IMascotasService {

    @Autowired
    private MascotasRepository mascotasRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public MascotaBE registrarMascota(RegistrarMascotaRequestBE request) {
        authorizationService.requirePerfil(
                PerfilUsuario.VETERINARIA,
                PerfilUsuario.MUNICIPALIDAD,
                PerfilUsuario.REFUGIO
        );
        // TODO(RF06): Validar datos, deduplicar por microchip/coincidencia y asignar o reutilizar NRM.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF06 - Registrar Mascota pendiente de implementación"
        );
    }

    @Override
    public PageResponseBE<MascotaBE> consultarMisMascotas(PageRequestBE pageRequest) {
        authorizationService.requirePerfil(PerfilUsuario.CIUDADANO);
        // TODO(RF20): Consultar mascotas del ciudadano autenticado usando su sesión y paginación estándar.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF20 - Consultar Mis Mascotas pendiente de implementación"
        );
    }
}
