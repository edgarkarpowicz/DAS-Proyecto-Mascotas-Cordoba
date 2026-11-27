package ar.edu.ubp.das.mascotas.resources.asistente;

import ar.edu.ubp.das.mascotas.BE.asistente.ConsultaAsistenteRequestBE;
import ar.edu.ubp.das.mascotas.BE.asistente.RespuestaAsistenteBE;
import ar.edu.ubp.das.mascotas.services.asistente.IAsistenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recurso REST público/híbrido del Asistente Virtual (RF21).
 */
@RestController
@RequestMapping("/publico/asistente")
public class AsistenteResource {

    @Autowired
    private IAsistenteService asistenteService;

    @PostMapping("/consultar")
    public ResponseEntity<RespuestaAsistenteBE> consultar(@RequestBody ConsultaAsistenteRequestBE request) {
        return ResponseEntity.ok(asistenteService.consultar(request));
    }
}
