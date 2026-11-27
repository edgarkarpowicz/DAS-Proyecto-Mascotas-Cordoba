package ar.edu.ubp.das.mascotas.services.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.AdministrarPublicacionResponseBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.CambioEstadoRequestBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.EspecieBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.EstadoPublicacionBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.MascotaDisponibleBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.NuevaPublicacionRequestBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RF13 - Gestión de publicaciones de adopción.
 *
 * Es la capa de negocio que usan tanto el recurso REST (PublicacionesResource)
 * como el endpoint SOAP. Concentra las reglas:
 * - el refugio solo opera sobre sus publicaciones y sus mascotas;
 * - una mascota sin registro se da de alta en el Registro Único antes de publicarla;
 * - una mascota no puede tener dos publicaciones Activas;
 * - transiciones de estado: Activa ⇄ Pausada, Activa/Pausada → Finalizada.
 *   Finalizada es un estado final: no se reactiva.
 *
 * Las operaciones que graban son transaccionales: si falla un paso (por ejemplo
 * el alta de la publicación después del alta de la mascota) no queda nada a medias.
 */
@Service
@WebService(
        endpointInterface = "ar.edu.ubp.das.mascotas.services.adopciones.IPublicacionesService",
        targetNamespace = "http://adopciones.services.mascotas.das.ubp.edu.ar/"
)
public class PublicacionesService implements IPublicacionesService {

