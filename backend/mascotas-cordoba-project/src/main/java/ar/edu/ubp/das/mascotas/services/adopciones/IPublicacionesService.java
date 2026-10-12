package ar.edu.ubp.das.mascotas.services.adopciones;

import ar.edu.ubp.das.mascotas.BE.adopciones.AdministrarPublicacionResponseBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.CambioEstadoRequestBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.EstadoPublicacionBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.MascotaDisponibleBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.NuevaPublicacionRequestBE;
import ar.edu.ubp.das.mascotas.BE.adopciones.PublicacionAdopcionBE;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import java.util.List;

@WebService(targetNamespace = "http://adopciones.services.mascotas.das.ubp.edu.ar/")
public interface IPublicacionesService {

    @WebMethod
    @WebResult(name = "estado")
    List<EstadoPublicacionBE> getEstadosPublicacion();

    @WebMethod
    @WebResult(name = "publicacion")
    List<PublicacionAdopcionBE> getPublicacionesPorRefugio(
            @WebParam(name = "idRefugio") int idRefugio
    );

    @WebMethod
    @WebResult(name = "mascota")
    List<MascotaDisponibleBE> getMascotasDisponibles(
            @WebParam(name = "idRefugio") int idRefugio
    );

    @WebMethod
    @WebResult(name = "response")
    AdministrarPublicacionResponseBE crearPublicacion(
            @WebParam(name = "request") NuevaPublicacionRequestBE request
    );

    @WebMethod
    @WebResult(name = "response")
    AdministrarPublicacionResponseBE actualizarEstado(
            @WebParam(name = "nroPublicacion") int nroPublicacion,
            @WebParam(name = "request") CambioEstadoRequestBE request
    );

}
