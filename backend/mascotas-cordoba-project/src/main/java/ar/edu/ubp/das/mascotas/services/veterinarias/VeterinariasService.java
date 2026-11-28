package ar.edu.ubp.das.mascotas.services.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.veterinarias.VeterinariaPublicaBE;
import ar.edu.ubp.das.mascotas.repositories.veterinarias.VeterinariasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RF17 - Consultar Veterinarias Adheridas.
 * Solo expone veterinarias habilitadas con información vigente de atención.
 */
@Service
public class VeterinariasService implements IVeterinariasService {

    @Autowired
    private VeterinariasRepository veterinariasRepository;

    @Override
    public PageResponseBE<VeterinariaPublicaBE> consultarVeterinarias(String filtro, PageRequestBE pageRequest) {
        PageRequestBE req = pageRequest != null ? pageRequest : new PageRequestBE();
        String filtroLimpio = filtro != null && !filtro.trim().isEmpty() ? filtro.trim() : null;

        int total = veterinariasRepository.countVeterinariasHabilitadas(filtroLimpio);
        List<VeterinariaPublicaBE> items = total > 0
                ? veterinariasRepository.getVeterinariasHabilitadas(filtroLimpio, req.getOffset(), req.getTamanioPagina())
                : List.of();
        return PageResponseBE.of(items, req, total);
    }
}
