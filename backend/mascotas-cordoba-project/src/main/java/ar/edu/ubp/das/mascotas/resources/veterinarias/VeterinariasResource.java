package ar.edu.ubp.das.mascotas.resources.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.veterinarias.VeterinariaPublicaBE;
import ar.edu.ubp.das.mascotas.services.veterinarias.IVeterinariasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recurso REST público de veterinarias adheridas (RF17).
 */
@RestController
@RequestMapping("/publico/veterinarias")
public class VeterinariasResource {

    @Autowired
    private IVeterinariasService veterinariasService;

    @GetMapping
    public ResponseEntity<PageResponseBE<VeterinariaPublicaBE>> getVeterinarias(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanioPagina) {
        return ResponseEntity.ok(
                veterinariasService.consultarVeterinarias(filtro, PageRequestBE.of(pagina, tamanioPagina))
        );
    }
}
