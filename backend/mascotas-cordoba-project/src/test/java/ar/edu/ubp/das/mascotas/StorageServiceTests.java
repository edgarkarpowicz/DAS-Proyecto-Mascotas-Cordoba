package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.services.adopciones.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTests {

    private StorageService storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageService = new StorageService();
        ReflectionTestUtils.setField(storageService, "storageLocation", tempDir.toString());
    }

    @Test
    void testAlmacenarFotoExitosa() {
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "mascota.jpg",
                "image/jpeg",
                "datos-de-imagen-binarios".getBytes()
        );

        String result = storageService.almacenarFoto(file);

        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/publicaciones/"));
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    void testAlmacenarFotoVaciaLanzaBadRequest() {
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "vacio.png",
                "image/png",
                new byte[0]
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> storageService.almacenarFoto(file));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("archivo de imagen válido"));
    }

    @Test
    void testAlmacenarFormatoInvalidoLanzaBadRequest() {
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "documento.pdf",
                "application/pdf",
                "%PDF-1.4".getBytes()
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> storageService.almacenarFoto(file));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("no permitido"));
    }

    @Test
    void testAlmacenarFotoSuperaTamañoMaximoLanzaBadRequest() {
        byte[] bytesGrandes = new byte[6 * 1024 * 1024]; // 6 MB
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "gigante.jpg",
                "image/jpeg",
                bytesGrandes
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> storageService.almacenarFoto(file));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("5 MB"));
    }

}

