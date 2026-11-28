package ar.edu.ubp.das.mascotas.resources.refugios;

import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.refugios.RefugioPublicoBE;
import ar.edu.ubp.das.mascotas.services.refugios.IRefugiosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Recurso REST público de refugios adheridos y sus publicaciones activas (RF18).
 */
@RestController
@RequestMapping("/publico/refugios")
public class RefugiosResource {

    @Autowired
    private IRefugiosService refugiosService;

    @GetMapping
    public ResponseEntity<PageResponseBE<RefugioPublicoBE>> getRefugios(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanioPagina) {
        return ResponseEntity.ok(
                refugiosService.consultarRefugios(filtro, PageRequestBE.of(pagina, tamanioPagina))
        );
    }

    @GetMapping("/{idRefugio}/publicaciones")
    public ResponseEntity<List<PublicacionAdopcionBE>> getPublicacionesActivas(@PathVariable int idRefugio) {
        return ResponseEntity.ok(refugiosService.consultarPublicacionesActivas(idRefugio));
    }
}
