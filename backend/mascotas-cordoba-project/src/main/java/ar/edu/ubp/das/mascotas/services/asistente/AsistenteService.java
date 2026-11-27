package ar.edu.ubp.das.mascotas.services.asistente;

import ar.edu.ubp.das.mascotas.BE.asistente.ConsultaAsistenteRequestBE;
import ar.edu.ubp.das.mascotas.BE.asistente.RespuestaAsistenteBE;
import ar.edu.ubp.das.mascotas.integrations.ia.AsistenteVirtualClient;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * RF21 - Consultar Asistente Virtual (consulta pública u orientada al perfil autenticado vía RAG).
 */
@Service
public class AsistenteService implements IAsistenteService {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ObjectProvider<AsistenteVirtualClient> asistenteClientProvider;

    @Override
    public RespuestaAsistenteBE consultar(ConsultaAsistenteRequestBE request) {
        // TODO(RF21): Validar consulta, recuperar contexto RAG público o por sesión (getOptionalAuthenticated)
        // e invocar a asistenteClientProvider.getIfAvailable() devolviendo respuesta controlada ante indisponibilidad.
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "RF21 - Consultar Asistente Virtual pendiente de implementación"
        );
    }
}
