package ar.edu.ubp.das.veterinaria.soap;

import ar.edu.ubp.das.veterinaria.service.ClinicaStoreService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {

    @Bean
    public EndpointImpl veterinariaSoapEndpointBean(
            Bus bus,
            VeterinariaSoapEndpoint endpointImplBean,
            ClinicaStoreService storeService) {
        EndpointImpl endpoint = new EndpointImpl(bus, endpointImplBean);
        endpoint.getInInterceptors().add(new VeterinariaApiKeyInterceptor(storeService));
        endpoint.publish("/VeterinariaSoapService");
        return endpoint;
    }
}
