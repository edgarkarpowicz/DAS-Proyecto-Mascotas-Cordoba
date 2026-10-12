package ar.edu.ubp.das.mascotas.config;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;

public class ApiKeyInterceptor extends AbstractSoapInterceptor {

    private final String configuredApiKey;

    public ApiKeyInterceptor(String configuredApiKey) {
        super(Phase.PRE_PROTOCOL);
        this.configuredApiKey = configuredApiKey;
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        // En modo desarrollo, si no está configurada la API key se permite el paso
        if (configuredApiKey == null || configuredApiKey.trim().isEmpty()) {
            return;
        }

        String apiKey = readApiKeyFromHeader(message);
        if (!configuredApiKey.equals(apiKey)) {
            throw new SoapFault(
                    "Invalid API Key",
                    SoapFault.FAULT_CODE_CLIENT
            );
        }
    }

    private String readApiKeyFromHeader(SoapMessage message) {
        if (message.getHeaders() == null) {
            return null;
        }

        for (Header header : message.getHeaders()) {
            if ("apiKey".equalsIgnoreCase(header.getName().getLocalPart())) {
                Object object = header.getObject();
                if (object instanceof Element element) {
                    return element.getTextContent().trim();
                } else if (object instanceof String str) {
                    return str.trim();
                }
            }
        }
        return null;
    }

}
