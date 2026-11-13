package ar.edu.ubp.das.mascotas.config;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.repositories.auth.SesionRepository;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.util.List;
import java.util.Map;

/**
 * Interceptor SOAP de Apache CXF que valida el token UUID de sesión (<token> o Authorization: Bearer)
 * y establece el contexto de usuario autenticado (RF15).
 */
public class AuthTokenInterceptor extends AbstractSoapInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    private final SesionRepository sesionRepository;

    public AuthTokenInterceptor(SesionRepository sesionRepository) {
        super(Phase.PRE_PROTOCOL);
        this.sesionRepository = sesionRepository;
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        if ("GET".equalsIgnoreCase(String.valueOf(message.get(Message.HTTP_REQUEST_METHOD)))) {
            return;
        }

        String token = extractToken(message);
        if (token == null || token.isBlank()) {
            throw new SoapFault("Missing or empty session token", SoapFault.FAULT_CODE_CLIENT);
        }

        SesionUsuarioBE sesion = sesionRepository.validarSesion(token)
                .orElseThrow(() -> new SoapFault("Invalid or expired session token", SoapFault.FAULT_CODE_CLIENT));

        UserContextHolder.set(sesion);
    }

    @Override
    public void handleFault(SoapMessage message) {
        UserContextHolder.clear();
    }

    private String extractToken(SoapMessage message) {
        String soapHeaderToken = SoapHeaderSupport.readSoapHeaderText(message, "token");
        if (soapHeaderToken != null && !soapHeaderToken.isBlank()) {
            return soapHeaderToken.trim();
        }

        Object protocolHeadersObj = message.get(Message.PROTOCOL_HEADERS);
        if (protocolHeadersObj instanceof Map<?, ?> headersMap) {
            for (Map.Entry<?, ?> entry : headersMap.entrySet()) {
                if (entry.getKey() != null && "Authorization".equalsIgnoreCase(String.valueOf(entry.getKey()))) {
                    Object valuesObj = entry.getValue();
                    if (valuesObj instanceof List<?> values && !values.isEmpty() && values.get(0) != null) {
                        String authHeader = String.valueOf(values.get(0)).trim();
                        if (authHeader.length() > BEARER_PREFIX.length()
                                && authHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
                            return authHeader.substring(BEARER_PREFIX.length()).trim();
                        }
                    }
                }
            }
        }
        return null;
    }
}

