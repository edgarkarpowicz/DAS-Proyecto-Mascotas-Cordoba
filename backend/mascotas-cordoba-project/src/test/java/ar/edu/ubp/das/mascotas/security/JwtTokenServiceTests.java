package ar.edu.ubp.das.mascotas.security;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTests {

    private static final String TEST_SECRET = "test-jwt-secret-key-mascotas-cba";

    private UsuarioRefugioBE buildRefugioUser() {
        UsuarioRefugioBE usuario = new UsuarioRefugioBE();
        usuario.setIdCiudadano(3);
        usuario.setNombre("Roberto");
        usuario.setApellido("Sánchez");
        usuario.setCuil("20-25999000-5");
        usuario.setCorreo("roberto.sanchez@patitasfelices.org");
        usuario.setPerfil("REFUGIO");
        usuario.setIdRefugio(1);
        usuario.setNombreRefugio("Refugio Patitas Felices");
        return usuario;
    }

    @Test
    void testGenerarYValidarJwtRestauraIdentidadYContexto() {
        JwtTokenService service = new JwtTokenService(TEST_SECRET, 60);
        UsuarioRefugioBE usuario = buildRefugioUser();

        String jwt = service.generarToken(usuario);
        assertEquals(3, jwt.split("\\.").length);

        Optional<SesionUsuarioBE> result = service.validarToken(jwt);
        assertTrue(result.isPresent());
        SesionUsuarioBE sesion = result.get();
        assertEquals(3, sesion.getIdCiudadano());
        assertEquals("20-25999000-5", sesion.getCuil());
        assertEquals("Roberto", sesion.getNombre());
        assertEquals("Sánchez", sesion.getApellido());
        assertEquals("roberto.sanchez@patitasfelices.org", sesion.getCorreo());
        assertEquals("REFUGIO", sesion.getPerfil());
        assertEquals(1, sesion.getIdRefugio());
        assertEquals("Refugio Patitas Felices", sesion.getNombreRefugio());
        assertNull(sesion.getIdVeterinaria());
        assertTrue(Boolean.TRUE.equals(sesion.getActiva()));
    }

    @Test
    void testRechazaTokenAlteradoOConOtraFirma() {
        JwtTokenService issuer = new JwtTokenService(TEST_SECRET, 60);
        JwtTokenService otherVerifier = new JwtTokenService("another-secret-key", 60);

        String jwt = issuer.generarToken(buildRefugioUser());
        String[] parts = jwt.split("\\.");

        String tamperedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"jti\":\"x\",\"idCiudadano\":3,\"perfil\":\"MUNICIPALIDAD\",\"exp\":9999999999}"
                        .getBytes(StandardCharsets.UTF_8)
        );
        String tamperedJwt = parts[0] + "." + tamperedPayload + "." + parts[2];

        assertTrue(issuer.validarToken(tamperedJwt).isEmpty());
        assertTrue(otherVerifier.validarToken(jwt).isEmpty());
        assertTrue(issuer.validarToken("not.a.jwt").isEmpty());
        assertTrue(issuer.validarToken("   ").isEmpty());
    }

    @Test
    void testRechazaTokenExpirado() {
        AtomicReference<Instant> currentInstant = new AtomicReference<>(Instant.parse("2026-11-13T12:00:00Z"));
        Clock mutableClock = new Clock() {
            @Override
            public java.time.ZoneId getZone() { return ZoneOffset.UTC; }
            @Override
            public Clock withZone(java.time.ZoneId zone) { return this; }
            @Override
            public Instant instant() { return currentInstant.get(); }
        };

        JwtTokenService service = new JwtTokenService(TEST_SECRET, 10, mutableClock);
        String jwt = service.generarToken(buildRefugioUser());

        assertTrue(service.validarToken(jwt).isPresent());

        currentInstant.set(Instant.parse("2026-11-13T12:11:00Z"));
        assertTrue(service.validarToken(jwt).isEmpty());
    }

    @Test
    void testRevocarTokenInvalidaJwtInmediatamente() {
        JwtTokenService service = new JwtTokenService(TEST_SECRET, 60);
        String jwt = service.generarToken(buildRefugioUser());

        assertTrue(service.validarToken(jwt).isPresent());

        service.revocarToken(jwt);

        assertTrue(service.validarToken(jwt).isEmpty());
    }
}

