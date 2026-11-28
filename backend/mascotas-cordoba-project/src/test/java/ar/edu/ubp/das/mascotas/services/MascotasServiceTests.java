package ar.edu.ubp.das.mascotas.services;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.common.PageRequestBE;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.RegistrarMascotaRequestBE;
import ar.edu.ubp.das.mascotas.repositories.mascotas.MascotasRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.security.UserContextHolder;
import ar.edu.ubp.das.mascotas.services.mascotas.MascotasService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotasServiceTests {

    @Mock
    private MascotasRepository mascotasRepository;

    @Spy
    private AuthorizationService authorizationService = new AuthorizationService();

    @InjectMocks
    private MascotasService mascotasService;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void testRegistrarMascotaReutilizaNrmPorMicrochipSinDuplicarAlta() {
        UserContextHolder.set(buildSesion(PerfilUsuario.VETERINARIA, 5, null));
        MascotaBE existente = new MascotaBE();
        existente.setNroRegMunicipal(100001);
        existente.setNombre("Firulais");
        when(mascotasRepository.buscarCiudadanoPorCuil("20-28555666-3")).thenReturn(Optional.of(1));
        when(mascotasRepository.buscarPorMicrochip("CHIP-01")).thenReturn(Optional.of(existente));

        RegistrarMascotaRequestBE req = baseRequest();
        req.setMicrochip(" CHIP-01 ");
        req.setCuilResponsable("20-28555666-3");

        MascotaBE res = mascotasService.registrarMascota(req);

        assertEquals(100001, res.getNroRegMunicipal());
        assertTrue(res.isNrmReutilizado());
        verify(mascotasRepository, never()).insertarMascotaRegistroUnico(any(), any(), anyInt(), any(), anyBoolean(), any(), any(), any(), any());
    }

    @Test
    void testRegistrarMascotaReutilizaPorCoincidenciaCuandoNoHayMicrochip() {
        UserContextHolder.set(buildSesion(PerfilUsuario.MUNICIPALIDAD, 6, null));
        MascotaBE existente = new MascotaBE();
        existente.setNroRegMunicipal(100003);
        when(mascotasRepository.buscarCiudadanoPorCuil("20-28555666-3")).thenReturn(Optional.of(1));
        when(mascotasRepository.buscarPorCoincidencia(1, null, "Luna", "H", 2023, "Gato"))
                .thenReturn(Optional.of(existente));

        RegistrarMascotaRequestBE req = new RegistrarMascotaRequestBE();
        req.setNombre("Luna");
        req.setSexo("Hembra");
        req.setAñoNacimiento(2023);
        req.setEspecie("Gato");
        req.setCuilResponsable("20-28555666-3");

        MascotaBE res = mascotasService.registrarMascota(req);

        assertEquals(100003, res.getNroRegMunicipal());
        assertTrue(res.isNrmReutilizado());
        verify(mascotasRepository, never()).insertarMascotaRegistroUnico(any(), any(), anyInt(), any(), anyBoolean(), any(), any(), any(), any());
    }

    @Test
    void testRegistrarMascotaNuevaAsignaNuevoNrm() {
        UserContextHolder.set(buildSesion(PerfilUsuario.VETERINARIA, 5, null));
        when(mascotasRepository.buscarPorMicrochip("CHIP-NEW")).thenReturn(Optional.empty());
        when(mascotasRepository.insertarMascotaRegistroUnico(
                "Toby", "M", 2022, "CHIP-NEW", true, null, null, "Perro", "Labrador"))
                .thenReturn(100007);

        RegistrarMascotaRequestBE req = baseRequest();
        req.setMicrochip("CHIP-NEW");
        req.setCastrado(true);

        MascotaBE res = mascotasService.registrarMascota(req);

        assertEquals(100007, res.getNroRegMunicipal());
        assertFalse(res.isNrmReutilizado());
        assertTrue(Boolean.TRUE.equals(res.getCastrado()));
    }

    @Test
    void testRegistrarMascotaValidaCuilInexistenteYPerfilCiudadano403() {
        UserContextHolder.set(buildSesion(PerfilUsuario.CIUDADANO, 1, null));
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                () -> mascotasService.registrarMascota(baseRequest())).getStatusCode());

        UserContextHolder.set(buildSesion(PerfilUsuario.VETERINARIA, 5, null));
        RegistrarMascotaRequestBE req = baseRequest();
        req.setCuilResponsable("20-99999999-9");
        when(mascotasRepository.buscarCiudadanoPorCuil("20-99999999-9")).thenReturn(Optional.empty());

        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> mascotasService.registrarMascota(req)).getStatusCode());
    }

    @Test
    void testConsultarMisMascotasSoloRetornaLasDelCiudadanoAutenticado() {
        UserContextHolder.set(buildSesion(PerfilUsuario.CIUDADANO, 1, null));
        MascotaBE m = new MascotaBE();
        m.setNroRegMunicipal(100001);
        m.setNombre("Firulais");
        when(mascotasRepository.countMascotasCiudadano(1)).thenReturn(1);
        when(mascotasRepository.getMascotasCiudadano(1, 0, 20)).thenReturn(List.of(m));

        PageResponseBE<MascotaBE> page = mascotasService.consultarMisMascotas(new PageRequestBE());

        assertEquals(1, page.getTotalElementos());
        assertEquals("Firulais", page.getItems().get(0).getNombre());
    }

    private RegistrarMascotaRequestBE baseRequest() {
        RegistrarMascotaRequestBE req = new RegistrarMascotaRequestBE();
        req.setNombre("Toby");
        req.setSexo("M");
        req.setAñoNacimiento(2022);
        req.setEspecie("Perro");
        req.setRaza("Labrador");
        return req;
    }

    private SesionUsuarioBE buildSesion(PerfilUsuario perfil, int idCiudadano, Integer idRefugio) {
        SesionUsuarioBE s = new SesionUsuarioBE();
        s.setIdCiudadano(idCiudadano);
        s.setCuil("20-12345678-9");
        s.setPerfil(perfil.name());
        s.setIdRefugio(idRefugio);
        return s;
    }
}
