package ar.edu.ubp.das.mascotas.resources.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.adopciones.IPublicacionesService;
import ar.edu.ubp.das.mascotas.services.adopciones.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de publicaciones de adopción (RF13). Solo adapta HTTP: las
 * validaciones, las reglas de negocio y las transacciones están en
 * PublicacionesService, el mismo servicio que expone el endpoint SOAP.
 */
@RestController
@RequestMapping("/refugios")
public class PublicacionesResource {

    @Autowired
    private IPublicacionesService publicacionesService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * RF13 - Carga y almacenamiento local de fotos de mascotas para adopción.
     * Recibe un archivo de imagen (MultipartFile), valida y guarda en el disco del servidor.
     * Retorna la ruta relativa web del archivo (/uploads/publicaciones/...).
     */
    @PostMapping("/publicaciones/fotos")
    public ResponseEntity<Map<String, Object>> subirFoto(
            @RequestParam("archivo") MultipartFile archivo) {
        authorizationService.requireRefugio(null);
        String urlFoto = storageService.almacenarFoto(archivo);

        Map<String, Object> response = new HashMap<>();
        response.put("codigoRespuesta", 1);
        response.put("mensajeRespuesta", "Foto subida y almacenada exitosamente");
        response.put("urlFoto", urlFoto);

        return ResponseEntity.ok(response);
    }

    /**
     * RF13 - Catálogo de estados válidos para publicaciones de adopción.
     */
    @GetMapping("/publicaciones/estados")
    public ResponseEntity<List<EstadoPublicacionBE>> getEstadosPublicacion() {
        return ResponseEntity.ok(publicacionesService.getEstadosPublicacion());
    }

    /**
     * RF13 / RF06 - Catálogo de especies disponibles desde el dominio de rasgos (cod_rasgo = 1).
     */
    @GetMapping({"/especies", "/publicaciones/especies"})
    public ResponseEntity<List<EspecieBE>> getEspecies() {
        return ResponseEntity.ok(publicacionesService.getEspecies());
    }

    /**
     * RF13 - Obtiene todas las publicaciones de adopción administradas por el refugio.
     */
    @GetMapping("/{idRefugio}/publicaciones")
    public ResponseEntity<List<PublicacionAdopcionBE>> getPublicaciones(@PathVariable int idRefugio) {
        return ResponseEntity.ok(publicacionesService.getPublicacionesPorRefugio(idRefugio));
    }

    /**
     * RF13 - Operación: AdministrarPublicacion() [Alta de publicación]
     */
    @PostMapping("/publicaciones")
    public ResponseEntity<AdministrarPublicacionResponseBE> crearPublicacion(
            @RequestBody NuevaPublicacionRequestBE request) {
        return ResponseEntity.ok(publicacionesService.crearPublicacion(request));
    }

    /**
     * RF13 - Operación: AdministrarPublicacion() [Cambio de estado]
     */
    @PutMapping("/publicaciones/{nroPublicacion}/estado")
    public ResponseEntity<AdministrarPublicacionResponseBE> actualizarEstado(
            @PathVariable int nroPublicacion,
            @RequestBody CambioEstadoRequestBE request) {
        return ResponseEntity.ok(publicacionesService.actualizarEstado(nroPublicacion, request));
    }

    /**
     * RF13 - Consulta mascotas del refugio disponibles para crear nuevas publicaciones.
     */
    @GetMapping("/{idRefugio}/mascotas-disponibles")
    public ResponseEntity<List<MascotaDisponibleBE>> getMascotasDisponibles(@PathVariable int idRefugio) {
        return ResponseEntity.ok(publicacionesService.getMascotasDisponibles(idRefugio));
    }

}