    static final String ACTIVA = "Activa";
    static final String PAUSADA = "Pausada";
    static final String FINALIZADA = "Finalizada";

    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
            ACTIVA, Set.of(PAUSADA, FINALIZADA),
            PAUSADA, Set.of(ACTIVA, FINALIZADA),
            FINALIZADA, Set.of()
    );

    /** Estados con los que se puede dar de alta una publicación. */
    private static final Set<String> ESTADOS_INICIALES = Set.of(ACTIVA, PAUSADA);

    private static final int LARGO_MAXIMO_NOMBRE = 64;
    private static final int ANTIGUEDAD_MAXIMA_ANIOS = 30;

    @Autowired
    private PublicacionesRepository publicacionesRepository;

    @Autowired
    private AuthorizationService authorizationService;

    // ------------------------------------------------------------- consultas

    @Override
    public List<EstadoPublicacionBE> getEstadosPublicacion() {
        authorizationService.requireAuthenticated();
        return publicacionesRepository.getEstadosPublicacion();
    }

    @Override
    public List<EspecieBE> getEspecies() {
        authorizationService.requireAuthenticated();
        return publicacionesRepository.getEspecies();
    }

    @Override
    public List<PublicacionAdopcionBE> getPublicacionesPorRefugio(int idRefugio) {
        SesionUsuarioBE sesion = authorizationService.requireRefugio(idRefugio > 0 ? idRefugio : null);
        validarIdRefugio(idRefugio);
        return publicacionesRepository.getPublicacionesPorRefugio(sesion.getIdRefugio());
    }

    @Override
    public List<MascotaDisponibleBE> getMascotasDisponibles(int idRefugio) {
        SesionUsuarioBE sesion = authorizationService.requireRefugio(idRefugio > 0 ? idRefugio : null);
        validarIdRefugio(idRefugio);
        return publicacionesRepository.getMascotasDisponibles(sesion.getIdRefugio());
    }

    // ------------------------------------------------------- alta (RF13/RF06)

    @Override
    @Transactional
    public AdministrarPublicacionResponseBE crearPublicacion(NuevaPublicacionRequestBE request) {
        Integer refugioSolicitado = (request != null && request.getIdRefugio() != null && request.getIdRefugio() > 0)
                ? request.getIdRefugio()
                : null;
        SesionUsuarioBE sesion = authorizationService.requireRefugio(refugioSolicitado);

        if (request == null || refugioSolicitado == null) {
            throw badRequest("El identificador del refugio es obligatorio");
        }
        int idRefugio = sesion.getIdRefugio();

        String estado = resolverEstadoInicial(request.getEstadoPublicacion());
        String caracteristicas = obligatorio(request.getCaracteristicasMascota(),
                "La descripción y las características de la mascota son obligatorias");
        String condicion = obligatorio(
                textoONulo(request.getCondicionAdopcion()) != null ? request.getCondicionAdopcion() : request.getCondicionSanitaria(),
                "Las condiciones de adopción son obligatorias");
        LocalDate fechaPublicacion = parsearFecha(request.getFechaPublicacion(), "publicación");

        boolean tieneMascotaRegistrada = request.getNroRegMunicipal() != null && request.getNroRegMunicipal() > 0;
        int nroRegMunicipal;
        if (tieneMascotaRegistrada) {
            nroRegMunicipal = request.getNroRegMunicipal();
            if (!publicacionesRepository.mascotaPerteneceARefugio(nroRegMunicipal, idRefugio)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "La mascota indicada no está registrada bajo cuidado del refugio");
            }
            if (ACTIVA.equals(estado) && publicacionesRepository.contarPublicacionesActivas(nroRegMunicipal, 0) > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "La mascota seleccionada ya cuenta con una publicación activa de adopción");
            }
        } else {
            nroRegMunicipal = registrarMascotaNueva(request, idRefugio);
        }

        request.setIdRefugio(idRefugio);
        if (request.getCuilPublicante() == null || request.getCuilPublicante().isBlank()) {
            request.setCuilPublicante(sesion.getCuil());
        }

        int nroPublicacion = publicacionesRepository.insertarPublicacion(
                idRefugio,
                nroRegMunicipal,
                fechaPublicacion,
                caracteristicas,
                condicion,
                textoONulo(request.getFoto()),
                estado
        );
        return AdministrarPublicacionResponseBE.exitoso("Publicación de adopción creada exitosamente", nroPublicacion);
    }

    private int registrarMascotaNueva(NuevaPublicacionRequestBE request, int idRefugio) {
        String nombre = textoONulo(request.getNombreMascota());
        if (nombre == null) {
            throw badRequest("Debe especificar una mascota existente o ingresar los datos de una nueva mascota");
        }
        if (nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw badRequest("El nombre de la mascota no puede superar los " + LARGO_MAXIMO_NOMBRE + " caracteres");
        }

        int añoNacimiento = resolverAñoNacimiento(request.getAñoNacimiento());

        return publicacionesRepository.registrarMascota(
                idRefugio,
                nombre,
                normalizarSexo(request.getSexo()),
                añoNacimiento,
                textoONulo(request.getEspecie()),
                textoONulo(request.getRaza())
        );
    }

    // ------------------------------------------------------ cambio de estado

    @Override
    @Transactional
    public AdministrarPublicacionResponseBE actualizarEstado(int nroPublicacion, CambioEstadoRequestBE request) {
        Integer refugioSolicitado = (request != null && request.getIdRefugio() != null && request.getIdRefugio() > 0)
                ? request.getIdRefugio()
                : null;
        SesionUsuarioBE sesion = authorizationService.requireRefugio(refugioSolicitado);

        if (nroPublicacion <= 0) {
            throw badRequest("El número de publicación debe ser un valor positivo");
        }
        if (request == null || request.getIdRefugio() == null || textoONulo(request.getNuevoEstado()) == null) {
            throw badRequest("El identificador del refugio y el nuevo estado son obligatorios");
        }

        String nuevoEstado = request.getNuevoEstado().trim();
        if (!publicacionesRepository.esEstadoValido(nuevoEstado)) {
            throw badRequest("Estado no válido para la publicación: " + nuevoEstado);
        }

        int idRefugio = sesion.getIdRefugio();
        request.setIdRefugio(idRefugio);

        PublicacionAdopcionBE actual = publicacionesRepository.getPublicacion(nroPublicacion, idRefugio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la publicación o no pertenece al refugio indicado"));
        validarTransicion(actual.getEstadoPublicacion(), nuevoEstado);

        if (ACTIVA.equals(nuevoEstado)
                && publicacionesRepository.contarPublicacionesActivas(actual.getNroRegMunicipal(), nroPublicacion) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La mascota ya cuenta con otra publicación activa de adopción");
        }

        if (!publicacionesRepository.actualizarEstado(nroPublicacion, idRefugio, nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró la publicación o no pertenece al refugio indicado");
        }
        return AdministrarPublicacionResponseBE.actualizado("Estado de publicación actualizado a " + nuevoEstado, nroPublicacion);
    }

    /**
     * Valida que el cambio de estado esté permitido. Finalizada no admite cambios.
     */
    static void validarTransicion(String estadoActual, String nuevoEstado) {
        if (nuevoEstado.equals(estadoActual)) {
            throw badRequest("La publicación ya se encuentra en estado " + nuevoEstado);
        }
        if (FINALIZADA.equals(estadoActual)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Una publicación finalizada no puede reactivarse ni cambiar de estado");
        }
        if (!TRANSICIONES.getOrDefault(estadoActual, Set.of()).contains(nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede pasar una publicación de " + estadoActual + " a " + nuevoEstado);
        }
    }

    // ------------------------------------------------------------- auxiliares

    private String resolverEstadoInicial(String estadoSolicitado) {
        String estado = textoONulo(estadoSolicitado) != null ? estadoSolicitado.trim() : ACTIVA;
        if (!publicacionesRepository.esEstadoValido(estado)) {
            throw badRequest("Estado no válido para la nueva publicación: " + estado);
        }
        if (!ESTADOS_INICIALES.contains(estado)) {
            throw badRequest("Una publicación nueva solo puede crearse Activa o Pausada");
        }
        return estado;
    }

    private static int resolverAñoNacimiento(Integer año) {
        if (año == null) {
            throw badRequest("El año de nacimiento es obligatorio para registrar una mascota nueva");
        }
        int actual = Year.now().getValue();
        if (año > actual) {
            throw badRequest("El año de nacimiento no puede ser futuro");
        }
        if (año < actual - ANTIGUEDAD_MAXIMA_ANIOS) {
            throw badRequest("El año de nacimiento no puede ser anterior a " + (actual - ANTIGUEDAD_MAXIMA_ANIOS));
        }
        return año;
    }

    public static String normalizarSexo(String sexo) {
        String valor = textoONulo(sexo);
        if (valor == null) {
            return "M";
        }
        return switch (valor.toUpperCase()) {
            case "H", "HEMBRA", "F", "FEMENINO" -> "H";
            case "M", "MACHO" -> "M";
            default -> throw badRequest("El sexo de la mascota debe ser 'M' (Macho) o 'H' (Hembra)");
        };
    }

    private static LocalDate parsearFecha(String fecha, String campo) {
        String valor = textoONulo(fecha);
        if (valor == null) {
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ex) {
            throw badRequest("La fecha de " + campo + " debe tener formato YYYY-MM-DD");
        }
    }

    private static void validarIdRefugio(int idRefugio) {
        if (idRefugio <= 0) {
            throw badRequest("El identificador del refugio debe ser un número entero positivo");
        }
    }

    private static String obligatorio(String valor, String mensaje) {
        String limpio = textoONulo(valor);
        if (limpio == null) {
            throw badRequest(mensaje);
        }
        return limpio;
    }

    private static String textoONulo(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim();
    }

    private static ResponseStatusException badRequest(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

}
