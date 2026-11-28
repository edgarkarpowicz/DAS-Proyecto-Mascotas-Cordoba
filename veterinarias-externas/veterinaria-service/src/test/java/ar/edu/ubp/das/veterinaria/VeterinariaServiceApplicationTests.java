package ar.edu.ubp.das.veterinaria;

import ar.edu.ubp.das.veterinaria.model.NrmAcuseDTO;
import ar.edu.ubp.das.veterinaria.model.NrmAsignadoDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaMascotasDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaPracticasDTO;
import ar.edu.ubp.das.veterinaria.service.ClinicaStoreService;
import ar.edu.ubp.das.veterinaria.soap.VeterinariaSoapEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VeterinariaServiceApplicationTests {

    private static final String API_KEY_SAN_ANTONIO = "UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClinicaStoreService storeService;

    @Autowired
    private VeterinariaSoapEndpoint soapEndpoint;

    @Test
    void testRestExigeApiKeyYRetornaMascotasYConfirmacionNrm() throws Exception {
        mockMvc.perform(get("/api/veterinaria/mascotas-sin-nrm"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/veterinaria/mascotas-sin-nrm")
                        .header("X-API-KEY", API_KEY_SAN_ANTONIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.items[0].añoNacimiento").exists());

        mockMvc.perform(post("/api/veterinaria/nrm-asignados")
                        .header("X-API-KEY", API_KEY_SAN_ANTONIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"idMascotaExterna\":\"SA-M-001\",\"nroRegMunicipal\":100001}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].confirmado").value(true));

        mockMvc.perform(get("/api/veterinaria/atenciones")
                        .header("X-API-KEY", API_KEY_SAN_ANTONIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].nroRegMunicipal").value(100001));
    }

    @Test
    void testSoapEndpointExponeOperacionesContraAlmacen() {
        PaginaMascotasDTO pendientes = soapEndpoint.obtenerMascotasSinNrm(null, null, 1, 10);
        assertFalse(pendientes.getItems().isEmpty());

        String id = pendientes.getItems().get(0).getIdMascotaExterna();
        List<NrmAcuseDTO> acuses = soapEndpoint.informarNrm(List.of(new NrmAsignadoDTO(id, 100999)));
        assertEquals(1, acuses.size());
        assertTrue(acuses.get(0).isConfirmado());

        PaginaPracticasDTO practicas = soapEndpoint.obtenerAtencionesSanitarias(null, null, 1, 10);
        assertTrue(practicas.getTotalElementos() >= 1);
    }
}
