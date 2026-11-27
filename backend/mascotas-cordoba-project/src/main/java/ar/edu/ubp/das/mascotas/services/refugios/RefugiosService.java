package ar.edu.ubp.das.mascotas.services.refugios;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.refugios.RefugioPublicoBE;
import ar.edu.ubp.das.mascotas.repositories.refugios.RefugiosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * RF18 - Consultar Refugios Adheridos.
 */
@Service
public class RefugiosService implements IRefugiosService {

    @Autowired
    private RefugiosRepository refugiosRepository;

    @Override
    public PageResponseBE<RefugioPublicoBE> consultarRefugios(String filtro, PageRequestBE pageRequest) {
        // TODO(RF18): Retornar refugios habilitados y resumen de publicaciones Activas sin exponer datos privados.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF18 - Consultar Refugios Adheridos pendiente de implementación"
        );
    }
}
