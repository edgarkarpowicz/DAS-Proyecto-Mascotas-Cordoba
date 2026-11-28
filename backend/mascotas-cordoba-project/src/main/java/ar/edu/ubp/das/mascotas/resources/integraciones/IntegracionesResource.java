package ar.edu.ubp.das.mascotas.resources.integraciones;

import ar.edu.ubp.das.mascotas.BE.integraciones.LoteSincronizacionBE;
import ar.edu.ubp.das.mascotas.BE.integraciones.ResultadoSincronizacionVeterinariaBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaConfiguracionBE;
import ar.edu.ubp.das.mascotas.services.integraciones.SincronizacionVeterinariasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Recurso REST municipal para consultar y disparar sincronizaciones por lotes con veterinarias (RF06 / RF09).
 */
@RestController
@RequestMapping("/integraciones/veterinarias")
public class IntegracionesResource {

    @Autowired
    private SincronizacionVeterinariasService sincronizacionService;

    @GetMapping
    public ResponseEntity<List<VeterinariaConfiguracionBE>> listarClinicas() {
        return ResponseEntity.ok(sincronizacionService.listarClinicas());
    }

    @GetMapping("/lotes")
    public ResponseEntity<List<LoteSincronizacionBE>> listarUltimosLotes(
            @RequestParam(defaultValue = "20") int top) {
        return ResponseEntity.ok(sincronizacionService.listarUltimosLotes(top));
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<List<ResultadoSincronizacionVeterinariaBE>> sincronizar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Integer idVeterinaria) {
        return ResponseEntity.ok(sincronizacionService.sincronizar(fechaDesde, fechaHasta, idVeterinaria));
    }
}
