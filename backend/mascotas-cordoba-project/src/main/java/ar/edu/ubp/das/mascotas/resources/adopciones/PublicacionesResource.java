package ar.edu.ubp.das.mascotas.resources.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/refugios")
@CrossOrigin(origins = "*")
public class PublicacionesResource {

    @Autowired
    private PublicacionesRepository publicacionesRepository;

    private static final List<String> ESTADOS_VALIDOS = Arrays.asList("Activa", "Pausada", "Finalizada");

    /**
     * RF13 - Obtiene todas las publicaciones de adopción administradas por el refugio.
     */
    @GetMapping("/{idRefugio}/publicaciones")
    public ResponseEntity<List<PublicacionAdopcionBE>> getPublicaciones(
            @PathVariable int idRefugio) {
        return ResponseEntity.ok(
                publicacionesRepository.getPublicacionesPorRefugio(idRefugio)
        );
    }

    /**
     * RF13 - Operación: AdministrarPublicacion() [Alta de publicación]
     * Permite al refugio registrar una nueva publicación de adopción.
     */
    @PostMapping("/publicaciones")
    public ResponseEntity<AdministrarPublicacionResponseBE> crearPublicacion(
            @RequestHeader(value = "X-API-Key", required = false) String apiKeyHeader,
            @RequestBody NuevaPublicacionRequestBE request) {

        if (request == null || request.getIdRefugio() == null) {
            return ResponseEntity.ok(
                    AdministrarPublicacionResponseBE.error("El identificador del refugio es obligatorio")
            );
        }

        if ((request.getNroRegMunicipal() == null || request.getNroRegMunicipal() <= 0) &&
                (request.getNombreMascota() == null || request.getNombreMascota().trim().isEmpty())) {
            return ResponseEntity.ok(
                    AdministrarPublicacionResponseBE.error("Debe especificar una mascota existente o ingresar los datos de una nueva mascota")
            );
        }

        try {
            int nroPublicacion = publicacionesRepository.crearPublicacion(request);

            if (nroPublicacion > 0) {
                return ResponseEntity.ok(
                        AdministrarPublicacionResponseBE.exitoso("Publicación de adopción creada exitosamente", nroPublicacion)
                );
            } else {
                return ResponseEntity.ok(
                        AdministrarPublicacionResponseBE.error("No se pudo registrar la publicación de adopción")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.ok(
                    AdministrarPublicacionResponseBE.error("Error al procesar la publicación: " + e.getMessage())
            );
        }
    }

    /**
     * RF13 - Operación: AdministrarPublicacion() [Cambio de estado]
     * Permite transicionar el estado de una publicación entre: Activa, Pausada y Finalizada.
     */
    @PutMapping("/publicaciones/{nroPublicacion}/estado")
    public ResponseEntity<AdministrarPublicacionResponseBE> actualizarEstado(
            @PathVariable int nroPublicacion,
            @RequestBody CambioEstadoRequestBE request) {

        if (request == null || request.getIdRefugio() == null || request.getNuevoEstado() == null) {
            return ResponseEntity.ok(
                    AdministrarPublicacionResponseBE.error("El identificador del refugio y el nuevo estado son obligatorios")
            );
        }

        String nuevoEstado = request.getNuevoEstado().trim();
        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            return ResponseEntity.ok(
                    AdministrarPublicacionResponseBE.error("Estado no válido. Los estados permitidos son: Activa, Pausada, Finalizada")
            );
        }

        boolean actualizado = publicacionesRepository.actualizarEstado(
                nroPublicacion,
                request.getIdRefugio(),
                nuevoEstado
        );

        if (actualizado) {
            return ResponseEntity.ok(
                    AdministrarPublicacionResponseBE.actualizado("Estado de publicación actualizado a " + nuevoEstado, nroPublicacion)
            );
        } else {
            return ResponseEntity.ok(
                    AdministrarPublicacionResponseBE.error("No se encontró la publicación o no pertenece al refugio indicado")
            );
        }
    }

    /**
     * RF13 - Consulta mascotas del refugio disponibles para crear nuevas publicaciones.
     */
    @GetMapping("/{idRefugio}/mascotas-disponibles")
    public ResponseEntity<List<MascotaDisponibleBE>> getMascotasDisponibles(
            @PathVariable int idRefugio) {
        return ResponseEntity.ok(
                publicacionesRepository.getMascotasDisponibles(idRefugio)
        );
    }

}
