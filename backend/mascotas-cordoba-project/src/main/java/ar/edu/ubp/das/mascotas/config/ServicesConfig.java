package ar.edu.ubp.das.mascotas.config;

import ar.edu.ubp.das.mascotas.services.adopciones.IPublicacionesService;
import ar.edu.ubp.das.mascotas.services.auth.IAuthService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

    @Value("${security.soap.api-key:${security.api-key:}}")
    private String apiKey;

    @Bean
    public EndpointImpl authEndpoint(Bus bus, IAuthService service) {
        return buildEndpoint(bus, service, "/auth");
    }

    @Bean
    public EndpointImpl publicacionesEndpoint(Bus bus, IPublicacionesService service) {
        return buildEndpoint(bus, service, "/publicaciones");
    }

    private EndpointImpl buildEndpoint(Bus bus, Object service, String path) {
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.getInInterceptors().add(new ApiKeyInterceptor(apiKey));
        endpoint.publish(path);
        return endpoint;
    }

}
