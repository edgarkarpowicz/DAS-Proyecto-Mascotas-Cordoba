# Estructura del Proyecto por Requerimientos Funcionales Obligatorios

Mapa de referencia para localizar e implementar cada módulo vertical (**Backend Spring Boot + Frontend Angular**) de los **11 RF obligatorios** de Mascotas Córdoba.

## Mapa Maestro de RF Obligatorios

| RF | Descripción | Estado | Backend (`ar.edu.ubp.das.mascotas`) | Endpoint REST | Integración externa (`integrations/`) | Frontend (`src/app/`) | Ruta Web |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **RF06** | Registrar Mascota | **Implementado** | `BE/mascotas/`, `BE/integraciones/`, `SincronizacionVeterinariasService`, `MascotasService` | `POST /mascotas` y `POST /integraciones/veterinarias/sincronizar` | `integrations/veterinarias/` (`RestVeterinariaIntegrationClient`, `SoapVeterinariaIntegrationClient`) + `veterinarias-externas/` | `mascotas/`, `integraciones/` | `/mascotas/registro`, `/municipalidad/integraciones` |
| **RF09** | Registrar Información Sanitaria | **Implementado** | `BE/sanidad/`, `BE/integraciones/`, `SincronizacionVeterinariasService`, `SanidadService` | `POST /sanidad/atenciones`, `GET /sanidad/mascotas/{nrm}/atenciones`, `/integraciones/veterinarias/**` | `integrations/veterinarias/` (`RestVeterinariaIntegrationClient`, `SoapVeterinariaIntegrationClient`) + `veterinarias-externas/` | `sanidad/`, `integraciones/` | `/sanidad/atenciones`, `/municipalidad/integraciones` |
| **RF10** | Generar Carnet Sanitario Digital | **Implementado** | `BE/sanidad/`, `repositories/sanidad/`, `services/sanidad/`, `resources/sanidad/` | `POST /sanidad/carnets/{nrm}` | — | `sanidad/` | `/sanidad/carnet` |
| **RF11** | Consultar y Validar Carnet Digital | **Implementado** | `BE/sanidad/`, `services/sanidad/`, `SanidadResource`, `CarnetsPublicoResource` | `GET /sanidad/carnets/{nrm}` y `GET /publico/carnets/{codigo}/validar` | — | `sanidad/` | `/sanidad/carnet`, `/sanidad/validar-carnet` |
| **RF13** | Gestionar Publicaciones de Adopción | **Implementado** | `BE/adopciones/`, `repositories/adopciones/`, `services/adopciones/`, `resources/adopciones/` | `/refugios/**` y SOAP `/services/publicaciones` | — | `publicaciones/` | `/publicaciones` |
| **RF15** | Autenticar Usuarios | **Implementado (local)** | `BE/auth/`, `repositories/auth/`, `services/auth/`, `resources/auth/`, `security/` | `POST /auth/login`, `GET /auth/me`, `POST /auth/logout` | `integrations/cidi/` (`IdentidadCiudadanaClient`) | `auth/`, `core/` | `/login` |
| **RF16** | Acceder al Portal del Programa | **Implementado** | `BE/portal/`, `repositories/portal/`, `services/portal/`, `resources/portal/` | `GET /publico/portal` | — | `portal/` | `/` |
| **RF17** | Consultar Veterinarias Adheridas | **Implementado** | `BE/veterinarias/`, `repositories/veterinarias/`, `services/veterinarias/`, `resources/veterinarias/` | `GET /publico/veterinarias` | — | `veterinarias/` | `/veterinarias` |
| **RF18** | Consultar Refugios Adheridos | **Implementado** | `BE/refugios/`, `repositories/refugios/`, `services/refugios/`, `resources/refugios/` | `GET /publico/refugios` y `GET /publico/refugios/{id}/publicaciones` | — | `refugios/` | `/refugios` |
| **RF20** | Consultar Mis Mascotas | **Implementado** | `BE/mascotas/`, `repositories/mascotas/`, `services/mascotas/`, `resources/mascotas/` | `GET /mascotas/mias` | — | `mascotas/` | `/mascotas/mias` |
| **RF21** | Consultar Asistente Virtual | Estructura lista (`501`) | `BE/asistente/`, `services/asistente/`, `resources/asistente/` | `POST /publico/asistente/consultar` | `integrations/ia/` (`AsistenteVirtualClient`) | `asistente/` | `/asistente` |

