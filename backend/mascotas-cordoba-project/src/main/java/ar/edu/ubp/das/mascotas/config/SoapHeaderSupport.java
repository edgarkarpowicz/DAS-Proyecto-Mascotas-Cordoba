package ar.edu.ubp.das.mascotas.config;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.w3c.dom.Element;

/**
 * Utilidades compartidas por los interceptores SOAP de Apache CXF.
 */
final class SoapHeaderSupport {

    private SoapHeaderSupport() {
    }

    static String readSoapHeaderText(SoapMessage message, String headerLocalName) {
        if (message == null || message.getHeaders() == null || headerLocalName == null) {
            return null;
        }
        for (Header header : message.getHeaders()) {
            if (header.getName() != null
                    && headerLocalName.equalsIgnoreCase(header.getName().getLocalPart())) {
                Object object = header.getObject();
                if (object instanceof Element element && element.getTextContent() != null) {
                    return element.getTextContent().trim();
                } else if (object instanceof String str) {
                    return str.trim();
                }
            }
        }
        return null;
    }
}

