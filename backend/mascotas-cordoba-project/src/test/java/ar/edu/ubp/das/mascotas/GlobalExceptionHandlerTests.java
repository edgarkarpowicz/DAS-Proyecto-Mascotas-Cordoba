package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos incompletos");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResponseStatusException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(-1, response.getBody().get("codigoRespuesta"));
        assertEquals("Datos incompletos", response.getBody().get("mensajeRespuesta"));
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Fallo imprevisto");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(-1, response.getBody().get("codigoRespuesta"));
        assertTrue(response.getBody().get("mensajeRespuesta").toString().contains("Fallo imprevisto"));
    }

}
