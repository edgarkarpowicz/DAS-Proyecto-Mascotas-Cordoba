package ar.edu.ubp.das.mascotas.services.integraciones;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import ar.edu.ubp.das.mascotas.BE.integraciones.LoteSincronizacionBE;
import ar.edu.ubp.das.mascotas.BE.integraciones.ResultadoSincronizacionVeterinariaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.MascotaBE;
import ar.edu.ubp.das.mascotas.BE.mascotas.RegistrarMascotaRequestBE;
import ar.edu.ubp.das.mascotas.BE.sanidad.TipoAtencionBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.LoteRangoFechasRequestBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.MascotaExternaBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.NrmAcuseBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.NrmAsignadoBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.PracticaSanitariaExternaBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaClientRegistry;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaConfiguracionBE;
import ar.edu.ubp.das.mascotas.integrations.veterinarias.VeterinariaIntegrationClient;
import ar.edu.ubp.das.mascotas.repositories.integraciones.SincronizacionRepository;
import ar.edu.ubp.das.mascotas.repositories.sanidad.SanidadRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import ar.edu.ubp.das.mascotas.services.mascotas.MascotasService;
import ar.edu.ubp.das.mascotas.services.sanidad.SanidadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Orquestador por lotes para altas/deduplicación de mascotas (RF06) y consolidación sanitaria (RF09)
 * desde sistemas externos de veterinarias REST/SOAP, garantizando aislamiento de fallas (RNF06).
 */
@Service
public class SincronizacionVeterinariasService {

    private static final Logger log = LoggerFactory.getLogger(SincronizacionVeterinariasService.class);
    private static final int TAMANIO_PAGINA_LOTE = 50;

    @Autowired
    private SincronizacionRepository sincronizacionRepository;

    @Autowired
    private VeterinariaClientRegistry clientRegistry;

    @Autowired
    private MascotasService mascotasService;

    @Autowired
    private SanidadRepository sanidadRepository;

    @Autowired
    private SanidadService sanidadService;

    @Autowired
    private AuthorizationService authorizationService;

    @Value("${app.sync.enabled:true}")
    private boolean syncEnabled = true;

    public List<VeterinariaConfiguracionBE> listarClinicas() {
        authorizationService.requirePerfil(PerfilUsuario.MUNICIPALIDAD);
        return sincronizacionRepository.getVeterinariasIntegradas();
    }

    public List<LoteSincronizacionBE> listarUltimosLotes(int top) {
        authorizationService.requirePerfil(PerfilUsuario.MUNICIPALIDAD);
        int safeTop = Math.max(1, Math.min(top, 100));
        return sincronizacionRepository.getUltimosLotes(safeTop);
    }

    public List<ResultadoSincronizacionVeterinariaBE> sincronizar(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer idVeterinaria) {
        authorizationService.requirePerfil(PerfilUsuario.MUNICIPALIDAD);
        List<VeterinariaConfiguracionBE> clinicas = sincronizacionRepository.getVeterinariasIntegradas();

        if (idVeterinaria != null) {
            clinicas = clinicas.stream()
                    .filter(c -> idVeterinaria.equals(c.getIdVeterinaria()))
                    .toList();
            if (clinicas.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró configuración habilitada para la veterinaria #" + idVeterinaria
                );
            }
        }

