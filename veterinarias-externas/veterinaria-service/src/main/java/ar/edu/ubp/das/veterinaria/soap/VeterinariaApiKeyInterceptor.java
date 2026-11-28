package ar.edu.ubp.das.veterinaria.soap;

import ar.edu.ubp.das.veterinaria.service.ClinicaStoreService;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.util.List;
import java.util.Map;

/**
 * Valida la cabecera HTTP X-API-KEY para invocaciones SOAP entrantes desde la Municipalidad.
 */
public class VeterinariaApiKeyInterceptor extends AbstractPhaseInterceptor<Message> {

    private final ClinicaStoreService storeService;

    public VeterinariaApiKeyInterceptor(ClinicaStoreService storeService) {
        super(Phase.RECEIVE);
        this.storeService = storeService;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        String requestMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if ("GET".equalsIgnoreCase(requestMethod)) {
            return;
        }
        String apiKey = extractApiKey(message);
        if (!storeService.isApiKeyValid(apiKey)) {
            throw new SoapFault("API Key de veterinaria inválida", SoapFault.FAULT_CODE_CLIENT);
        }
    }

    private static String extractApiKey(Message message) {
        Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
        if (headers == null) {
            return null;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if ("X-API-KEY".equalsIgnoreCase(entry.getKey()) && entry.getValue() != null && !entry.getValue().isEmpty()) {
                return entry.getValue().get(0);
            }
        }
        return null;
    }
}
