package ar.edu.ubp.das.mascotas.config;

import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import ar.edu.ubp.das.mascotas.services.adopciones.IPublicacionesService;
import ar.edu.ubp.das.mascotas.services.auth.IAuthService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

    @Value("${security.soap.api-key:${security.api-key:}}")
    private String apiKey;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Bean
    public EndpointImpl authEndpoint(Bus bus, IAuthService service) {
        return buildEndpoint(bus, service, "/auth", false);
    }

    @Bean
    public EndpointImpl publicacionesEndpoint(Bus bus, IPublicacionesService service) {
        return buildEndpoint(bus, service, "/publicaciones", true);
    }

    private EndpointImpl buildEndpoint(Bus bus, Object service, String path, boolean requireSessionToken) {
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.getInInterceptors().add(new ApiKeyInterceptor(apiKey));
        if (requireSessionToken) {
            endpoint.getInInterceptors().add(new AuthTokenInterceptor(jwtTokenService));
        }
        endpoint.publish(path);
        return endpoint;
    }

}
