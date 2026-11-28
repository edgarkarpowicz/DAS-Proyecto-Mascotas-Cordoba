package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.AtencionSanitariaBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.CarnetSanitarioBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.RegistrarAtencionRequestBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.TipoAtencionBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.ValidacionCarnetBE;
import ar.edu.ubp.das.mascotas.repositories.sanidad.SanidadRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import ar.edu.ubp.das.mascotas.services.sanidad.SanidadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class SanidadServiceTests {

    @Mock
    private SanidadRepository sanidadRepository;

    @Spy
    private AuthorizationService authorizationService = new AuthorizationService();

    @InjectMocks
    private SanidadService sanidadService;

    private TipoAtencionBE tipoVacunacion;

    @BeforeEach
    void setUp() {
        tipoVacunacion = new TipoAtencionBE();
        tipoVacunacion.setCodTipoAtencion(1);
        tipoVacunacion.setDescTipoAtencion("Vacunación");
    }

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testRegistrarAtencionVeterinariaFuerzaSuPropiaClinicaYRechazaSuplantacion() {
        SesionUsuarioBE vet = buildSesion(PerfilUsuario.VETERINARIA, 5, null, 1);
        UserContextHolder.set(vet);

        RegistrarAtencionRequestBE request = new RegistrarAtencionRequestBE();
        request.setNroRegMunicipal(100001);
        request.setCodTipoAtencion(1);
        request.setDetalleAtencion("Antirrábica anual Lote 2026");
        request.setFechaAtencion(LocalDate.of(2026, 10, 1));
        request.setFechaVencimiento(LocalDate.of(2027, 10, 1));
        request.setIdVeterinaria(2);

        ResponseStatusException forbidden = assertThrows(ResponseStatusException.class,
                () -> sanidadService.registrarAtencion(request));
        assertEquals(HttpStatus.FORBIDDEN, forbidden.getStatusCode());

        request.setIdVeterinaria(null);
        when(sanidadRepository.getTiposAtencion()).thenReturn(List.of(tipoVacunacion));
        when(sanidadRepository.findMascotaByNrm(100001)).thenReturn(Optional.of(buildMascota(100001, 1, null, true)));
        AtencionSanitariaBE expected = new AtencionSanitariaBE();
        expected.setNroRegMunicipal(100001);
        expected.setNroRegistro(3);
        expected.setIdVeterinaria(1);
        when(sanidadRepository.registrarAtencion(eq(100001), eq(LocalDate.of(2026, 10, 1)), eq(1),
                eq("Antirrábica anual Lote 2026"), eq(LocalDate.of(2027, 10, 1)), eq(1), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(expected);

        AtencionSanitariaBE result = sanidadService.registrarAtencion(request);
        assertEquals(100001, result.getNroRegMunicipal());
        assertEquals(1, result.getIdVeterinaria());
    }

    @Test
    void testRegistrarAtencionRechazaVencimientoAnteriorYTipoInvalido() {
        UserContextHolder.set(buildSesion(PerfilUsuario.MUNICIPALIDAD, 6, null, null));

        RegistrarAtencionRequestBE vencimientoInvalido = new RegistrarAtencionRequestBE();
        vencimientoInvalido.setNroRegMunicipal(100001);
        vencimientoInvalido.setCodTipoAtencion(1);
        vencimientoInvalido.setDetalleAtencion("Vacuna");
        vencimientoInvalido.setFechaAtencion(LocalDate.of(2026, 10, 10));
        vencimientoInvalido.setFechaVencimiento(LocalDate.of(2026, 10, 5));
        vencimientoInvalido.setIdVeterinaria(1);

        ResponseStatusException badDate = assertThrows(ResponseStatusException.class,
                () -> sanidadService.registrarAtencion(vencimientoInvalido));
        assertEquals(HttpStatus.BAD_REQUEST, badDate.getStatusCode());

        vencimientoInvalido.setFechaVencimiento(LocalDate.of(2027, 10, 10));
        vencimientoInvalido.setCodTipoAtencion(99);
        when(sanidadRepository.getTiposAtencion()).thenReturn(List.of(tipoVacunacion));

        ResponseStatusException badTipo = assertThrows(ResponseStatusException.class,
                () -> sanidadService.registrarAtencion(vencimientoInvalido));
        assertEquals(HttpStatus.BAD_REQUEST, badTipo.getStatusCode());
    }

    @Test
    void testGenerarYConsultarCarnetRespetaPropiedadDelCiudadano() {
        UserContextHolder.set(buildSesion(PerfilUsuario.CIUDADANO, 1, null, null));
        when(sanidadRepository.findMascotaByNrm(100002)).thenReturn(Optional.of(buildMascota(100002, 2, null, true)));

        ResponseStatusException forbidden = assertThrows(ResponseStatusException.class,
                () -> sanidadService.generarCarnet(100002));
        assertEquals(HttpStatus.FORBIDDEN, forbidden.getStatusCode());

        MascotaBE firulais = buildMascota(100001, 1, null, true);
        firulais.setEstadoSanitario("Al día");
        when(sanidadRepository.findMascotaByNrm(100001)).thenReturn(Optional.of(firulais));

        CarnetSanitarioBE emitted = new CarnetSanitarioBE();
        emitted.setNroRegMunicipal(100001);
        emitted.setCodigoValidacion("CBA-100001-ABCD1234");
        emitted.setNombreMascota("Firulais");
        emitted.setVigente(true);
        when(sanidadRepository.emitirCarnet(eq(100001), argThat(c -> c.startsWith("CBA-100001-")), eq(1)))
                .thenReturn(Optional.of(emitted));
        when(sanidadRepository.getAtencionesPorMascota(100001)).thenReturn(List.of(new AtencionSanitariaBE()));

        CarnetSanitarioBE result = sanidadService.generarCarnet(100001);
        assertTrue(result.isVigente());
        assertEquals("Al día", result.getEstadoSanitario());
        assertEquals(1, result.getAtenciones().size());
    }

    @Test
    void testValidarCarnetPublicoDistingueVigenteReemplazadoEInexistente() {
        UserContextHolder.clear();

        when(sanidadRepository.findCarnetByCodigo("INVALIDO")).thenReturn(Optional.empty());
        ValidacionCarnetBE inexistente = sanidadService.validarCarnet("INVALIDO");
        assertFalse(inexistente.isAutentico());
        assertFalse(inexistente.isVigente());

        CarnetSanitarioBE reemplazado = new CarnetSanitarioBE();
        reemplazado.setNroRegMunicipal(100001);
        reemplazado.setCodigoValidacion("CBA-100001-OLD");
        reemplazado.setNombreMascota("Firulais");
        reemplazado.setVigente(false);
        reemplazado.setVive(true);
        when(sanidadRepository.findCarnetByCodigo("CBA-100001-OLD")).thenReturn(Optional.of(reemplazado));

        ValidacionCarnetBE resReemplazado = sanidadService.validarCarnet("CBA-100001-OLD");
        assertTrue(resReemplazado.isAutentico());
        assertFalse(resReemplazado.isVigente());

        CarnetSanitarioBE vigente = new CarnetSanitarioBE();
        vigente.setNroRegMunicipal(100001);
        vigente.setCodigoValidacion("CBA-100001-SEED01");
        vigente.setNombreMascota("Firulais");
        vigente.setVigente(true);
        vigente.setVive(true);
        MascotaBE firulais = buildMascota(100001, 1, null, true);
        firulais.setEstadoSanitario("Al día");
        when(sanidadRepository.findCarnetByCodigo("CBA-100001-SEED01")).thenReturn(Optional.of(vigente));
        when(sanidadRepository.findMascotaByNrm(100001)).thenReturn(Optional.of(firulais));

        ValidacionCarnetBE resVigente = sanidadService.validarCarnet("CBA-100001-SEED01");
        assertTrue(resVigente.isAutentico());
        assertTrue(resVigente.isVigente());
        assertEquals("Al día", resVigente.getEstadoSanitario());
    }

    private SesionUsuarioBE buildSesion(PerfilUsuario perfil, int idCiudadano, Integer idRefugio, Integer idVet) {
        SesionUsuarioBE s = new SesionUsuarioBE();
        s.setIdCiudadano(idCiudadano);
        s.setCuil("20-12345678-9");
        s.setPerfil(perfil.name());
        s.setIdRefugio(idRefugio);
        s.setIdVeterinaria(idVet);
        return s;
    }

    private MascotaBE buildMascota(int nrm, Integer idResp, Integer idRef, boolean vive) {
        MascotaBE m = new MascotaBE();
        m.setNroRegMunicipal(nrm);
        m.setNombre("Firulais");
        m.setIdResponsable(idResp);
        m.setIdRefugio(idRef);
        m.setVive(vive);
        return m;
    }
}
