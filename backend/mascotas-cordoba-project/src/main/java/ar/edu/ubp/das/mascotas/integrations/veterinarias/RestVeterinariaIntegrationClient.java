package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Conector HTTP REST hacia sistemas externos de veterinarias adheridas (RF06 / RF09 / RNF09).
 */
@Component
public class RestVeterinariaIntegrationClient implements VeterinariaIntegrationClient {

    private static final int DEFAULT_TIMEOUT_MS = 5000;

    private final Supplier<RestClient.Builder> builderFactory;
    private final boolean applyCustomTimeoutFactory;

    @Autowired
    public RestVeterinariaIntegrationClient() {
        this(RestClient::builder, true);
    }

    RestVeterinariaIntegrationClient(Supplier<RestClient.Builder> builderFactory) {
        this(builderFactory, false);
    }

    private RestVeterinariaIntegrationClient(
            Supplier<RestClient.Builder> builderFactory,
            boolean applyCustomTimeoutFactory) {
        this.builderFactory = builderFactory;
        this.applyCustomTimeoutFactory = applyCustomTimeoutFactory;
    }

    @Override
    public TecnologiaIntegracion getTecnologia() {
        return TecnologiaIntegracion.REST;
    }

    @Override
    public PageResponseBE<MascotaExternaBE> obtenerMascotasSinNrm(
            VeterinariaConfiguracionBE config,
            LoteRangoFechasRequestBE filtro) {
        String uri = buildPagedUri(normalizeBaseUrl(config) + "/mascotas-sin-nrm", filtro);
        PageResponseBE<MascotaExternaBE> response = buildClient(config).get()
                .uri(uri)
                .header("X-API-KEY", safeApiKey(config))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response != null ? response : new PageResponseBE<>();
    }

    @Override
    public List<NrmAcuseBE> informarNrm(
            VeterinariaConfiguracionBE config,
            List<NrmAsignadoBE> asignaciones) {
        if (asignaciones == null || asignaciones.isEmpty()) {
            return Collections.emptyList();
        }
        String uri = normalizeBaseUrl(config) + "/nrm-asignados";
        List<NrmAcuseBE> response = buildClient(config).post()
                .uri(uri)
                .header("X-API-KEY", safeApiKey(config))
                .contentType(MediaType.APPLICATION_JSON)
                .body(asignaciones)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response != null ? response : Collections.emptyList();
    }

    @Override
    public PageResponseBE<PracticaSanitariaExternaBE> obtenerAtencionesSanitarias(
            VeterinariaConfiguracionBE config,
            LoteRangoFechasRequestBE filtro) {
        String uri = buildPagedUri(normalizeBaseUrl(config) + "/atenciones", filtro);
        PageResponseBE<PracticaSanitariaExternaBE> response = buildClient(config).get()
                .uri(uri)
                .header("X-API-KEY", safeApiKey(config))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response != null ? response : new PageResponseBE<>();
    }

    protected RestClient buildClient(VeterinariaConfiguracionBE config) {
        RestClient.Builder builder = builderFactory.get();
        if (applyCustomTimeoutFactory) {
            int timeoutMs = parseTimeoutMs(config != null ? config.getParametros() : null);
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(Duration.ofMillis(timeoutMs));
            requestFactory.setReadTimeout(Duration.ofMillis(timeoutMs));
            builder.requestFactory(requestFactory);
        }
        return builder.build();
    }

    private static String normalizeBaseUrl(VeterinariaConfiguracionBE config) {
        if (config == null || config.getUrlServicio() == null || config.getUrlServicio().isBlank()) {
            throw new IllegalArgumentException("URL de servicio REST de la veterinaria no configurada");
        }
        String url = config.getUrlServicio().trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String buildPagedUri(String path, LoteRangoFechasRequestBE filtro) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);
        if (filtro != null) {
            if (filtro.getFechaDesde() != null) {
                builder.queryParam("fechaDesde", filtro.getFechaDesde().toString());
            }
            if (filtro.getFechaHasta() != null) {
                builder.queryParam("fechaHasta", filtro.getFechaHasta().toString());
            }
            if (filtro.getPagina() != null) {
                builder.queryParam("pagina", filtro.getPagina());
            }
            if (filtro.getTamanioPagina() != null) {
                builder.queryParam("tamanioPagina", filtro.getTamanioPagina());
            }
        }
        return builder.toUriString();
    }

    private static String safeApiKey(VeterinariaConfiguracionBE config) {
        return config != null && config.getApiKey() != null ? config.getApiKey().trim() : "";
    }

    static int parseTimeoutMs(String parametros) {
        if (parametros == null || parametros.isBlank()) {
            return DEFAULT_TIMEOUT_MS;
        }
        for (String pair : parametros.split(";")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && "timeout".equalsIgnoreCase(kv[0].trim())) {
                try {
                    int parsed = Integer.parseInt(kv[1].trim());
                    return parsed > 0 ? parsed : DEFAULT_TIMEOUT_MS;
                } catch (NumberFormatException ignored) {
                    return DEFAULT_TIMEOUT_MS;
                }
            }
        }
        return DEFAULT_TIMEOUT_MS;
    }
}
