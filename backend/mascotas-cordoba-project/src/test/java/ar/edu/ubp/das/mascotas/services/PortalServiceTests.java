package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.portal.PortalInfoBE;
import ar.edu.ubp.das.mascotas.BE.portal.ResumenProgramaBE;
import ar.edu.ubp.das.mascotas.repositories.portal.PortalRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.portal.PortalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortalServiceTests {

    @Mock
    private PortalRepository portalRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private PortalService portalService;

    @Test
    void testConsultarPortalAnonimoRetornaResumenYServiciosPublicosSinServiciosDePerfil() {
        when(portalRepository.getResumenPortal()).thenReturn(new ResumenProgramaBE(4, 3, 2));
        when(authorizationService.getOptionalAuthenticated()).thenReturn(Optional.empty());

        PortalInfoBE info = portalService.consultarServiciosHabilitados();

        assertFalse(info.isAutenticado());
        assertNull(info.getPerfil());
        assertNull(info.getUsuario());
        assertEquals(4, info.getResumen().getPublicacionesActivas());
        assertEquals(3, info.getResumen().getVeterinariasHabilitadas());
        assertEquals(2, info.getResumen().getRefugiosAdheridos());
        assertEquals(5, info.getServiciosPublicos().size());
        assertTrue(info.getServiciosHabilitados().isEmpty());
    }

    @Test
    void testConsultarPortalConRefugioHabilitaPublicacionesComoDisponible() {
        when(portalRepository.getResumenPortal()).thenReturn(new ResumenProgramaBE(2, 3, 2));
        SesionUsuarioBE sesion = buildSesion(PerfilUsuario.REFUGIO);
        sesion.setIdRefugio(1);
        sesion.setNombreRefugio("Refugio Patitas Felices");
        when(authorizationService.getOptionalAuthenticated()).thenReturn(Optional.of(sesion));

        PortalInfoBE info = portalService.consultarServiciosHabilitados();

        assertTrue(info.isAutenticado());
        assertEquals("REFUGIO", info.getPerfil());
        assertNotNull(info.getUsuario());
        assertEquals(1, info.getUsuario().getIdRefugio());
        assertTrue(info.getServiciosHabilitados().stream()
                .anyMatch(s -> "PUBLICACIONES_ADOPCION".equals(s.getCodigo())
                        && "/publicaciones".equals(s.getRuta())
                        && s.isDisponible()));
    }

    @Test
    void testConsultarPortalConOtrosPerfilesPersonalizaServicios() {
        when(portalRepository.getResumenPortal()).thenReturn(new ResumenProgramaBE(1, 3, 2));

        when(authorizationService.getOptionalAuthenticated()).thenReturn(Optional.of(buildSesion(PerfilUsuario.CIUDADANO)));
        PortalInfoBE ciudadano = portalService.consultarServiciosHabilitados();
        assertTrue(ciudadano.getServiciosHabilitados().stream().anyMatch(s -> "MIS_MASCOTAS".equals(s.getCodigo())));

        when(authorizationService.getOptionalAuthenticated()).thenReturn(Optional.of(buildSesion(PerfilUsuario.VETERINARIA)));
        PortalInfoBE vet = portalService.consultarServiciosHabilitados();
        assertTrue(vet.getServiciosHabilitados().stream().anyMatch(s -> "REGISTRO_MASCOTAS".equals(s.getCodigo())));

        when(authorizationService.getOptionalAuthenticated()).thenReturn(Optional.of(buildSesion(PerfilUsuario.MUNICIPALIDAD)));
        PortalInfoBE muni = portalService.consultarServiciosHabilitados();
        assertTrue(muni.getServiciosHabilitados().stream().anyMatch(s -> "CONFIGURAR_INTEGRACIONES".equals(s.getCodigo())));
    }

    private SesionUsuarioBE buildSesion(PerfilUsuario perfil) {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setIdCiudadano(10);
        sesion.setCuil("20-12345678-9");
        sesion.setNombre("Ana");
        sesion.setApellido("Pérez");
        sesion.setPerfil(perfil.name());
        return sesion;
    }
}
