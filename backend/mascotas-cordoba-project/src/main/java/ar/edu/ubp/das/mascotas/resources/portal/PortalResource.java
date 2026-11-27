package ar.edu.ubp.das.mascotas.resources.portal;

import ar.edu.ubp.das.mascotas.BE.portal.PortalInfoBE;
import ar.edu.ubp.das.mascotas.services.portal.IPortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recurso REST público del portal municipal (RF16).
 * Tolera acceso anónimo o sesión Bearer opcional en /publico/**.
 */
@RestController
@RequestMapping("/publico/portal")
public class PortalResource {

    @Autowired
    private IPortalService portalService;

    @GetMapping
    public ResponseEntity<PortalInfoBE> getPortal() {
        return ResponseEntity.ok(portalService.consultarServiciosHabilitados());
    }
}