---

## Piezas Transversales Reutilizables

### Base de datos
- [`db/municipalidad-creation-script.sql`](../db/municipalidad-creation-script.sql): script único de creación de esquema, stored procedures y datos iniciales (se recrea completo ante cambios de esquema).

### Backend (`backend/mascotas-cordoba-project`)
- **Seguridad unificada:** [`UsuarioBE`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/BE/auth/UsuarioBE.java), [`PerfilUsuario`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/BE/auth/PerfilUsuario.java) y [`AuthorizationService`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/security/AuthorizationService.java).
- **Prefijo público estandarizado:** toda ruta bajo `/publico/**` permite acceso anónimo en [`AuthTokenFilter`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/config/AuthTokenFilter.java) y carga `UserContextHolder` de forma opcional si llega un JWT válido.
- **Repositorios JDBC:** extender [`BaseStoredProcRepository`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/repositories/common/BaseStoredProcRepository.java) para reutilizar mapeo, cache de `SimpleJdbcCall` y manejo de excepciones SQL.
- **Paginación:** [`PageRequestBE`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/BE/common/PageRequestBE.java) y [`PageResponseBE`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/BE/common/PageResponseBE.java).
- **Integraciones SOA:** interfaces y DTOs externos aislados en `integrations/` (`veterinarias/`, `cidi/`, `ia/`).

### Frontend (`frontend/Mascotas-Cordoba-FrontEnd`)
- **Enrutamiento y permisos:** `app.routes.ts` carga cada módulo con `loadChildren`; las rutas protegidas usan [`roleGuard`](../frontend/Mascotas-Cordoba-FrontEnd/src/app/core/guards/role-guard.ts) o [`authGuard`](../frontend/Mascotas-Cordoba-FrontEnd/src/app/core/guards/auth-guard.ts).
- **HTTP y diálogos:** [`appHttpInterceptor`](../frontend/Mascotas-Cordoba-FrontEnd/src/app/core/interceptors/app-http-interceptor.ts) soporta [`SKIP_GLOBAL_LOADER`](../frontend/Mascotas-Cordoba-FrontEnd/src/app/core/http/http-context-tokens.ts) y excluye `/publico/**` de redirecciones `401` a `/login`; `MAT_DIALOG_DEFAULT_OPTIONS` centraliza los presets de `MatDialog` en `app.config.ts`.
- **Paginación y placeholders:** [`pagination.ts`](../frontend/Mascotas-Cordoba-FrontEnd/src/app/shared/models/pagination.ts) y [`RfPlaceholderPageComponent`](../frontend/Mascotas-Cordoba-FrontEnd/src/app/shared/components/rf-placeholder-page/rf-placeholder-page.component.ts).

---

## Cómo Pasar un RF de Placeholder a Implementado

1. **Base de datos:** agregar o ajustar los stored procedures necesarios en `db/municipalidad-creation-script.sql`.
2. **Backend:** implementar las consultas en el `*Repository` existente (extendiendo `BaseStoredProcRepository`), completar la lógica y validaciones en el `*Service` reemplazando el `501 NOT_IMPLEMENTED`, y añadir adaptadores en `integrations/` si el RF consume un sistema externo.
3. **Frontend:** reemplazar el template `<app-rf-placeholder-page>` del componente de página por la vista real consumiendo su `*Resource`, y habilitar el enlace en [`PortalService.java`](../backend/mascotas-cordoba-project/src/main/java/ar/edu/ubp/das/mascotas/services/portal/PortalService.java) (`disponible = true`) y en [`NavbarComponent`](../frontend/Mascotas-Cordoba-FrontEnd/src/app/shared/components/navbar/navbar.component.ts) si corresponde.
4. **Pruebas:** agregar tests unitarios en backend y frontend y verificar `mvnw test`, `ng test --watch=false --browsers=ChromeHeadless` y `npm run build`.
