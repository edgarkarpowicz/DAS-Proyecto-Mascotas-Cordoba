package ar.edu.ubp.das.mascotas;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.portal.PortalInfoBE;
import ar.edu.ubp.das.mascotas.BE.portal.ResumenProgramaBE;
import ar.edu.ubp.das.mascotas.config.AuthTokenFilter;
import ar.edu.ubp.das.mascotas.resources.portal.PortalResource;
import ar.edu.ubp.das.mascotas.security.JwtTokenService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import ar.edu.ubp.das.mascotas.services.portal.IPortalService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PortalResourceTests {

    @Mock
    private IPortalService portalService;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private PortalResource portalResource;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testGetPortalDelegaEnElServicio() {
        PortalInfoBE info = new PortalInfoBE();
        info.setNombrePrograma("Programa Municipal");
        info.setResumen(new ResumenProgramaBE(2, 3, 2));
        when(portalService.consultarServiciosHabilitados()).thenReturn(info);

        ResponseEntity<PortalInfoBE> response = portalResource.getPortal();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getResumen().getPublicacionesActivas());
    }

    @Test
    void testEndpointPublicoPortalConMockMvcResponde200ConTokenInvalido() throws Exception {
        PortalInfoBE info = new PortalInfoBE();
        info.setAutenticado(false);
        info.setResumen(new ResumenProgramaBE(1, 3, 2));
        when(portalService.consultarServiciosHabilitados()).thenReturn(info);
        when(jwtTokenService.validarToken("token-vencido")).thenReturn(Optional.empty());

        AuthTokenFilter filter = new AuthTokenFilter();
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "jwtTokenService", jwtTokenService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(portalResource)
                .addFilters(filter)
                .build();

        mockMvc.perform(get("/publico/portal")
                        .header("Authorization", "Bearer token-vencido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(false))
                .andExpect(jsonPath("$.resumen.publicacionesActivas").value(1));
    }

    @Test
    void testEndpointPublicoPortalSinTokenResponde200() throws Exception {
        PortalInfoBE info = new PortalInfoBE();
        info.setAutenticado(false);
        when(portalService.consultarServiciosHabilitados()).thenReturn(info);

        AuthTokenFilter filter = new AuthTokenFilter();
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "jwtTokenService", jwtTokenService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(portalResource)
                .addFilters(filter)
                .build();

        mockMvc.perform(get("/publico/portal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(false));

        verifyNoInteractions(jwtTokenService);
    }

    @Test
    void testEndpointPublicoPortalConTokenValidoExponePerfil() throws Exception {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setIdCiudadano(3);
        sesion.setPerfil("REFUGIO");
        when(jwtTokenService.validarToken("token-ok")).thenReturn(Optional.of(sesion));

        PortalInfoBE info = new PortalInfoBE();
        info.setAutenticado(true);
        info.setPerfil("REFUGIO");
        when(portalService.consultarServiciosHabilitados()).thenReturn(info);

        AuthTokenFilter filter = new AuthTokenFilter();
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "jwtTokenService", jwtTokenService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(portalResource)
                .addFilters(filter)
                .build();

        mockMvc.perform(get("/publico/portal")
                        .header("Authorization", "Bearer token-ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.perfil").value("REFUGIO"));
    }
}
