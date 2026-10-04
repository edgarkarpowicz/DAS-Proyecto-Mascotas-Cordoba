package ar.edu.ubp.das.mascotas.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("Pipeline - ResponseStatusException capturada: status={}, motivo={}", ex.getStatusCode(), ex.getReason());
        Map<String, Object> error = new HashMap<>();
        error.put("codigoRespuesta", -1);
        error.put("mensajeRespuesta", ex.getReason() != null ? ex.getReason() : ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Pipeline - JSON malformado o cuerpo de petición ilegible: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("codigoRespuesta", -1);
        error.put("mensajeRespuesta", "El cuerpo de la solicitud JSON es requerido y debe tener un formato válido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.warn("Pipeline - Método HTTP no soportado: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("codigoRespuesta", -1);
        error.put("mensajeRespuesta", "El método HTTP utilizado no está permitido para este recurso");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Pipeline - Tipo de parámetro inválido en la URL: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("codigoRespuesta", -1);
        error.put("mensajeRespuesta", String.format("El parámetro '%s' debe ser de tipo numérico válido", ex.getName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Pipeline - Excepción no controlada capturada: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("codigoRespuesta", -1);
        error.put("mensajeRespuesta", "Error inesperado del servidor: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
