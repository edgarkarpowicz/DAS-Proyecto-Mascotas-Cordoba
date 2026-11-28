package ar.edu.ubp.das.mascotas.resources.catalogos;

import ar.edu.ubp.das.mascotas.BE.adopciones.EspecieBE;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Catálogos públicos compartidos del programa municipal.
 */
@RestController
@RequestMapping("/publico/catalogos")
public class CatalogosPublicoResource {

    @Autowired
    private PublicacionesRepository publicacionesRepository;

    @GetMapping("/especies")
    public ResponseEntity<List<EspecieBE>> getEspecies() {
        return ResponseEntity.ok(publicacionesRepository.getEspecies());
    }
}
