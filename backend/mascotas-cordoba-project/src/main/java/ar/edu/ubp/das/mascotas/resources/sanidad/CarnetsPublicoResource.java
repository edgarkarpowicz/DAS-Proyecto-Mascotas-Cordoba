package ar.edu.ubp.das.mascotas.resources.sanidad;

import ar.edu.ubp.das.mascotas.BE.sanidad.ValidacionCarnetBE;
import ar.edu.ubp.das.mascotas.services.sanidad.ISanidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recurso REST público de validación de carnets sanitarios digitales (RF11).
 */
@RestController
@RequestMapping("/publico/carnets")
public class CarnetsPublicoResource {

    @Autowired
    private ISanidadService sanidadService;

    @GetMapping("/{codigoValidacion}/validar")
    public ResponseEntity<ValidacionCarnetBE> validarCarnet(@PathVariable String codigoValidacion) {
        return ResponseEntity.ok(sanidadService.validarCarnet(codigoValidacion));
    }
}
