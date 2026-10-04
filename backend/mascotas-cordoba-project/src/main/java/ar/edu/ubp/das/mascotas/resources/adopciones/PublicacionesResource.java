package ar.edu.ubp.das.mascotas.resources.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        validarIdRefugio(idRefugio);
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

        validarNuevaPublicacion(request);

        int nroPublicacion = publicacionesRepository.crearPublicacion(request);

        return ResponseEntity.ok(
                AdministrarPublicacionResponseBE.exitoso("Publicación de adopción creada exitosamente", nroPublicacion)
        );
    }

    /**
     * RF13 - Operación: AdministrarPublicacion() [Cambio de estado]
     * Permite transicionar el estado de una publicación entre: Activa, Pausada y Finalizada.
     */
    @PutMapping("/publicaciones/{nroPublicacion}/estado")
    public ResponseEntity<AdministrarPublicacionResponseBE> actualizarEstado(
            @PathVariable int nroPublicacion,
            @RequestBody CambioEstadoRequestBE request) {

        validarActualizarEstado(nroPublicacion, request);

        String nuevoEstado = request.getNuevoEstado().trim();

        boolean actualizado = publicacionesRepository.actualizarEstado(
                nroPublicacion,
                request.getIdRefugio(),
                nuevoEstado
        );

        if (!actualizado) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró la publicación o no pertenece al refugio indicado"
            );
        }

        return ResponseEntity.ok(
                AdministrarPublicacionResponseBE.actualizado("Estado de publicación actualizado a " + nuevoEstado, nroPublicacion)
        );
    }

    /**
     * RF13 - Consulta mascotas del refugio disponibles para crear nuevas publicaciones.
     */
    @GetMapping("/{idRefugio}/mascotas-disponibles")
    public ResponseEntity<List<MascotaDisponibleBE>> getMascotasDisponibles(
            @PathVariable int idRefugio) {
        validarIdRefugio(idRefugio);
        return ResponseEntity.ok(
                publicacionesRepository.getMascotasDisponibles(idRefugio)
        );
    }

    private void validarIdRefugio(int idRefugio) {
        if (idRefugio <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador del refugio debe ser un número entero positivo");
        }
    }

    private void validarNuevaPublicacion(NuevaPublicacionRequestBE request) {
        if (request == null || request.getIdRefugio() == null || request.getIdRefugio() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador del refugio es obligatorio");
        }

        boolean tieneMascotaRegistrada = request.getNroRegMunicipal() != null && request.getNroRegMunicipal() > 0;
        boolean tieneNombreMascota = request.getNombreMascota() != null && !request.getNombreMascota().trim().isEmpty();

        if (!tieneMascotaRegistrada && !tieneNombreMascota) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe especificar una mascota existente o ingresar los datos de una nueva mascota");
        }
    }

    private void validarActualizarEstado(int nroPublicacion, CambioEstadoRequestBE request) {
        if (nroPublicacion <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de publicación debe ser un valor positivo");
        }

        if (request == null || request.getIdRefugio() == null || request.getNuevoEstado() == null ||
                request.getNuevoEstado().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador del refugio y el nuevo estado son obligatorios");
        }

        String nuevoEstado = request.getNuevoEstado().trim();
        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido. Los estados permitidos son: Activa, Pausada, Finalizada");
        }
    }

}
