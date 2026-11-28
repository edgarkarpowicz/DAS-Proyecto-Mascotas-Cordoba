package ar.edu.ubp.das.veterinaria.service;

import ar.edu.ubp.das.veterinaria.model.ClinicaMascotaDTO;
import ar.edu.ubp.das.veterinaria.model.NrmAcuseDTO;
import ar.edu.ubp.das.veterinaria.model.NrmAsignadoDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaMascotasDTO;
import ar.edu.ubp.das.veterinaria.model.PaginaPracticasDTO;
import ar.edu.ubp.das.veterinaria.model.PracticaSanitariaDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Almacén en memoria del sistema interno de cada veterinaria adherida según CLINIC_PROFILE.
 */
@Service
public class ClinicaStoreService {

    @Value("${veterinaria.profile:san-antonio}")
    private String profile;

    @Value("${veterinaria.api-key:}")
    private String configuredApiKey;

    private String effectiveApiKey;
    private final Map<String, ClinicaMascotaDTO> mascotas = new LinkedHashMap<>();
    private final List<PracticaSanitariaDTO> practicas = new ArrayList<>();

    @PostConstruct
    public void init() {
        String normalizedProfile = profile != null ? profile.trim().toLowerCase() : "san-antonio";
        mascotas.clear();
        practicas.clear();

        if ("central-soap".equals(normalizedProfile)) {
            effectiveApiKey = hasText(configuredApiKey)
                    ? configuredApiKey.trim()
                    : "UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT";
            seedCentralSoap();
        } else {
            effectiveApiKey = hasText(configuredApiKey)
                    ? configuredApiKey.trim()
                    : "UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6";
            seedSanAntonio();
        }
    }

    public boolean isApiKeyValid(String providedApiKey) {
        return !hasText(effectiveApiKey) || (providedApiKey != null && effectiveApiKey.equals(providedApiKey.trim()));
    }

    public synchronized PaginaMascotasDTO obtenerMascotasSinNrm(
            String fechaDesde,
            String fechaHasta,
            Integer pagina,
            Integer tamanioPagina) {
        LocalDate desde = parseDate(fechaDesde);
        LocalDate hasta = parseDate(fechaHasta);

        List<ClinicaMascotaDTO> pendientes = mascotas.values().stream()
                .filter(m -> m.getNroRegMunicipal() == null)
                .filter(m -> inRange(m.getFechaRegistro(), desde, hasta))
                .toList();

        int page = pagina != null && pagina >= 1 ? pagina : 1;
        int size = tamanioPagina != null && tamanioPagina >= 1 ? Math.min(tamanioPagina, 100) : 20;
        int fromIndex = Math.min((page - 1) * size, pendientes.size());
        int toIndex = Math.min(fromIndex + size, pendientes.size());

        PaginaMascotasDTO response = new PaginaMascotasDTO();
        response.setItems(new ArrayList<>(pendientes.subList(fromIndex, toIndex)));
        response.setPagina(page);
        response.setTamanioPagina(size);
        response.setTotalElementos(pendientes.size());
        response.setTotalPaginas(pendientes.isEmpty() ? 0 : (int) Math.ceil((double) pendientes.size() / size));
        return response;
    }

    public synchronized List<NrmAcuseDTO> informarNrm(List<NrmAsignadoDTO> asignaciones) {
        if (asignaciones == null || asignaciones.isEmpty()) {
            return List.of();
        }
        List<NrmAcuseDTO> acuses = new ArrayList<>();
        for (NrmAsignadoDTO item : asignaciones) {
            if (item == null || !hasText(item.getIdMascotaExterna()) || item.getNroRegMunicipal() == null) {
                continue;
            }
            ClinicaMascotaDTO mascota = mascotas.get(item.getIdMascotaExterna().trim());
            if (mascota == null) {
                acuses.add(new NrmAcuseDTO(
                        item.getIdMascotaExterna(),
                        item.getNroRegMunicipal(),
                        false,
                        "Mascota externa no encontrada en la clínica"
                ));
            } else {
                mascota.setNroRegMunicipal(item.getNroRegMunicipal());
                acuses.add(new NrmAcuseDTO(
                        mascota.getIdMascotaExterna(),
                        mascota.getNroRegMunicipal(),
                        true,
                        "NRM confirmado y almacenado"
                ));
            }
        }
        return acuses;
    }

