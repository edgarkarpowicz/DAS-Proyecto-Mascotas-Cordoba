package ar.edu.ubp.das.mascotas.services.adopciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp",
            ".gif"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Value("${app.storage.location:uploads/publicaciones}")
    private String storageLocation;

    /**
     * Valida y almacena una imagen de publicación en el sistema de archivos local.
     * Retorna la ruta relativa web para acceder al recurso (/uploads/publicaciones/{uuid}_{archivo}).
     */
    public String almacenarFoto(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar un archivo de imagen válido");
        }

        if (archivo.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tamaño de la imagen no puede superar los 5 MB");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato de imagen no permitido. Solo se aceptan imágenes JPG, PNG, WEBP o GIF"
            );
        }

        String originalFilename = archivo.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            originalFilename = "foto.jpg";
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Extensión de archivo no permitida. Solo se admiten extensiones .jpg, .jpeg, .png, .webp o .gif"
            );
        }

        // Sanitizar el nombre original eliminando caracteres conflictivos
        String cleanName = originalFilename.substring(0, dotIndex > 0 ? dotIndex : originalFilename.length())
                .replaceAll("[^a-zA-Z0-9_-]", "_");
        if (cleanName.length() > 30) {
            cleanName = cleanName.substring(0, 30);
        }

        String uniqueFilename = UUID.randomUUID().toString() + "_" + cleanName + extension;

        try {
            Path targetDir = Paths.get(storageLocation).toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Path targetPath = targetDir.resolve(uniqueFilename).normalize();

            // Evitar posibles ataques de path traversal
            if (!targetPath.startsWith(targetDir)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo inválido");
            }

            try (InputStream inputStream = archivo.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            logger.info("Foto de mascota almacenada exitosamente en: {}", targetPath);

            return "/uploads/publicaciones/" + uniqueFilename;
        } catch (IOException e) {
            logger.error("Error al guardar archivo en disco: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar la imagen en el servidor: " + e.getMessage()
            );
        }
    }

}

