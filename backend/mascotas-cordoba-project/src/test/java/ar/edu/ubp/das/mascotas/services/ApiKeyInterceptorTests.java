package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.config.ApiKeyInterceptor;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiKeyInterceptorTests {

    private static final String VALID_KEY = "UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT";

    @Test
    void testPermitirSiClaveNoConfigurada() {
        ApiKeyInterceptor interceptor = new ApiKeyInterceptor("");
        SoapMessage message = mock(SoapMessage.class);

        assertDoesNotThrow(() -> interceptor.handleMessage(message));
    }

    @Test
    void testPermitirSiClaveValida() {
        ApiKeyInterceptor interceptor = new ApiKeyInterceptor(VALID_KEY);
        SoapMessage message = mock(SoapMessage.class);

        Header header = new Header(new QName("http://example.com", "apiKey"), VALID_KEY);
        List<Header> headers = new ArrayList<>();
        headers.add(header);

        when(message.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> interceptor.handleMessage(message));
    }

    @Test
    void testLanzarFaultSiClaveInvalida() {
        ApiKeyInterceptor interceptor = new ApiKeyInterceptor(VALID_KEY);
        SoapMessage message = mock(SoapMessage.class);

        Header header = new Header(new QName("http://example.com", "apiKey"), "CLAVE_INCORRECTA");
        List<Header> headers = new ArrayList<>();
        headers.add(header);

        when(message.getHeaders()).thenReturn(headers);

        assertThrows(SoapFault.class, () -> interceptor.handleMessage(message));
    }

    @Test
    void testLanzarFaultSiHeadersNulos() {
        ApiKeyInterceptor interceptor = new ApiKeyInterceptor(VALID_KEY);
        SoapMessage message = mock(SoapMessage.class);

        when(message.getHeaders()).thenReturn(null);

        assertThrows(SoapFault.class, () -> interceptor.handleMessage(message));
    }

}
