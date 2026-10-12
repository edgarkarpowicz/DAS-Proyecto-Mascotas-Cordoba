package ar.edu.ubp.das.mascotas.services.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.AdministrarPublicacionResponseBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.CambioEstadoRequestBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.EstadoPublicacionBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.MascotaDisponibleBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.NuevaPublicacionRequestBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import ar.edu.ubp.das.mascotas.repositories.adopciones.PublicacionesRepository;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@WebService(
        endpointInterface = "ar.edu.ubp.das.mascotas.services.adopciones.IPublicacionesService",
        targetNamespace = "http://adopciones.services.mascotas.das.ubp.edu.ar/"
)
public class PublicacionesService implements IPublicacionesService {

    @Autowired
    private PublicacionesRepository publicacionesRepository;

    @Override
    public List<EstadoPublicacionBE> getEstadosPublicacion() {
        return publicacionesRepository.getEstadosPublicacion();
    }

    @Override
    public List<PublicacionAdopcionBE> getPublicacionesPorRefugio(int idRefugio) {
        if (idRefugio <= 0) {
            return Collections.emptyList();
        }
        return publicacionesRepository.getPublicacionesPorRefugio(idRefugio);
    }

    @Override
    public List<MascotaDisponibleBE> getMascotasDisponibles(int idRefugio) {
        if (idRefugio <= 0) {
            return Collections.emptyList();
        }
        return publicacionesRepository.getMascotasDisponibles(idRefugio);
    }

    @Override
    public AdministrarPublicacionResponseBE crearPublicacion(NuevaPublicacionRequestBE request) {
        if (request == null || request.getIdRefugio() == null || request.getIdRefugio() <= 0) {
            return AdministrarPublicacionResponseBE.error("El identificador del refugio es obligatorio");
        }

        boolean tieneMascotaRegistrada = request.getNroRegMunicipal() != null && request.getNroRegMunicipal() > 0;
        boolean tieneNombreMascota = request.getNombreMascota() != null && !request.getNombreMascota().trim().isEmpty();

        if (!tieneMascotaRegistrada && !tieneNombreMascota) {
            return AdministrarPublicacionResponseBE.error("Debe especificar una mascota existente o ingresar los datos de una nueva mascota");
        }

        if (request.getEstadoPublicacion() != null && !request.getEstadoPublicacion().trim().isEmpty()) {
            String estado = request.getEstadoPublicacion().trim();
            if (!publicacionesRepository.esEstadoValido(estado)) {
                return AdministrarPublicacionResponseBE.error("Estado no válido para la nueva publicación: " + estado);
            }
        }

        int nroPublicacion = publicacionesRepository.crearPublicacion(request);
        return AdministrarPublicacionResponseBE.exitoso("Publicación de adopción creada exitosamente", nroPublicacion);
    }

    @Override
    public AdministrarPublicacionResponseBE actualizarEstado(int nroPublicacion, CambioEstadoRequestBE request) {
        if (nroPublicacion <= 0) {
            return AdministrarPublicacionResponseBE.error("El número de publicación debe ser un valor positivo");
        }

        if (request == null || request.getIdRefugio() == null || request.getNuevoEstado() == null ||
                request.getNuevoEstado().trim().isEmpty()) {
            return AdministrarPublicacionResponseBE.error("El identificador del refugio y el nuevo estado son obligatorios");
        }

        String nuevoEstado = request.getNuevoEstado().trim();
        if (!publicacionesRepository.esEstadoValido(nuevoEstado)) {
            return AdministrarPublicacionResponseBE.error("Estado no válido para la publicación: " + nuevoEstado);
        }

        boolean actualizado = publicacionesRepository.actualizarEstado(
                nroPublicacion,
                request.getIdRefugio(),
                nuevoEstado
        );

        if (!actualizado) {
            return AdministrarPublicacionResponseBE.error("No se encontró la publicación o no pertenece al refugio indicado");
        }

        return AdministrarPublicacionResponseBE.actualizado("Estado de publicación actualizado a " + nuevoEstado, nroPublicacion);
    }

}
