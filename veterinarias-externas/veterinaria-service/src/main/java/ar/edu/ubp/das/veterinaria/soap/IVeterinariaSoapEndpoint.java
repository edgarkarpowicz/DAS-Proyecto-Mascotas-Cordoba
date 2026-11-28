package ar.edu.ubp.das.veterinaria.soap;

import ar.edu.ubp.das.veterinaria.model.NrmAcuseDTO;
import ar.edu.ubp.das.veterinaria.model.NrmAsignadoDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaMascotasDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaPracticasDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(targetNamespace = "http://veterinaria.das.ubp.edu.ar/", name = "VeterinariaSoapService")
public interface IVeterinariaSoapEndpoint {

    @WebMethod
    PaginaMascotasDTO obtenerMascotasSinNrm(
            @WebParam(name = "fechaDesde") String fechaDesde,
            @WebParam(name = "fechaHasta") String fechaHasta,
            @WebParam(name = "pagina") Integer pagina,
            @WebParam(name = "tamanioPagina") Integer tamanioPagina
    );

    @WebMethod
    List<NrmAcuseDTO> informarNrm(@WebParam(name = "asignaciones") List<NrmAsignadoDTO> asignaciones);

    @WebMethod
    PaginaPracticasDTO obtenerAtencionesSanitarias(
            @WebParam(name = "fechaDesde") String fechaDesde,
            @WebParam(name = "fechaHasta") String fechaHasta,
            @WebParam(name = "pagina") Integer pagina,
            @WebParam(name = "tamanioPagina") Integer tamanioPagina
    );
}