        return ejecutarClinicas(clinicas, fechaDesde, fechaHasta);
    }

    @Scheduled(cron = "${app.sync.cron:0 0 2 * * *}")
    public void sincronizarProgramado() {
        if (!syncEnabled) {
            return;
        }
        try {
            List<VeterinariaConfiguracionBE> clinicas = sincronizacionRepository.getVeterinariasIntegradas();
            ejecutarClinicas(clinicas, null, null);
        } catch (Exception ex) {
            log.error("Error inesperado en corrida programada de sincronización veterinaria: {}", ex.getMessage(), ex);
        }
    }

    private List<ResultadoSincronizacionVeterinariaBE> ejecutarClinicas(
            List<VeterinariaConfiguracionBE> clinicas,
            LocalDate fechaDesdeSolicitada,
            LocalDate fechaHastaSolicitada) {
        Map<String, Integer> tiposPorNombre = cargarMapaTiposAtencion();
        List<ResultadoSincronizacionVeterinariaBE> resultados = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (VeterinariaConfiguracionBE config : clinicas) {
            LocalDate hasta = fechaHastaSolicitada != null ? fechaHastaSolicitada : hoy;
            LocalDate desde = fechaDesdeSolicitada != null
                    ? fechaDesdeSolicitada
                    : (config.getUltimaFechaProcesada() != null ? config.getUltimaFechaProcesada() : hasta.minusDays(60));
            if (desde.isAfter(hasta)) {
                desde = hasta;
            }
            resultados.add(sincronizarClinicaAislada(config, desde, hasta, tiposPorNombre));
        }
        return resultados;
    }

    private ResultadoSincronizacionVeterinariaBE sincronizarClinicaAislada(
            VeterinariaConfiguracionBE config,
            LocalDate desde,
            LocalDate hasta,
            Map<String, Integer> tiposPorNombre) {
        ResultadoSincronizacionVeterinariaBE resultado = new ResultadoSincronizacionVeterinariaBE();
        resultado.setIdVeterinaria(config.getIdVeterinaria());
        resultado.setNombreVeterinaria(config.getNombreVeterinaria());
        resultado.setTecnologia(config.getTecnologia() != null ? config.getTecnologia().name() : null);
        resultado.setFechaDesde(desde);
        resultado.setFechaHasta(hasta);

        try {
            VeterinariaIntegrationClient client = clientRegistry.resolve(config.getTecnologia());

            // 1. Ingestar mascotas pendientes sin NRM (RF06)
            int mascotasNuevas = 0;
            int mascotasDeduplicadas = 0;
            int pagina = 1;
            int totalPaginas;
            do {
                PageResponseBE<MascotaExternaBE> page = client.obtenerMascotasSinNrm(
                        config,
                        buildFiltro(desde, hasta, pagina)
                );
                totalPaginas = page.getTotalPaginas();
                for (MascotaExternaBE externa : page.getItems()) {
                    RegistrarMascotaRequestBE req = toRegistrarRequest(externa);
                    MascotaBE guardada = mascotasService.registrarODeduplicarInternal(req, null);
                    if (guardada.isNrmReutilizado()) {
                        mascotasDeduplicadas++;
                    } else {
                        mascotasNuevas++;
                    }
                    if (hasText(externa.getIdMascotaExterna())) {
                        sincronizacionRepository.upsertMapeo(
                                config.getIdVeterinaria(),
                                externa.getIdMascotaExterna().trim(),
                                guardada.getNroRegMunicipal(),
                                false
                        );
                    }
                }
                pagina++;
            } while (pagina <= totalPaginas);

            // 2. Informar NRMs asignados pendientes de confirmación
            int nrmsConfirmados = 0;
            List<NrmAsignadoBE> pendientes = sincronizacionRepository.getNrmsPendientes(config.getIdVeterinaria());
            if (!pendientes.isEmpty()) {
                List<NrmAcuseBE> acuses = client.informarNrm(config, pendientes);
                for (NrmAcuseBE acuse : acuses) {
                    if (acuse.isConfirmado() && hasText(acuse.getIdMascotaExterna())) {
                        sincronizacionRepository.marcarNrmConfirmado(
                                config.getIdVeterinaria(),
                                acuse.getIdMascotaExterna().trim()
                        );
                        nrmsConfirmados++;
                    }
                }
            }

            // 3. Ingestar prácticas sanitarias de forma idempotente (RF09)
            int atencionesProcesadas = 0;
            pagina = 1;
            do {
                PageResponseBE<PracticaSanitariaExternaBE> page = client.obtenerAtencionesSanitarias(
                        config,
                        buildFiltro(desde, hasta, pagina)
                );
                totalPaginas = page.getTotalPaginas();
                for (PracticaSanitariaExternaBE practica : page.getItems()) {
                    Integer nrm = resolverNrm(config.getIdVeterinaria(), practica);
                    Integer codTipo = resolverCodTipoPractica(practica.getTipoPractica(), tiposPorNombre);
                    if (nrm == null || codTipo == null) {
                        continue;
                    }
                    sanidadService.registrarPracticaExternaInternal(
                            nrm,
                            config.getIdVeterinaria(),
                            practica.getFechaAtencion(),
                            codTipo,
                            practica.getDetalle(),
                            practica.getFechaVencimiento(),
                            null,
                            practica.getProfesional(),
                            practica.getNroAtencion(),
                            practica.getNroDetalle()
                    );
                    atencionesProcesadas++;
                }
                pagina++;
            } while (pagina <= totalPaginas);

            resultado.setEstado("EXITO");
            resultado.setMascotasNuevas(mascotasNuevas);
            resultado.setMascotasDeduplicadas(mascotasDeduplicadas);
            resultado.setNrmsConfirmados(nrmsConfirmados);
            resultado.setAtencionesProcesadas(atencionesProcesadas);

            sincronizacionRepository.registrarLote(
                    config.getIdVeterinaria(),
                    "MASCOTAS_Y_SANIDAD",
                    desde,
                    hasta,
                    "EXITO",
                    mascotasNuevas + mascotasDeduplicadas + atencionesProcesadas,
                    mascotasNuevas,
                    mascotasDeduplicadas,
                    nrmsConfirmados,
                    null
            );
        } catch (Exception ex) {
            String motivo = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            String motivoCorto = motivo.length() > 480 ? motivo.substring(0, 480) : motivo;
            log.warn("Falla aislada al sincronizar veterinaria #{} ({}): {}",
                    config.getIdVeterinaria(), config.getNombreVeterinaria(), motivoCorto);

            resultado.setEstado("ERROR");
            resultado.setMensajeError(motivoCorto);

            try {
                sincronizacionRepository.registrarLote(
                        config.getIdVeterinaria(),
                        "MASCOTAS_Y_SANIDAD",
                        desde,
                        hasta,
                        "ERROR",
                        0,
                        0,
                        0,
                        0,
                        motivoCorto
                );
            } catch (Exception logEx) {
                log.error("No se pudo persistir bitácora de error para veterinaria #{}: {}",
                        config.getIdVeterinaria(), logEx.getMessage());
            }
        }
        return resultado;
    }

    private Integer resolverNrm(int idVeterinaria, PracticaSanitariaExternaBE practica) {
        if (practica.getNroRegMunicipal() != null && practica.getNroRegMunicipal() > 0) {
            return practica.getNroRegMunicipal();
        }
        if (hasText(practica.getIdMascotaExterna())) {
            return sincronizacionRepository
                    .getMapeoMascota(idVeterinaria, practica.getIdMascotaExterna().trim())
                    .orElse(null);
        }
        return null;
    }

    private Map<String, Integer> cargarMapaTiposAtencion() {
        Map<String, Integer> mapa = new HashMap<>();
        List<TipoAtencionBE> tipos = sanidadRepository.getTiposAtencion();
        if (tipos != null) {
            for (TipoAtencionBE t : tipos) {
                if (t.getCodTipoAtencion() != null && hasText(t.getDescTipoAtencion())) {
                    mapa.put(normalizeKey(t.getDescTipoAtencion()), t.getCodTipoAtencion());
                }
            }
        }
        return mapa;
    }

    private static Integer resolverCodTipoPractica(String tipoPractica, Map<String, Integer> tiposPorNombre) {
        if (!hasText(tipoPractica)) {
            return null;
        }
        return tiposPorNombre.get(normalizeKey(tipoPractica));
    }

    private static RegistrarMascotaRequestBE toRegistrarRequest(MascotaExternaBE externo) {
        RegistrarMascotaRequestBE req = new RegistrarMascotaRequestBE();
        req.setNombre(externo.getNombre());
        req.setSexo(externo.getSexo());
        Integer anio = externo.getAñoNacimiento();
        if (anio == null && externo.getEdadAproximada() != null && externo.getEdadAproximada() >= 0) {
            anio = LocalDate.now().getYear() - externo.getEdadAproximada();
        }
        req.setAñoNacimiento(anio != null ? anio : LocalDate.now().getYear());
        req.setEspecie(externo.getEspecie());
        req.setRaza(externo.getRaza());
        req.setCastrado(Boolean.TRUE.equals(externo.getCastrado()));
        req.setMicrochip(externo.getMicrochip());
        req.setCuilResponsable(externo.getCuilResponsable());
        return req;
    }

    private static LoteRangoFechasRequestBE buildFiltro(LocalDate desde, LocalDate hasta, int pagina) {
        LoteRangoFechasRequestBE filtro = new LoteRangoFechasRequestBE();
        filtro.setFechaDesde(desde);
        filtro.setFechaHasta(hasta);
        filtro.setPagina(pagina);
        filtro.setTamanioPagina(TAMANIO_PAGINA_LOTE);
        return filtro;
    }

    private static String normalizeKey(String raw) {
        String normalized = Normalizer.normalize(raw.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}+", "");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
