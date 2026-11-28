package ar.edu.ubp.das.mascotas.services.sanidad;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.AtencionSanitariaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.CarnetSanitarioBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.RegistrarAtencionRequestBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.TipoAtencionBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.ValidacionCarnetBE;
import ar.edu.ubp.das.mascotas.repositories.sanidad.SanidadRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RF09 (Información Sanitaria), RF10 (Generar Carnet) y RF11 (Consultar y Validar Carnet).
 */
@Service
public class SanidadService implements ISanidadService {

    @Autowired
    private SanidadRepository sanidadRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    @Transactional
    public AtencionSanitariaBE registrarAtencion(RegistrarAtencionRequestBE request) {
        SesionUsuarioBE sesion = authorizationService.requirePerfil(
                PerfilUsuario.VETERINARIA,
                PerfilUsuario.MUNICIPALIDAD
        );
        if (request == null || request.getNroRegMunicipal() == null || request.getNroRegMunicipal() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El NRM de la mascota es obligatorio");
        }
        if (request.getCodTipoAtencion() == null || request.getCodTipoAtencion() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de práctica sanitaria es obligatorio");
        }
        if (request.getDetalleAtencion() == null || request.getDetalleAtencion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El detalle de la práctica sanitaria es obligatorio");
        }

        LocalDate fechaAtencion = request.getFechaAtencion() != null ? request.getFechaAtencion() : LocalDate.now();
        if (request.getFechaVencimiento() != null && request.getFechaVencimiento().isBefore(fechaAtencion)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de vencimiento no puede ser anterior a la fecha de atención"
            );
        }

        int idVeterinaria = resolverIdVeterinaria(sesion, request.getIdVeterinaria());
        validarTipoAtencionHabilitado(request.getCodTipoAtencion());
        obtenerMascotaActiva(request.getNroRegMunicipal());

