package ar.edu.ubp.das.mascotas.security;

import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.auth.UsuarioRefugioBE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Emite, valida y revoca tokens JWT firmados con HMAC-SHA256 (HS256) sin estado en BD (RF15).
 */
@Service
public class JwtTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final SecretKeySpec signingKey;
    private final long expirationMinutes;
    private final Clock clock;
    private final ConcurrentHashMap<String, Long> revokedTokens = new ConcurrentHashMap<>();

    @Autowired
    public JwtTokenService(
            @Value("${security.jwt.secret:MascotasCordoba-DAS-UBP-2026-HMAC-SHA256-Secret-Key}") String secret,
            @Value("${security.jwt.expiration-minutes:480}") long expirationMinutes) {
        this(secret, expirationMinutes, Clock.systemUTC());
    }

    JwtTokenService(String secret, long expirationMinutes, Clock clock) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("security.jwt.secret no puede estar vacío");
        }
        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException("security.jwt.expiration-minutes debe ser positivo");
        }
        this.signingKey = deriveKey(secret.trim());
        this.expirationMinutes = expirationMinutes;
        this.clock = clock;
    }

    public String generarToken(UsuarioRefugioBE usuario) {
        if (usuario == null || usuario.getIdCiudadano() == null) {
            throw new IllegalArgumentException("Los datos del usuario autenticado son obligatorios para generar JWT");
        }

        long nowEpoch = clock.instant().getEpochSecond();
        long expEpoch = nowEpoch + (expirationMinutes * 60L);

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("sub", String.valueOf(usuario.getIdCiudadano()));
        claims.put("idCiudadano", usuario.getIdCiudadano());
        claims.put("cuil", usuario.getCuil());
        claims.put("nombre", usuario.getNombre());
        claims.put("apellido", usuario.getApellido());
        claims.put("correo", usuario.getCorreo());
        claims.put("perfil", usuario.getPerfil() != null ? usuario.getPerfil().trim().toUpperCase() : null);
        claims.put("idRefugio", usuario.getIdRefugio());
        claims.put("nombreRefugio", usuario.getNombreRefugio());
        claims.put("idVeterinaria", usuario.getIdVeterinaria());
        claims.put("nombreVeterinaria", usuario.getNombreVeterinaria());
        claims.put("iat", nowEpoch);
        claims.put("exp", expEpoch);

        String headerB64 = URL_ENCODER.encodeToString(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
        String payloadB64 = URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(claims));
        String signingInput = headerB64 + "." + payloadB64;
        String signatureB64 = URL_ENCODER.encodeToString(sign(signingInput));
        return signingInput + "." + signatureB64;
    }

    public SesionUsuarioBE construirSesion(String token, UsuarioRefugioBE usuario) {
        SesionUsuarioBE sesion = new SesionUsuarioBE();
        sesion.setToken(token);
        sesion.setIdCiudadano(usuario.getIdCiudadano());
        sesion.setApellido(usuario.getApellido());
        sesion.setNombre(usuario.getNombre());
        sesion.setCuil(usuario.getCuil());
        sesion.setCorreo(usuario.getCorreo());
        sesion.setPerfil(usuario.getPerfil() != null ? usuario.getPerfil().trim().toUpperCase() : null);
        sesion.setIdRefugio(usuario.getIdRefugio());
        sesion.setNombreRefugio(usuario.getNombreRefugio());
        sesion.setIdVeterinaria(usuario.getIdVeterinaria());
        sesion.setNombreVeterinaria(usuario.getNombreVeterinaria());
        return sesion;
    }

    public Optional<SesionUsuarioBE> validarToken(String token) {
        return parseVerifiedClaims(token).map(claims -> {
            SesionUsuarioBE sesion = new SesionUsuarioBE();
            sesion.setToken(token.trim());
            sesion.setIdCiudadano(asInteger(claims.get("idCiudadano")));
            sesion.setApellido(asString(claims.get("apellido")));
            sesion.setNombre(asString(claims.get("nombre")));
            sesion.setCuil(asString(claims.get("cuil")));
            sesion.setCorreo(asString(claims.get("correo")));
            String perfil = asString(claims.get("perfil"));
            sesion.setPerfil(perfil != null ? perfil.toUpperCase() : null);
            sesion.setIdRefugio(asInteger(claims.get("idRefugio")));
            sesion.setNombreRefugio(asString(claims.get("nombreRefugio")));
            sesion.setIdVeterinaria(asInteger(claims.get("idVeterinaria")));
            sesion.setNombreVeterinaria(asString(claims.get("nombreVeterinaria")));
            return sesion;
        });
    }

    public void revocarToken(String token) {
        parseVerifiedClaims(token).ifPresent(claims -> {
            String jti = asString(claims.get("jti"));
            Long exp = asLong(claims.get("exp"));
            if (jti != null && exp != null) {
                pruneExpiredRevocations(clock.instant().getEpochSecond());
                revokedTokens.put(jti, exp);
            }
        });
    }

    private Optional<Map<String, Object>> parseVerifiedClaims(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String trimmed = token.trim();
        String[] parts = trimmed.split("\\.", -1);
        if (parts.length != 3 || parts[0].isEmpty() || parts[1].isEmpty() || parts[2].isEmpty()) {
            return Optional.empty();
        }

        try {
            byte[] headerBytes = URL_DECODER.decode(parts[0]);
            Map<String, Object> header = objectMapper.readValue(headerBytes, MAP_TYPE);
            if (!"HS256".equals(header.get("alg"))) {
                return Optional.empty();
            }

            byte[] actualSig = URL_DECODER.decode(parts[2]);
            byte[] expectedSig = sign(parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expectedSig, actualSig)) {
                return Optional.empty();
            }

            byte[] payloadBytes = URL_DECODER.decode(parts[1]);
            Map<String, Object> claims = objectMapper.readValue(payloadBytes, MAP_TYPE);

            Long exp = asLong(claims.get("exp"));
            Integer idCiudadano = asInteger(claims.get("idCiudadano"));
            String perfil = asString(claims.get("perfil"));
            if (exp == null || idCiudadano == null || perfil == null || perfil.isBlank()) {
                return Optional.empty();
            }

            long nowEpoch = clock.instant().getEpochSecond();
            if (nowEpoch >= exp) {
                return Optional.empty();
            }

            String jti = asString(claims.get("jti"));
            if (jti != null && !revokedTokens.isEmpty()) {
                pruneExpiredRevocations(nowEpoch);
                if (revokedTokens.containsKey(jti)) {
                    return Optional.empty();
                }
            }

            return Optional.of(claims);
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private void pruneExpiredRevocations(long nowEpoch) {
        revokedTokens.entrySet().removeIf(entry -> entry.getValue() <= nowEpoch);
    }

    private byte[] sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No se pudo calcular la firma HMAC-SHA256 del JWT", ex);
        }
    }

    private static SecretKeySpec deriveKey(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No se pudo derivar la clave HMAC-SHA256", ex);
        }
    }

    private static String asString(Object value) {
        return value != null ? String.valueOf(value) : null;
    }

    private static Integer asInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private static Long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }
}

