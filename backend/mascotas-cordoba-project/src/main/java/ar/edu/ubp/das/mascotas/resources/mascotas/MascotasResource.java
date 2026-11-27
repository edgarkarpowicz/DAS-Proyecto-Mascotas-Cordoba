package ar.edu.ubp.das.mascotas.resources.mascotas;

import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.RegistrarMascotaRequestBE;
import ar.edu.ubp.das.mascotas.services.mascotas.IMascotasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recurso REST de mascotas (RF06 y RF20).
 */
@RestController
@RequestMapping("/mascotas")
public class MascotasResource {

    @Autowired
    private IMascotasService mascotasService;

    @PostMapping
    public ResponseEntity<MascotaBE> registrarMascota(@RequestBody RegistrarMascotaRequestBE request) {
        return ResponseEntity.ok(mascotasService.registrarMascota(request));
    }

    @GetMapping("/mias")
    public ResponseEntity<PageResponseBE<MascotaBE>> consultarMisMascotas(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanioPagina) {
        return ResponseEntity.ok(mascotasService.consultarMisMascotas(PageRequestBE.of(pagina, tamanioPagina)));
    }
}
