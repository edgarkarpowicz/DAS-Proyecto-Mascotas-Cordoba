# Mascotas Córdoba - FrontEnd Web Application

Portal Web cliente de la plataforma **Mascotas Córdoba** (Programa Municipal de Bienestar Animal).  
Desarrollado en **Angular 21** con **Angular Material**, Standalone Components, Signals, Reactive Forms, Resolvers, Guards e Interceptors.

---

## 📋 Requisitos Previos

- **Node.js:** Versión `20.19+` o `22+`.
- **npm:** Versión `10.x` o superior.
- **Backend:** Servicio Spring Boot corriendo en `http://localhost:8080`.

---

## ⚙️ Configuración del Entorno

Los parámetros de conexión se encuentran en:
- `src/environments/environment.ts` (Producción)
- `src/environments/environment.development.ts` (Desarrollo)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  apiKey: 'UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6'
};
```

---

## 🚀 Cómo Ejecutar el FrontEnd

1. **Abrir una terminal en este directorio:**
   ```powershell
   cd frontend/Mascotas-Cordoba-FrontEnd
   ```

2. **Instalar dependencias (solo la primera vez o tras actualizar `package.json`):**
   ```powershell
   npm install
   ```

3. **Iniciar el servidor de desarrollo:**
   ```powershell
   npm start
   # o:
   npx ng serve
   ```

4. **Acceder a la aplicación:**
   Abrir en el navegador: **`http://localhost:4200/`**

---

## 🧪 Tests y Compilación para Producción

Para ejecutar la suite de pruebas unitarias:
```powershell
npx ng test --watch=false --browsers=ChromeHeadless
```

Para compilar el proyecto y verificar que no existan errores de tipos o empaquetado:
```powershell
npm run build
```
Los archivos compilados se generarán en la carpeta `dist/mascotas-cordoba-front-end`.

---

## 🔑 Credenciales para Iniciar Sesión (Pruebas)

Utiliza los usuarios precargados en la base de datos municipal:

| Usuario | CUIL | Contraseña | Perfil / Organización |
| :--- | :--- | :--- | :--- |
| **Roberto Sánchez** | `20-25999000-5` | `pass123` | `REFUGIO` — **Refugio Patitas Felices** (Carga 2 publicaciones iniciales) |
| **Ana Carolina Morales** | `27-38123456-9` | `pass123` | `REFUGIO` — **Hogar Animal Córdoba** (Sin publicaciones iniciales; incluye mascota precargada NRM 100006 "Thor") |
| **Juan Carlos Pérez** | `20-28555666-3` | `pass123` | `CIUDADANO` (Inicia sesión, personaliza el portal RF16 y queda bloqueado por `roleGuard` en `/publicaciones`) |

---

## 🏛️ Arquitectura del FrontEnd

La aplicación se organiza por módulos funcionales cargados con `loadChildren` desde `src/app/app.routes.ts` (ver detalle completo por RF en [`docs/ESTRUCTURA_PROYECTO.md`](../../docs/ESTRUCTURA_PROYECTO.md)):

- **Portal Público (`src/app/portal/`, ruta `/` - RF16):**
  - `PORTAL_ROUTES` (`portal.routes.ts`), `PortalResource` (`GET /publico/portal`), `portalResolver` y `PortalComponent` (indicadores en vivo, directorio público y servicios personalizados por perfil).
- **Módulo de Autenticación (`src/app/auth/`, ruta `/login` - RF15):**
  - `AUTH_ROUTES` (`auth.routes.ts`) y `LoginComponent` con soporte multiperfil y redirección declarativa (`HOME_BY_PERFIL`).
- **Módulo de Publicaciones (`src/app/publicaciones/`, ruta `/publicaciones` - RF13):**
  - `PUBLICACIONES_ROUTES` (`publicaciones.routes.ts`) protegido con `roleGuard('REFUGIO')`, proveedores locales (`PublicacionesResource`, `EstadoPublicacionService`) y resolvers de navegación (`publicacionesResolver`, `estadosResolver`).
  - `PublicacionesComponent` y modales por referencia con `MatDialog` (`NuevaPublicacionModalComponent`, `DetallePublicacionModalComponent`).
- **Módulos Estructurados para Próximos RF Obligatorios:**
  - `src/app/mascotas/` (`/mascotas/mias` RF20, `/mascotas/registro` RF06)
  - `src/app/sanidad/` (`/sanidad/atenciones` RF09, `/sanidad/carnet` RF10/11, `/sanidad/validar-carnet` RF11 público)
  - `src/app/veterinarias/` (`/veterinarias` RF17)
  - `src/app/refugios/` (`/refugios` RF18)
  - `src/app/asistente/` (`/asistente` RF21, con `SKIP_GLOBAL_LOADER`)
- **Núcleo (`src/app/core/`):**
  - `AuthService` y `AuthSessionService` (sesión JWT persistida únicamente en `sessionStorage`, `IUsuario` unificado y restaurado con `provideAppInitializer`).
  - `appHttpInterceptor`: inyecta `X-API-Key` y `Authorization: Bearer <token>`, soporta `SKIP_GLOBAL_LOADER`, cancela peticiones al navegar (`HttpCancelService`) y evita redirecciones a `/login` en `/publico/**`.
  - `AppErrorHandler`, `AppMessageService`, `ConfirmDialogService`, `MAT_DIALOG_DEFAULT_OPTIONS` en `app.config.ts`, `authGuard`, `roleGuard` y `controlarCambiosGuard`.
- **Compartido (`src/app/shared/`):**
  - `NavbarComponent`, `RfPlaceholderPageComponent`, `ImagenLightboxModalComponent`, `ExpandImageButtonComponent`, `FiltrarPorPipe`, `AutofocusDirective`, `pagination.ts` y `mascota-formatters.ts`.
