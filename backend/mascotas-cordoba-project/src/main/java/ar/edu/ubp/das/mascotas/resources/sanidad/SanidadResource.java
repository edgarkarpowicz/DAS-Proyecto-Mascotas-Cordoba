package ar.edu.ubp.das.mascotas.resources.sanidad;

import ar.edu.ubp.das.mascotas.BE.sanidad.AtencionSanitariaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.CarnetSanitarioBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.RegistrarAtencionRequestBE;
import ar.edu.ubp.das.mascotas.services.sanidad.ISanidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Recurso REST protegido de sanidad y carnets digitales (RF09, RF10 y RF11).
 */
@RestController
@RequestMapping("/sanidad")
public class SanidadResource {

    @Autowired
    private ISanidadService sanidadService;

    @PostMapping("/atenciones")
    public ResponseEntity<AtencionSanitariaBE> registrarAtencion(@RequestBody RegistrarAtencionRequestBE request) {
        return ResponseEntity.ok(sanidadService.registrarAtencion(request));
    }

    @GetMapping("/mascotas/{nroRegMunicipal}/atenciones")
    public ResponseEntity<List<AtencionSanitariaBE>> consultarAtenciones(@PathVariable int nroRegMunicipal) {
        return ResponseEntity.ok(sanidadService.consultarAtenciones(nroRegMunicipal));
    }

    @PostMapping("/carnets/{nroRegMunicipal}")
    public ResponseEntity<CarnetSanitarioBE> generarCarnet(@PathVariable int nroRegMunicipal) {
        return ResponseEntity.ok(sanidadService.generarCarnet(nroRegMunicipal));
    }

    @GetMapping("/carnets/{nroRegMunicipal}")
    public ResponseEntity<CarnetSanitarioBE> consultarCarnet(@PathVariable int nroRegMunicipal) {
        return ResponseEntity.ok(sanidadService.consultarCarnet(nroRegMunicipal));
    }
}