    public synchronized PaginaPracticasDTO obtenerAtencionesSanitarias(
            String fechaDesde,
            String fechaHasta,
            Integer pagina,
            Integer tamanioPagina) {
        LocalDate desde = parseDate(fechaDesde);
        LocalDate hasta = parseDate(fechaHasta);

        List<PracticaSanitariaDTO> filtradas = practicas.stream()
                .filter(p -> inRange(p.getFechaAtencion(), desde, hasta))
                .map(p -> {
                    ClinicaMascotaDTO mascota = mascotas.get(p.getIdMascotaExterna());
                    Integer nrmActual = mascota != null ? mascota.getNroRegMunicipal() : p.getNroRegMunicipal();
                    return p.copyWithNrm(nrmActual);
                })
                .toList();

        int page = pagina != null && pagina >= 1 ? pagina : 1;
        int size = tamanioPagina != null && tamanioPagina >= 1 ? Math.min(tamanioPagina, 100) : 20;
        int fromIndex = Math.min((page - 1) * size, filtradas.size());
        int toIndex = Math.min(fromIndex + size, filtradas.size());

        PaginaPracticasDTO response = new PaginaPracticasDTO();
        response.setItems(new ArrayList<>(filtradas.subList(fromIndex, toIndex)));
        response.setPagina(page);
        response.setTamanioPagina(size);
        response.setTotalElementos(filtradas.size());
        response.setTotalPaginas(filtradas.isEmpty() ? 0 : (int) Math.ceil((double) filtradas.size() / size));
        return response;
    }

    private void seedSanAntonio() {
        putMascota(new ClinicaMascotaDTO(
                "SA-M-001", "2026-10-10", "Firulais", "M", 2021,
                "Perro", "Border Collie", false, "981098104523123", "20-28555666-3"
        ));
        putMascota(new ClinicaMascotaDTO(
                "SA-M-002", "2026-10-15", "Nala", "H", 2024,
                "Perro", "Labrador", false, "981098107778889", "20-28555666-3"
        ));
        putMascota(new ClinicaMascotaDTO(
                "SA-M-003", "2026-11-02", "Simba", "M", 2025,
                "Gato", "Siamés", true, null, "27-34777888-4"
        ));

        practicas.add(new PracticaSanitariaDTO(
                "SA-M-001", 1, 1, "2026-10-12", "Vacunación",
                "Refuerzo séxtuple canina - Lote SA-2026-1", "2027-10-12", "Dr. Esteban Quito"
        ));
        practicas.add(new PracticaSanitariaDTO(
                "SA-M-002", 2, 1, "2026-10-15", "Desparasitación",
                "Antiparasitario interno comprimidos - Lote DP-88", "2027-04-15", "Dra. Laura Martínez"
        ));
        practicas.add(new PracticaSanitariaDTO(
                "SA-M-003", 3, 1, "2026-11-02", "Castración",
                "Castración quirúrgica electiva", null, "Dr. Esteban Quito"
        ));
    }

    private void seedCentralSoap() {
        putMascota(new ClinicaMascotaDTO(
                "VC-M-001", "2026-10-18", "Milo", "M", 2022,
                "Perro", "Mestizo Canino", true, "981098107845612", "27-34777888-4"
        ));
        putMascota(new ClinicaMascotaDTO(
                "VC-M-002", "2026-11-05", "Kira", "H", 2024,
                "Perro", "Labrador", false, "981098109900112", "27-34777888-4"
        ));

        practicas.add(new PracticaSanitariaDTO(
                "VC-M-001", 101, 1, "2026-10-18", "Vacunación",
                "Vacuna antirrábica anual - Lote VC-771", "2027-10-18", "Dr. Martín Ferrero"
        ));
        practicas.add(new PracticaSanitariaDTO(
                "VC-M-002", 102, 1, "2026-11-05", "Consulta clínica",
                "Control clínico general de ingreso", null, "Dr. Martín Ferrero"
        ));
    }

    private void putMascota(ClinicaMascotaDTO dto) {
        mascotas.put(dto.getIdMascotaExterna(), dto);
    }

    private static LocalDate parseDate(String iso) {
        return hasText(iso) ? LocalDate.parse(iso.trim()) : null;
    }

    private static boolean inRange(String dateIso, LocalDate desde, LocalDate hasta) {
        if (!hasText(dateIso)) {
            return true;
        }
        LocalDate value = LocalDate.parse(dateIso.trim());
        if (desde != null && value.isBefore(desde)) {
            return false;
        }
        return hasta == null || !value.isAfter(hasta);
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public String getEffectiveApiKey() {
        return Objects.toString(effectiveApiKey, "");
    }
}
