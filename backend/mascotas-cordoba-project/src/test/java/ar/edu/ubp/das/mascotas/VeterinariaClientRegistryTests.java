package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.LoteRangoFechasRequestBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.MascotaExternaBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.NrmAcuseBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.NrmAsignadoBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.PracticaSanitariaExternaBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.TecnologiaIntegracion;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaClientRegistry;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaConfiguracionBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaIntegrationClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VeterinariaClientRegistryTests {

    @Test
    @SuppressWarnings("unchecked")
    void testResuelveClientePorTecnologiaYLanza501SiFalta() {
        VeterinariaIntegrationClient restClient = new StubVetClient(TecnologiaIntegracion.REST);

        ObjectProvider<List<VeterinariaIntegrationClient>> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable(any(Supplier.class))).thenReturn(List.of(restClient));

        VeterinariaClientRegistry registry = new VeterinariaClientRegistry(provider);

        assertTrue(registry.supports(TecnologiaIntegracion.REST));
        assertFalse(registry.supports(TecnologiaIntegracion.SOAP));
        assertSame(restClient, registry.resolve(TecnologiaIntegracion.REST));

        ResponseStatusException notImpl = assertThrows(ResponseStatusException.class,
                () -> registry.resolve(TecnologiaIntegracion.SOAP));
        assertEquals(HttpStatus.NOT_IMPLEMENTED, notImpl.getStatusCode());

        ResponseStatusException badReq = assertThrows(ResponseStatusException.class,
                () -> registry.resolve(null));
        assertEquals(HttpStatus.BAD_REQUEST, badReq.getStatusCode());
    }

    private static final class StubVetClient implements VeterinariaIntegrationClient {
        private final TecnologiaIntegracion tecnologia;

        private StubVetClient(TecnologiaIntegracion tecnologia) {
            this.tecnologia = tecnologia;
        }

        @Override
        public TecnologiaIntegracion getTecnologia() {
            return tecnologia;
        }

        @Override
        public PageResponseBE<MascotaExternaBE> obtenerMascotasSinNrm(VeterinariaConfiguracionBE config, LoteRangoFechasRequestBE filtro) {
            return new PageResponseBE<>();
        }

        @Override
        public List<NrmAcuseBE> informarNrm(VeterinariaConfiguracionBE config, List<NrmAsignadoBE> asignaciones) {
            return List.of();
        }

        @Override
        public PageResponseBE<PracticaSanitariaExternaBE> obtenerAtencionesSanitarias(VeterinariaConfiguracionBE config, LoteRangoFechasRequestBE filtro) {
            return new PageResponseBE<>();
        }
    }
}
