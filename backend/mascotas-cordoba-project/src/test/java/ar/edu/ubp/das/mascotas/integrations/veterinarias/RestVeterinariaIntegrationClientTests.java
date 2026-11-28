package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RestVeterinariaIntegrationClientTests {

    @Test
    void testClienteRestEnviaApiKeyYParseaMascotasAcusesYAtenciones() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        RestVeterinariaIntegrationClient client = new RestVeterinariaIntegrationClient(() -> builder);
        VeterinariaConfiguracionBE config = new VeterinariaConfiguracionBE();
        config.setIdVeterinaria(1);
        config.setUrlServicio("http://localhost:8081/api/veterinaria");
        config.setTecnologia(TecnologiaIntegracion.REST);
        config.setApiKey("API_TEST_123");
        config.setParametros("timeout=3000;format=json");

        LoteRangoFechasRequestBE filtro = new LoteRangoFechasRequestBE();
        filtro.setFechaDesde(LocalDate.of(2026, 10, 1));
        filtro.setFechaHasta(LocalDate.of(2026, 11, 1));
        filtro.setPagina(1);
        filtro.setTamanioPagina(50);

        server.expect(requestTo("http://localhost:8081/api/veterinaria/mascotas-sin-nrm?fechaDesde=2026-10-01&fechaHasta=2026-11-01&pagina=1&tamanioPagina=50"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-API-KEY", "API_TEST_123"))
                .andRespond(withSuccess("""
                        {"items":[{"idMascotaExterna":"SA-1","nombre":"Nala","sexo":"H","añoNacimiento":2024,"especie":"Perro","castrado":false}],"pagina":1,"tamanioPagina":50,"totalElementos":1,"totalPaginas":1}
                        """, MediaType.APPLICATION_JSON));

        server.expect(requestTo("http://localhost:8081/api/veterinaria/nrm-asignados"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-API-KEY", "API_TEST_123"))
                .andRespond(withSuccess("""
                        [{"idMascotaExterna":"SA-1","nroRegMunicipal":100007,"confirmado":true,"mensaje":"OK"}]
                        """, MediaType.APPLICATION_JSON));

        server.expect(requestTo("http://localhost:8081/api/veterinaria/atenciones?fechaDesde=2026-10-01&fechaHasta=2026-11-01&pagina=1&tamanioPagina=50"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-API-KEY", "API_TEST_123"))
                .andRespond(withSuccess("""
                        {"items":[{"idMascotaExterna":"SA-1","nroRegMunicipal":100007,"nroAtencion":1,"nroDetalle":1,"fechaAtencion":"2026-10-15","tipoPractica":"Vacunación","detalle":"Antirrábica"}],"pagina":1,"tamanioPagina":50,"totalElementos":1,"totalPaginas":1}
                        """, MediaType.APPLICATION_JSON));

        PageResponseBE<MascotaExternaBE> mascotas = client.obtenerMascotasSinNrm(config, filtro);
        assertEquals(1, mascotas.getItems().size());
        assertEquals("Nala", mascotas.getItems().get(0).getNombre());

        List<NrmAcuseBE> acuses = client.informarNrm(config, List.of(new NrmAsignadoBE("SA-1", 100007)));
        assertEquals(1, acuses.size());
        assertTrue(acuses.get(0).isConfirmado());

        PageResponseBE<PracticaSanitariaExternaBE> atenciones = client.obtenerAtencionesSanitarias(config, filtro);
        assertEquals(1, atenciones.getItems().size());
        assertEquals(LocalDate.of(2026, 10, 15), atenciones.getItems().get(0).getFechaAtencion());

        server.verify();
    }
}
