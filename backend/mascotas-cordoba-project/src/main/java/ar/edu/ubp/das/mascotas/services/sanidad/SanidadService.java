package ar.edu.ubp.das.mascotas.services.sanidad;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.sanidad.AtencionSanitariaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.CarnetSanitarioBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.RegistrarAtencionRequestBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.ValidacionCarnetBE;
import ar.edu.ubp.das.mascotas.repositories.sanidad.SanidadRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * RF09 (Información Sanitaria), RF10 (Generar Carnet) y RF11 (Consultar y Validar Carnet).
 */
@Service
public class SanidadService implements ISanidadService {

    @Autowired
    private SanidadRepository sanidadRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public AtencionSanitariaBE registrarAtencion(RegistrarAtencionRequestBE request) {
        authorizationService.requirePerfil(PerfilUsuario.VETERINARIA, PerfilUsuario.MUNICIPALIDAD);
        // TODO(RF09): Validar práctica del programa, autoría de veterinaria e idempotencia.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF09 - Registrar Información Sanitaria pendiente de implementación"
        );
    }

    @Override
    public CarnetSanitarioBE generarCarnet(int nroRegMunicipal) {
        authorizationService.requireAuthenticated();
        // TODO(RF10): Emitir carnet digital único vigente e invalidar códigos anteriores.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF10 - Generar Carnet Sanitario Digital pendiente de implementación"
        );
    }

    @Override
    public CarnetSanitarioBE consultarCarnet(int nroRegMunicipal) {
        authorizationService.requireAuthenticated();
        // TODO(RF11): Retornar CarnetSanitarioBE verificando pertenencia/permisos del solicitante.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF11 - Consultar Carnet Sanitario Digital pendiente de implementación"
        );
    }

    @Override
    public ValidacionCarnetBE validarCarnet(String codigoValidacion) {
        // TODO(RF11): Verificar autenticidad y vigencia para terceros autorizados / consulta pública.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF11 - Validar Carnet Sanitario Digital pendiente de implementación"
        );
    }
}
