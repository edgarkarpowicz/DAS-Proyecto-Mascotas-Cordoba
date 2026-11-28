package ar.edu.ubp.das.mascotas.services.mascotas;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.RegistrarMascotaRequestBE;
import ar.edu.ubp.das.mascotas.repositories.mascotas.MascotasRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.adopciones.PublicacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * RF06 (Registrar Mascota con deduplicación y NRM único) y RF20 (Consultar Mis Mascotas).
 */
@Service
public class MascotasService implements IMascotasService {

    private static final int AÑO_NACIMIENTO_MINIMO = 1990;

    @Autowired
    private MascotasRepository mascotasRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    @Transactional
    public MascotaBE registrarMascota(RegistrarMascotaRequestBE request) {
        SesionUsuarioBE sesion = authorizationService.requirePerfil(
                PerfilUsuario.VETERINARIA,
                PerfilUsuario.MUNICIPALIDAD,
                PerfilUsuario.REFUGIO
        );
        Integer idRefugio = sesion.esPerfil(PerfilUsuario.REFUGIO)
                ? sesion.getIdRefugio()
                : (request != null ? request.getIdRefugio() : null);
        return registrarODeduplicarInternal(request, idRefugio);
    }

    /**
     * Alta o reutilización idempotente de NRM compartida entre altas manuales (RF06)
     * y el proceso de sincronización por lotes desde veterinarias.
     */
    @Transactional
    public MascotaBE registrarODeduplicarInternal(RegistrarMascotaRequestBE request, Integer idRefugioOverride) {
        if (request == null || esVacio(request.getNombre()) || esVacio(request.getEspecie())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre y la especie son obligatorios para registrar la mascota"
            );
        }

        String nombre = request.getNombre().trim();
        String especie = request.getEspecie().trim();
        String raza = !esVacio(request.getRaza()) ? request.getRaza().trim() : null;
        String sexo = PublicacionesService.normalizarSexo(request.getSexo());
        int añoNacimiento = validarAñoNacimiento(request.getAñoNacimiento());
        String microchip = !esVacio(request.getMicrochip()) ? request.getMicrochip().trim() : null;
        boolean castrado = Boolean.TRUE.equals(request.getCastrado());

        Integer idResponsable = null;
        if (!esVacio(request.getCuilResponsable())) {
            String cuilLimpio = request.getCuilResponsable().trim();
            idResponsable = mascotasRepository.buscarCiudadanoPorCuil(cuilLimpio)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "No existe un ciudadano habilitado con el CUIL indicado"
                    ));
        }

        // Prioridad 1 de deduplicación: microchip único.
        if (microchip != null) {
            Optional<MascotaBE> porChip = mascotasRepository.buscarPorMicrochip(microchip);
            if (porChip.isPresent()) {
                MascotaBE existente = porChip.get();
                existente.setNrmReutilizado(true);
                return existente;
            }
        }

        // Prioridad 2: coincidencia por responsable/refugio, nombre, sexo, año y especie.
        if (idResponsable != null || idRefugioOverride != null) {
            Optional<MascotaBE> coincidencia = mascotasRepository.buscarPorCoincidencia(
                    idResponsable,
                    idRefugioOverride,
                    nombre,
                    sexo,
                    añoNacimiento,
                    especie
            );
            if (coincidencia.isPresent()) {
                MascotaBE existente = coincidencia.get();
                existente.setNrmReutilizado(true);
                return existente;
            }
        }

        int nrm = mascotasRepository.insertarMascotaRegistroUnico(
                nombre,
                sexo,
                añoNacimiento,
                microchip,
                castrado,
                idResponsable,
                idRefugioOverride,
                especie,
                raza
        );

        MascotaBE nueva = new MascotaBE();
        nueva.setNroRegMunicipal(nrm);
        nueva.setNombre(nombre);
        nueva.setSexo(sexo);
        nueva.setAñoNacimiento(añoNacimiento);
        nueva.setEspecie(especie);
        nueva.setRaza(raza);
        nueva.setMicrochip(microchip);
        nueva.setCastrado(castrado);
        nueva.setVive(true);
        nueva.setIdResponsable(idResponsable);
        nueva.setIdRefugio(idRefugioOverride);
        nueva.setNrmReutilizado(false);
        return nueva;
    }

    @Override
    public PageResponseBE<MascotaBE> consultarMisMascotas(PageRequestBE pageRequest) {
        SesionUsuarioBE sesion = authorizationService.requirePerfil(PerfilUsuario.CIUDADANO);
        PageRequestBE req = pageRequest != null ? pageRequest : new PageRequestBE();
        int total = mascotasRepository.countMascotasCiudadano(sesion.getIdCiudadano());
        List<MascotaBE> items = total > 0
                ? mascotasRepository.getMascotasCiudadano(sesion.getIdCiudadano(), req.getOffset(), req.getTamanioPagina())
                : List.of();
        return PageResponseBE.of(items, req, total);
    }

    private static int validarAñoNacimiento(Integer año) {
        int añoActual = Year.now().getValue();
        if (año == null || año < AÑO_NACIMIENTO_MINIMO || año > añoActual) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El año de nacimiento debe estar entre " + AÑO_NACIMIENTO_MINIMO + " y " + añoActual
            );
        }
        return año;
    }

    private static boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
