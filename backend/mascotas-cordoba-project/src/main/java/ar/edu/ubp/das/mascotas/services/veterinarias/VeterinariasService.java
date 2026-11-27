package ar.edu.ubp.das.mascotas.services.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.veterinarias.VeterinariaPublicaBE;
import ar.edu.ubp.das.mascotas.repositories.veterinarias.VeterinariasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * RF17 - Consultar Veterinarias Adheridas.
 */
@Service
public class VeterinariasService implements IVeterinariasService {

    @Autowired
    private VeterinariasRepository veterinariasRepository;

    @Override
    public PageResponseBE<VeterinariaPublicaBE> consultarVeterinarias(String filtro, PageRequestBE pageRequest) {
        // TODO(RF17): Consultar veterinarias habilitadas con filtros por zona/servicio y paginación.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF17 - Consultar Veterinarias Adheridas pendiente de implementación"
        );
    }
}
