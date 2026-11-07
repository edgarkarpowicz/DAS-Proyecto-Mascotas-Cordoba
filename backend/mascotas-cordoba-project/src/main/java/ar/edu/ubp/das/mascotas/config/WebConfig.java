package ar.edu.ubp.das.mascotas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.location:uploads/publicaciones}")
    private String storageLocation;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path configuredPath = Paths.get(storageLocation).toAbsolutePath().normalize();
        Path configuredUploadsRoot = configuredPath.getParent() != null ? configuredPath.getParent() : configuredPath;
        String configuredLocation = "file:" + configuredUploadsRoot.toString().replace("\\", "/") + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        configuredLocation,
                        "file:uploads/",
                        "file:backend/mascotas-cordoba-project/uploads/"
                );
    }

}
