package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Selecciona dinámicamente el adaptador REST o SOAP según la configuración de la veterinaria (RF04 / RNF09).
 */
@Component
public class VeterinariaClientRegistry {

    private final Map<TecnologiaIntegracion, VeterinariaIntegrationClient> clientsByTech;

    public VeterinariaClientRegistry(ObjectProvider<List<VeterinariaIntegrationClient>> clientsProvider) {
        List<VeterinariaIntegrationClient> clients = clientsProvider != null
                ? clientsProvider.getIfAvailable(Collections::emptyList)
                : Collections.emptyList();
        EnumMap<TecnologiaIntegracion, VeterinariaIntegrationClient> map = new EnumMap<>(TecnologiaIntegracion.class);
        if (clients != null) {
            for (VeterinariaIntegrationClient client : clients) {
                if (client != null && client.getTecnologia() != null) {
                    map.put(client.getTecnologia(), client);
                }
            }
        }
        this.clientsByTech = Collections.unmodifiableMap(map);
    }

    public VeterinariaIntegrationClient resolve(TecnologiaIntegracion tecnologia) {
        if (tecnologia == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe especificar la tecnología de integración de la veterinaria (REST o SOAP)"
            );
        }
        VeterinariaIntegrationClient client = clientsByTech.get(tecnologia);
        if (client == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_IMPLEMENTED,
                    "No hay un conector registrado para la tecnología " + tecnologia
            );
        }
        return client;
    }

    public boolean supports(TecnologiaIntegracion tecnologia) {
        return tecnologia != null && clientsByTech.containsKey(tecnologia);
    }
}
