package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.integraciones.ResultadoSincronizacionVeterinariaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.AtencionSanitariaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.TipoAtencionBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.MascotaExternaBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.NrmAcuseBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.NrmAsignadoBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.PracticaSanitariaExternaBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.TecnologiaIntegracion;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaClientRegistry;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaConfiguracionBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaIntegrationClient;
import ar.edu.ubp.das.mascotas.repositories.integraciones.SincronizacionRepository;
import ar.edu.ubp.das.mascotas.repositories.sanidad.SanidadRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import ar.edu.ubp.das.mascotas.services.integraciones.SincronizacionVeterinariasService;
import ar.edu.ubp.das.mascotas.services.mascotas.MascotasService;
import ar.edu.ubp.das.mascotas.services.sanidad.SanidadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SincronizacionVeterinariasServiceTests {

    @Mock
    private SincronizacionRepository sincronizacionRepository;

    @Mock
    private VeterinariaClientRegistry clientRegistry;

    @Mock
    private MascotasService mascotasService;

    @Mock
    private SanidadRepository sanidadRepository;

    @Mock
    private SanidadService sanidadService;

    @Spy
    private AuthorizationService authorizationService = new AuthorizationService();

    @InjectMocks
    private SincronizacionVeterinariasService syncService;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testSincronizarRequierePerfilMunicipalidad() {
        UserContextHolder.set(buildSesion(PerfilUsuario.CIUDADANO));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> syncService.sincronizar(null, null, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testSincronizarAislaFallasEntreClinicasYCompletaCicloNrmYSanidad() {
        UserContextHolder.set(buildSesion(PerfilUsuario.MUNICIPALIDAD));

        TipoAtencionBE vac = new TipoAtencionBE();
        vac.setCodTipoAtencion(1);
        vac.setDescTipoAtencion("Vacunación");
        when(sanidadRepository.getTiposAtencion()).thenReturn(List.of(vac));

        VeterinariaConfiguracionBE vetOk = buildConfig(1, "San Antonio Vet", TecnologiaIntegracion.REST);
        VeterinariaConfiguracionBE vetCaida = buildConfig(3, "Alta Córdoba", TecnologiaIntegracion.SOAP);
        when(sincronizacionRepository.getVeterinariasIntegradas()).thenReturn(List.of(vetOk, vetCaida));

        VeterinariaIntegrationClient restClient = mock(VeterinariaIntegrationClient.class);
        VeterinariaIntegrationClient soapClientCaido = mock(VeterinariaIntegrationClient.class);
        when(clientRegistry.resolve(TecnologiaIntegracion.REST)).thenReturn(restClient);
        when(clientRegistry.resolve(TecnologiaIntegracion.SOAP)).thenReturn(soapClientCaido);

        MascotaExternaBE petNueva = new MascotaExternaBE();
        petNueva.setIdMascotaExterna("SA-1");
        petNueva.setNombre("Nala");
        petNueva.setSexo("H");
        petNueva.setAñoNacimiento(2024);
        petNueva.setEspecie("Perro");

        MascotaExternaBE petDup = new MascotaExternaBE();
        petDup.setIdMascotaExterna("SA-2");
        petDup.setNombre("Firulais");
        petDup.setSexo("M");
        petDup.setAñoNacimiento(2021);
        petDup.setEspecie("Perro");

        PageResponseBE<MascotaExternaBE> mascotasPage = new PageResponseBE<>(
                List.of(petNueva, petDup), 1, 50, 2, 1);
        when(restClient.obtenerMascotasSinNrm(eq(vetOk), any())).thenReturn(mascotasPage);

        MascotaBE guardadaNueva = new MascotaBE();
        guardadaNueva.setNroRegMunicipal(100007);
        guardadaNueva.setNrmReutilizado(false);
        MascotaBE guardadaDup = new MascotaBE();
        guardadaDup.setNroRegMunicipal(100001);
        guardadaDup.setNrmReutilizado(true);
        when(mascotasService.registrarODeduplicarInternal(any(), isNull()))
                .thenReturn(guardadaNueva, guardadaDup);

        when(sincronizacionRepository.getNrmsPendientes(1))
                .thenReturn(List.of(new NrmAsignadoBE("SA-1", 100007), new NrmAsignadoBE("SA-2", 100001)));

        NrmAcuseBE a1 = new NrmAcuseBE();
        a1.setIdMascotaExterna("SA-1");
        a1.setNroRegMunicipal(100007);
        a1.setConfirmado(true);
        NrmAcuseBE a2 = new NrmAcuseBE();
        a2.setIdMascotaExterna("SA-2");
        a2.setNroRegMunicipal(100001);
        a2.setConfirmado(true);
        when(restClient.informarNrm(eq(vetOk), anyList())).thenReturn(List.of(a1, a2));

        PracticaSanitariaExternaBE practica = new PracticaSanitariaExternaBE();
        practica.setIdMascotaExterna("SA-1");
        practica.setNroAtencion(10);
        practica.setNroDetalle(1);
        practica.setFechaAtencion(LocalDate.of(2026, 10, 20));
        practica.setTipoPractica("Vacunacion");
        practica.setDetalle("Antirrábica");
        when(restClient.obtenerAtencionesSanitarias(eq(vetOk), any()))
                .thenReturn(new PageResponseBE<>(List.of(practica), 1, 50, 1, 1));
        when(sincronizacionRepository.getMapeoMascota(1, "SA-1")).thenReturn(Optional.of(100007));
        when(sanidadService.registrarPracticaExternaInternal(
                eq(100007), eq(1), eq(LocalDate.of(2026, 10, 20)), eq(1),
                eq("Antirrábica"), isNull(), isNull(), isNull(), eq(10), eq(1)))
                .thenReturn(new AtencionSanitariaBE());

        when(soapClientCaido.obtenerMascotasSinNrm(eq(vetCaida), any()))
                .thenThrow(new RuntimeException("Connection refused"));

        List<ResultadoSincronizacionVeterinariaBE> resultados = syncService.sincronizar(
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 28), null);

        assertEquals(2, resultados.size());

        ResultadoSincronizacionVeterinariaBE r1 = resultados.get(0);
        assertEquals("EXITO", r1.getEstado());
        assertEquals(1, r1.getMascotasNuevas());
        assertEquals(1, r1.getMascotasDeduplicadas());
        assertEquals(2, r1.getNrmsConfirmados());
        assertEquals(1, r1.getAtencionesProcesadas());
        verify(sincronizacionRepository).marcarNrmConfirmado(1, "SA-1");
        verify(sincronizacionRepository).marcarNrmConfirmado(1, "SA-2");

        ResultadoSincronizacionVeterinariaBE r2 = resultados.get(1);
        assertEquals("ERROR", r2.getEstado());
        assertTrue(r2.getMensajeError().contains("Connection refused"));
    }

    private static SesionUsuarioBE buildSesion(PerfilUsuario perfil) {
        SesionUsuarioBE s = new SesionUsuarioBE();
        s.setIdCiudadano(6);
        s.setCuil("27-22333444-1");
        s.setPerfil(perfil.name());
        return s;
    }

    private static VeterinariaConfiguracionBE buildConfig(int id, String nombre, TecnologiaIntegracion tec) {
        VeterinariaConfiguracionBE c = new VeterinariaConfiguracionBE();
        c.setIdVeterinaria(id);
        c.setNombreVeterinaria(nombre);
        c.setTecnologia(tec);
        c.setUrlServicio("http://localhost:808" + id);
        return c;
    }
}
