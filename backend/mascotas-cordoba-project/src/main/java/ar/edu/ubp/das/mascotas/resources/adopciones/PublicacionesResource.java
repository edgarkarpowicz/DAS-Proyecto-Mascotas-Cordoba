package ar.edu.ubp.das.mascotas.resources.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.*;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.adopciones.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/refugios")
public class PublicacionesResource {

    @Autowired
    private PublicacionesRepository publicacionesRepository;

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
     * Retorna la lista dinámica de estados configurados en la base de datos.
     */
    @GetMapping("/publicaciones/estados")
    public ResponseEntity<List<EstadoPublicacionBE>> getEstadosPublicacion() {
        authorizationService.requireAuthenticated();
        return ResponseEntity.ok(
                publicacionesRepository.getEstadosPublicacion()
        );
    }

    /**
     * RF13 / RF06 - Catálogo de especies disponibles desde el dominio de rasgos (cod_rasgo = 1).
     * Retorna la lista dinámica de especies configuradas en la base de datos.
     */
    @GetMapping({"/especies", "/publicaciones/especies"})
    public ResponseEntity<List<EspecieBE>> getEspecies() {
        authorizationService.requireAuthenticated();
        return ResponseEntity.ok(
                publicacionesRepository.getEspecies()
        );
    }

    /**
     * RF13 - Obtiene todas las publicaciones de adopción administradas por el refugio.
     */
    @GetMapping("/{idRefugio}/publicaciones")
    public ResponseEntity<List<PublicacionAdopcionBE>> getPublicaciones(
            @PathVariable int idRefugio) {
        SesionUsuarioBE sesion = authorizationService.requireRefugio(idRefugio > 0 ? idRefugio : null);
        validarIdRefugio(idRefugio);
        return ResponseEntity.ok(
                publicacionesRepository.getPublicacionesPorRefugio(sesion.getIdRefugio())
        );
    }

    /**
     * RF13 - Operación: AdministrarPublicacion() [Alta de publicación]
     * Permite al refugio registrar una nueva publicación de adopción.
     */
    @PostMapping("/publicaciones")
    public ResponseEntity<AdministrarPublicacionResponseBE> crearPublicacion(
            @RequestBody NuevaPublicacionRequestBE request) {

        Integer requestedRefugio = (request != null && request.getIdRefugio() != null && request.getIdRefugio() > 0)
                ? request.getIdRefugio()
                : null;
        SesionUsuarioBE sesion = authorizationService.requireRefugio(requestedRefugio);

        validarNuevaPublicacion(request);
        request.setIdRefugio(sesion.getIdRefugio());
        if (request.getCuilPublicante() == null || request.getCuilPublicante().isBlank()) {
            request.setCuilPublicante(sesion.getCuil());
        }

        int nroPublicacion = publicacionesRepository.crearPublicacion(request);

        return ResponseEntity.ok(
                AdministrarPublicacionResponseBE.exitoso("Publicación de adopción creada exitosamente", nroPublicacion)
        );
    }

    /**
     * RF13 - Operación: AdministrarPublicacion() [Cambio de estado]
     * Permite transicionar el estado de una publicación según los estados válidos registrados en la base de datos.
     */
    @PutMapping("/publicaciones/{nroPublicacion}/estado")
    public ResponseEntity<AdministrarPublicacionResponseBE> actualizarEstado(
            @PathVariable int nroPublicacion,
            @RequestBody CambioEstadoRequestBE request) {

        Integer requestedRefugio = (request != null && request.getIdRefugio() != null && request.getIdRefugio() > 0)
                ? request.getIdRefugio()
                : null;
        SesionUsuarioBE sesion = authorizationService.requireRefugio(requestedRefugio);

        validarActualizarEstado(nroPublicacion, request);
        request.setIdRefugio(sesion.getIdRefugio());

        String nuevoEstado = request.getNuevoEstado().trim();

        boolean actualizado = publicacionesRepository.actualizarEstado(
                nroPublicacion,
                sesion.getIdRefugio(),
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
        SesionUsuarioBE sesion = authorizationService.requireRefugio(idRefugio > 0 ? idRefugio : null);
        validarIdRefugio(idRefugio);
        return ResponseEntity.ok(
                publicacionesRepository.getMascotasDisponibles(sesion.getIdRefugio())
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

        try {
            PublicacionesRepository.parseFechaNacimiento(request.getFechaNacimiento());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        if (request.getEstadoPublicacion() != null && !request.getEstadoPublicacion().trim().isEmpty()) {
            String estado = request.getEstadoPublicacion().trim();
            if (!publicacionesRepository.esEstadoValido(estado)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estado no válido para la nueva publicación: " + estado
                );
            }
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
        if (!publicacionesRepository.esEstadoValido(nuevoEstado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado no válido para la publicación: " + nuevoEstado
            );
        }
    }

}
