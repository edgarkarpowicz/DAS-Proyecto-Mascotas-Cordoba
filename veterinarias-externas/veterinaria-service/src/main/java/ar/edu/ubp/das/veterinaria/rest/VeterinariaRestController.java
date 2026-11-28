package ar.edu.ubp.das.veterinaria.rest;

import ar.edu.ubp.das.veterinaria.model.NrmAcuseDTO;
import ar.edu.ubp.das.veterinaria.model.NrmAsignadoDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaMascotasDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaPracticasDTO;
import ar.edu.ubp.das.veterinaria.service.ClinicaStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contrato REST externo consumido por la Municipalidad (RF06 / RF09).
 */
@RestController
@RequestMapping("/api/veterinaria")
public class VeterinariaRestController {

    @Autowired
    private ClinicaStoreService storeService;

    @GetMapping("/mascotas-sin-nrm")
    public ResponseEntity<PaginaMascotasDTO> obtenerMascotasSinNrm(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanioPagina) {
        requireApiKey(apiKey);
        return ResponseEntity.ok(storeService.obtenerMascotasSinNrm(fechaDesde, fechaHasta, pagina, tamanioPagina));
    }

    @PostMapping("/nrm-asignados")
    public ResponseEntity<List<NrmAcuseDTO>> informarNrm(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @RequestBody(required = false) List<NrmAsignadoDTO> asignaciones) {
        requireApiKey(apiKey);
        return ResponseEntity.ok(storeService.informarNrm(asignaciones));
    }

    @GetMapping("/atenciones")
    public ResponseEntity<PaginaPracticasDTO> obtenerAtenciones(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanioPagina) {
        requireApiKey(apiKey);
        return ResponseEntity.ok(storeService.obtenerAtencionesSanitarias(fechaDesde, fechaHasta, pagina, tamanioPagina));
    }

    private void requireApiKey(String apiKey) {
        if (!storeService.isApiKeyValid(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API Key de veterinaria inválida");
        }
    }
}