        return sanidadRepository.registrarAtencion(
                request.getNroRegMunicipal(),
                fechaAtencion,
                request.getCodTipoAtencion(),
                request.getDetalleAtencion().trim(),
                request.getFechaVencimiento(),
                idVeterinaria,
                request.getIdProfesional(),
                request.getProfesional(),
                null,
                null
        );
    }

    /**
     * Inserta o recupera de manera idempotente una práctica sanitaria sincronizada desde una clínica externa (Wave 4).
     */
    @Transactional
    public AtencionSanitariaBE registrarPracticaExternaInternal(int nroRegMunicipal,
                                                               int idVeterinaria,
                                                               LocalDate fechaAtencion,
                                                               int codTipoAtencion,
                                                               String detalleAtencion,
                                                               LocalDate fechaVencimiento,
                                                               Integer idProfesional,
                                                               String profesionalTexto,
                                                               Integer nroAtencionExterna,
                                                               Integer nroDetalleExterno) {
        validarTipoAtencionHabilitado(codTipoAtencion);
        obtenerMascotaActiva(nroRegMunicipal);
        return sanidadRepository.registrarAtencion(
                nroRegMunicipal,
                fechaAtencion != null ? fechaAtencion : LocalDate.now(),
                codTipoAtencion,
                detalleAtencion != null ? detalleAtencion.trim() : "Práctica sanitaria registrada",
                fechaVencimiento,
                idVeterinaria,
                idProfesional,
                profesionalTexto,
                nroAtencionExterna,
                nroDetalleExterno
        );
    }

    @Override
    public List<AtencionSanitariaBE> consultarAtenciones(int nroRegMunicipal) {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        MascotaBE mascota = obtenerMascotaExistente(nroRegMunicipal);
        verificarAccesoMascota(sesion, mascota);
        return sanidadRepository.getAtencionesPorMascota(nroRegMunicipal);
    }

    @Override
    @Transactional
    public CarnetSanitarioBE generarCarnet(int nroRegMunicipal) {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        MascotaBE mascota = obtenerMascotaActiva(nroRegMunicipal);
        verificarAccesoMascota(sesion, mascota);

        String codigoValidacion = "CBA-" + nroRegMunicipal + "-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        CarnetSanitarioBE carnet = sanidadRepository.emitirCarnet(
                nroRegMunicipal,
                codigoValidacion,
                sesion.getIdCiudadano()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudo emitir el carnet sanitario digital"
        ));

        enriquecerCarnet(carnet, mascota);
        return carnet;
    }

    @Override
    public CarnetSanitarioBE consultarCarnet(int nroRegMunicipal) {
        SesionUsuarioBE sesion = authorizationService.requireAuthenticated();
        MascotaBE mascota = obtenerMascotaExistente(nroRegMunicipal);
        verificarAccesoMascota(sesion, mascota);

        CarnetSanitarioBE carnet = sanidadRepository.findCarnetActivoByNrm(nroRegMunicipal)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La mascota NRM " + nroRegMunicipal + " todavía no tiene un carnet sanitario digital emitido"
                ));

        enriquecerCarnet(carnet, mascota);
        return carnet;
    }

    @Override
    public ValidacionCarnetBE validarCarnet(String codigoValidacion) {
        if (codigoValidacion == null || codigoValidacion.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código de validación es obligatorio");
        }

        Optional<CarnetSanitarioBE> carnetOpt = sanidadRepository.findCarnetByCodigo(codigoValidacion.trim());
        ValidacionCarnetBE resultado = new ValidacionCarnetBE();
        if (carnetOpt.isEmpty()) {
            resultado.setAutentico(false);
            resultado.setVigente(false);
            resultado.setCodigoValidacion(codigoValidacion.trim());
            resultado.setMensaje("No se encontró ningún carnet sanitario oficial con ese código.");
            return resultado;
        }

        CarnetSanitarioBE carnet = carnetOpt.get();
        boolean mascotaActiva = carnet.getVive() == null || Boolean.TRUE.equals(carnet.getVive());
        boolean vigente = carnet.isVigente() && mascotaActiva;

        resultado.setAutentico(true);
        resultado.setVigente(vigente);
        resultado.setNroRegMunicipal(carnet.getNroRegMunicipal());
        resultado.setCodigoValidacion(carnet.getCodigoValidacion());
        resultado.setNombreMascota(carnet.getNombreMascota());
        resultado.setEspecie(carnet.getEspecie());
        resultado.setResponsableNombre(carnet.getResponsableNombre());
        resultado.setFechaEmision(carnet.getFechaEmision());

        if (carnet.getNroRegMunicipal() != null) {
            sanidadRepository.findMascotaByNrm(carnet.getNroRegMunicipal())
                    .ifPresent(m -> resultado.setEstadoSanitario(m.getEstadoSanitario()));
        }

        if (vigente) {
            resultado.setMensaje("Carnet sanitario digital auténtico y vigente emitido por la Municipalidad de Córdoba.");
        } else if (!mascotaActiva) {
            resultado.setMensaje("El código pertenece a un carnet emitido, pero la mascota figura inactiva en el Registro Único.");
        } else {
            resultado.setMensaje("El código es auténtico, pero corresponde a una emisión anterior ya reemplazada.");
        }
        return resultado;
    }

    private void enriquecerCarnet(CarnetSanitarioBE carnet, MascotaBE mascota) {
        carnet.setEstadoSanitario(mascota.getEstadoSanitario());
        carnet.setAtenciones(sanidadRepository.getAtencionesPorMascota(carnet.getNroRegMunicipal()));
    }

    private int resolverIdVeterinaria(SesionUsuarioBE sesion, Integer idVeterinariaSolicitada) {
        if (sesion.esPerfil(PerfilUsuario.VETERINARIA)) {
            if (sesion.getIdVeterinaria() == null) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La sesión de veterinaria no tiene una clínica habilitada asociada"
                );
            }
            if (idVeterinariaSolicitada != null && !idVeterinariaSolicitada.equals(sesion.getIdVeterinaria())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No está autorizado a registrar prácticas en nombre de otra veterinaria"
                );
            }
            return sesion.getIdVeterinaria();
        }

        if (idVeterinariaSolicitada == null || idVeterinariaSolicitada <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar la veterinaria participante que realizó la práctica"
            );
        }
        return idVeterinariaSolicitada;
    }

    private void validarTipoAtencionHabilitado(int codTipoAtencion) {
        List<TipoAtencionBE> tipos = sanidadRepository.getTiposAtencion();
        boolean existe = tipos.stream().anyMatch(t -> Integer.valueOf(codTipoAtencion).equals(t.getCodTipoAtencion()));
        if (!existe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de práctica sanitaria indicado (" + codTipoAtencion + ") no pertenece al programa municipal"
            );
        }
    }

    private MascotaBE obtenerMascotaExistente(int nroRegMunicipal) {
        return sanidadRepository.findMascotaByNrm(nroRegMunicipal)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe ninguna mascota registrada con NRM " + nroRegMunicipal
                ));
    }

    private MascotaBE obtenerMascotaActiva(int nroRegMunicipal) {
        MascotaBE mascota = obtenerMascotaExistente(nroRegMunicipal);
        if (Boolean.FALSE.equals(mascota.getVive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La mascota NRM " + nroRegMunicipal + " no se encuentra activa en el Registro Único"
            );
        }
        return mascota;
    }

    private void verificarAccesoMascota(SesionUsuarioBE sesion, MascotaBE mascota) {
        if (sesion.esPerfil(PerfilUsuario.VETERINARIA) || sesion.esPerfil(PerfilUsuario.MUNICIPALIDAD)) {
            return;
        }
        if (sesion.esPerfil(PerfilUsuario.CIUDADANO)) {
            if (sesion.getIdCiudadano() != null && sesion.getIdCiudadano().equals(mascota.getIdResponsable())) {
                return;
            }
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo podés consultar o emitir el carnet de tus propias mascotas registradas"
            );
        }
        if (sesion.esPerfil(PerfilUsuario.REFUGIO)) {
            if (sesion.getIdRefugio() != null && sesion.getIdRefugio().equals(mascota.getIdRefugio())) {
                return;
            }
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo podés consultar o emitir el carnet de mascotas alojadas en tu refugio"
            );
        }
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "El perfil actual no tiene permisos para acceder al carnet sanitario de esta mascota"
        );
    }
}
