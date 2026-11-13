package ar.edu.ubp.das.mascotas.config;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

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

        String apiKey = SoapHeaderSupport.readSoapHeaderText(message, "apiKey");
        if (!configuredApiKey.equals(apiKey)) {
            throw new SoapFault(
                    "Invalid API Key",
                    SoapFault.FAULT_CODE_CLIENT
            );
        }
    }

}
