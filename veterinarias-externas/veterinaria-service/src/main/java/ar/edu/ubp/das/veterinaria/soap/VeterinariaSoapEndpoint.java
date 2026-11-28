package ar.edu.ubp.das.veterinaria.soap;

import ar.edu.ubp.das.veterinaria.model.NrmAcuseDTO;
import ar.edu.ubp.das.veterinaria.model.NrmAsignadoDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaMascotasDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaPracticasDTO;
import ar.edu.ubp.das.veterinaria.service.ClinicaStoreService;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@WebService(
        targetNamespace = "http://veterinaria.das.ubp.edu.ar/",
        serviceName = "VeterinariaSoapService",
        endpointInterface = "ar.edu.ubp.das.veterinaria.soap.IVeterinariaSoapEndpoint"
)
public class VeterinariaSoapEndpoint implements IVeterinariaSoapEndpoint {

    @Autowired
    private ClinicaStoreService storeService;

    @Override
    public PaginaMascotasDTO obtenerMascotasSinNrm(
            String fechaDesde,
            String fechaHasta,
            Integer pagina,
            Integer tamanioPagina) {
        return storeService.obtenerMascotasSinNrm(fechaDesde, fechaHasta, pagina, tamanioPagina);
    }

    @Override
    public List<NrmAcuseDTO> informarNrm(List<NrmAsignadoDTO> asignaciones) {
        return storeService.informarNrm(asignaciones);
    }

    @Override
    public PaginaPracticasDTO obtenerAtencionesSanitarias(
            String fechaDesde,
            String fechaHasta,
            Integer pagina,
            Integer tamanioPagina) {
        return storeService.obtenerAtencionesSanitarias(fechaDesde, fechaHasta, pagina, tamanioPagina);
    }
}
