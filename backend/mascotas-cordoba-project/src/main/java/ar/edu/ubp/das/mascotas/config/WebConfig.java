package ar.edu.ubp.das.mascotas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.location:uploads/publicaciones}")
    private String storageLocation;

    /**
     * Registra CORS como filtro Servlet de máxima prioridad para garantizar que
     * incluso respuestas 401/500 emitidas por filtros de seguridad incluyan
     * encabezados CORS y no sean enmascaradas por el navegador como error 0.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path configuredPath = Paths.get(storageLocation).toAbsolutePath().normalize();
        if (!Files.exists(configuredPath) && !storageLocation.startsWith("/") && !storageLocation.contains(":")) {
            Path rootUploads = Paths.get("../..", storageLocation).toAbsolutePath().normalize();
            if (Files.exists(rootUploads.getParent())) {
                configuredPath = rootUploads;
            }
        }
        Path configuredUploadsRoot = configuredPath.getParent() != null ? configuredPath.getParent() : configuredPath;
        String configuredLocation = "file:" + configuredUploadsRoot.toString().replace("\\", "/") + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        configuredLocation,
                        "file:uploads/",
                        "file:../../uploads/"
                );
    }

}
