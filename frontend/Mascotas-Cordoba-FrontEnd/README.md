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

| Usuario | CUIL | Contraseña | Refugio Asociado |
| :--- | :--- | :--- | :--- |
| **Roberto Sánchez** | `20-25999000-5` | `pass123` | **Refugio Patitas Felices** (Carga 2 publicaciones iniciales) |
| **Ana Carolina Morales** | `27-38123456-9` | `pass123` | **Hogar Animal Córdoba** (Sin publicaciones iniciales; incluye mascota precargada NRM 100006 "Thor") |
| **Juan Carlos Pérez** | `20-28555666-3` | `pass123` | *Sin refugio* (Prueba de validación de perfil / acceso denegado) |

---

## 🏛️ Arquitectura del FrontEnd

La aplicación se organiza por módulos funcionales cargados con `loadChildren` desde `src/app/app.routes.ts`:

- **Módulo de Autenticación (`src/app/auth/`):**
  - `AUTH_ROUTES` (`auth.routes.ts`) y `LoginComponent` con Reactive Forms y validación en línea al enviar.
- **Módulo de Publicaciones (`src/app/publicaciones/`):**
  - `PUBLICACIONES_ROUTES` (`publicaciones.routes.ts`) con proveedores locales (`PublicacionesResource`, `EstadoPublicacionService`) y resolvers de navegación (`publicacionesResolver`, `estadosResolver`).
  - `PublicacionesComponent`: grilla filtrable por pestañas de estado y nombre, con `EstadoPublicacionService` (impide reactivar publicaciones `Finalizada`).
  - Modales por referencia con `MatDialog`:
    - `NuevaPublicacionModalComponent`: carga sus propios catálogos (`getMascotasDisponibles`, `getEspecies`), usa Reactive Forms, `textarea` para condiciones de adopción, calendario de años de Angular Material (`añoNacimiento`), subida de foto con caché ante reintentos y grabación directa al publicar.
    - `DetallePublicacionModalComponent`: cambia el estado desde el propio diálogo y devuelve la publicación actualizada al cerrar.
- **Núcleo (`src/app/core/`):**
  - `AuthService` y `AuthSessionService` (sesión JWT persistida únicamente en `sessionStorage`, restaurada al inicio con `provideAppInitializer`).
  - `appHttpInterceptor`: inyecta `X-API-Key` y `Authorization: Bearer <token>`, controla `LoaderService`, cancela peticiones al navegar (`HttpCancelService`) y limpia la sesión ante `401`.
  - `AppErrorHandler`, `AppMessageService` (`MatSnackBar` para éxitos y `MessageDialogComponent` para errores), `ConfirmDialogService` (`ConfirmDialogComponent`), `authGuard` y `controlarCambiosGuard`.
- **Compartido (`src/app/shared/`):**
  - `NavbarComponent`, `ImagenLightboxModalComponent`, `ExpandImageButtonComponent`, `FiltrarPorPipe`, `AutofocusDirective` y utilidades en `mascota-formatters.ts`.
