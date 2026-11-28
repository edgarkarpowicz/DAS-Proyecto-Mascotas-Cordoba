package ar.edu.ubp.das.mascotas.services.portal;

import ar.edu.ubp.das.mascotas.BE.auth.PerfilUsuario;
import ar.edu.ubp.das.mascotas.BE.auth.SesionUsuarioBE;
import ar.edu.ubp.das.mascotas.BE.portal.PortalInfoBE;
import ar.edu.ubp.das.mascotas.BE.portal.ResumenProgramaBE;
import ar.edu.ubp.das.mascotas.BE.portal.ServicioHabilitadoBE;
import ar.edu.ubp.das.mascotas.repositories.portal.PortalRepository;
import ar.edu.ubp.das.mascotas.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * RF16 - Acceder al Portal del Programa Municipal de Bienestar Animal.
 * Devuelve información pública sin exigir sesión y agrega las funcionalidades
 * habilitadas del perfil cuando existe un token JWT válido en el contexto.
 */
@Service
public class PortalService implements IPortalService {

    private static final String NOMBRE_PROGRAMA = "Programa Municipal de Bienestar Animal";
    private static final String DESCRIPCION_PROGRAMA =
            "Plataforma pública de Mascotas Córdoba para consultar redes adheridas, registro sanitario " +
            "y servicios personalizados según cada perfil.";

    private static final List<ServicioHabilitadoBE> SERVICIOS_PUBLICOS = List.of(
            new ServicioHabilitadoBE(
                    "VETERINARIAS",
                    "Veterinarias adheridas",
                    "Consultá clínicas veterinarias habilitadas, horarios de atención y servicios.",
                    "/veterinarias",
                    true,
                    false,
                    "RF17"
            ),
            new ServicioHabilitadoBE(
                    "REFUGIOS",
                    "Refugios y adopciones",
                    "Explorá refugios adheridos y las mascotas con publicación activa.",
                    "/refugios",
                    true,
                    false,
                    "RF18"
            ),
            new ServicioHabilitadoBE(
                    "CAMPANAS",
                    "Campañas municipales",
                    "Agenda vigente de castración, vacunación antirrábica y jornadas territoriales.",
                    "/campanas",
                    false,
                    false,
                    "RF19"
            ),
            new ServicioHabilitadoBE(
                    "ASISTENTE",
                    "Asistente virtual",
                    "Orientación pública sobre trámites, cuidado responsable y red municipal.",
                    "/asistente",
                    false,
                    false,
                    "RF21"
            )
    );

    @Autowired
    private PortalRepository portalRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public PortalInfoBE consultarServiciosHabilitados() {
        ResumenProgramaBE resumen = portalRepository.getResumenPortal();

        PortalInfoBE info = new PortalInfoBE();
        info.setNombrePrograma(NOMBRE_PROGRAMA);
        info.setDescripcionPrograma(DESCRIPCION_PROGRAMA);
        info.setResumen(resumen != null ? resumen : new ResumenProgramaBE(0, 0, 0));
        info.setServiciosPublicos(SERVICIOS_PUBLICOS);

        Optional<SesionUsuarioBE> sesionOpt = authorizationService.getOptionalAuthenticated();
        if (sesionOpt.isEmpty()) {
            info.setAutenticado(false);
            info.setPerfil(null);
            info.setUsuario(null);
            info.setServiciosHabilitados(List.of());
            return info;
        }

        SesionUsuarioBE sesion = sesionOpt.get();
        info.setAutenticado(true);
        info.setPerfil(sesion.getPerfil());
        info.setUsuario(sesion.toUsuarioBE());
        info.setServiciosHabilitados(serviciosPorPerfil(sesion.resolvePerfil().orElse(null)));
        return info;
    }

    private static List<ServicioHabilitadoBE> serviciosPorPerfil(PerfilUsuario perfil) {
        if (perfil == null) {
            return List.of();
        }
        return switch (perfil) {
            case REFUGIO -> List.of(
                    new ServicioHabilitadoBE(
                            "PUBLICACIONES_ADOPCION",
                            "Gestionar publicaciones de adopción",
                            "Creá, pausá o finalizá publicaciones y registrá mascotas bajo cuidado.",
                            "/publicaciones",
                            true,
                            true,
                            "RF13"
                    ),
                    new ServicioHabilitadoBE(
                            "ANIMALES_CUIDADO",
                            "Animales bajo cuidado",
                            "Seguimiento de ingresos y animales alojados en el refugio.",
                            "/refugio/animales",
                            false,
                            true,
                            "RF12"
                    )
            );
            case CIUDADANO -> List.of(
                    new ServicioHabilitadoBE(
                            "MIS_MASCOTAS",
                            "Mis mascotas",
                            "Visualizá tus mascotas registradas y su estado general en el programa.",
                            "/mascotas/mias",
                            true,
                            true,
                            "RF20"
                    ),
                    new ServicioHabilitadoBE(
                            "CARNET_SANITARIO",
                            "Carnet sanitario digital",
                            "Generá y compartí el carnet oficial vigente con código de validación.",
                            "/sanidad/carnet",
                            false,
                            true,
                            "RF10 / RF11"
                    )
            );
            case VETERINARIA -> List.of(
                    new ServicioHabilitadoBE(
                            "REGISTRO_MASCOTAS",
                            "Registro único de mascotas",
                            "Consultá y enviá altas de mascotas sin NRM desde tu clínica.",
                            "/mascotas/registro",
                            true,
                            true,
                            "RF06"
                    ),
                    new ServicioHabilitadoBE(
                            "INFO_SANITARIA",
                            "Información sanitaria",
                            "Consolidá vacunaciones, castraciones y prácticas del programa.",
                            "/sanidad/atenciones",
                            false,
                            true,
                            "RF09"
                    )
            );
            case MUNICIPALIDAD -> List.of(
                    new ServicioHabilitadoBE(
                            "REGISTRO_MASCOTAS",
                            "Registro único de mascotas",
                            "Registrá o deduplicá mascotas en el padrón municipal único (NRM).",
                            "/mascotas/registro",
                            true,
                            true,
                            "RF06"
                    ),
                    new ServicioHabilitadoBE(
                            "APROBAR_ORGANIZACIONES",
                            "Aprobar organizaciones adheridas",
                            "Evaluá solicitudes de adhesión de veterinarias y refugios.",
                            "/municipalidad/organizaciones",
                            false,
                            true,
                            "RF03"
                    ),
                    new ServicioHabilitadoBE(
                            "CONFIGURAR_INTEGRACIONES",
                            "Configurar integraciones veterinarias",
                            "Administrá URLs, protocolo REST/SOAP y credenciales de clínicas.",
                            "/municipalidad/integraciones",
                            false,
                            true,
                            "RF04"
                    ),
                    new ServicioHabilitadoBE(
                            "ADMINISTRAR_CAMPANAS",
                            "Administrar campañas municipales",
                            "Publicá y actualizá jornadas sanitarias del programa.",
                            "/municipalidad/campanas",
                            false,
                            true,
                            "RF05"
                    )
            );
        };
    }
}
