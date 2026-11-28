package ar.edu.ubp.das.mascotas.services.refugios;

import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.refugios.RefugioPublicoBE;
import ar.edu.ubp.das.mascotas.repositories.refugios.RefugiosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * RF18 - Consultar Refugios Adheridos.
 */
@Service
public class RefugiosService implements IRefugiosService {

    @Autowired
    private RefugiosRepository refugiosRepository;

    @Override
    public PageResponseBE<RefugioPublicoBE> consultarRefugios(String filtro, PageRequestBE pageRequest) {
        PageRequestBE req = pageRequest != null ? pageRequest : new PageRequestBE();
        String filtroLimpio = filtro != null && !filtro.trim().isEmpty() ? filtro.trim() : null;

        int total = refugiosRepository.countRefugiosPublicos(filtroLimpio);
        List<RefugioPublicoBE> items = total > 0
                ? refugiosRepository.getRefugiosPublicos(filtroLimpio, req.getOffset(), req.getTamanioPagina())
                : List.of();
        return PageResponseBE.of(items, req, total);
    }

    @Override
    public List<PublicacionAdopcionBE> consultarPublicacionesActivas(int idRefugio) {
        if (idRefugio <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idRefugio debe ser mayor a cero");
        }
        return refugiosRepository.getPublicacionesActivasPorRefugio(idRefugio);
    }
}
