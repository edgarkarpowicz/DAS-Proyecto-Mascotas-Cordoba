# Mascotas Córdoba - FrontEnd Web Application

Portal Web cliente de la plataforma **Mascotas Córdoba** (Programa Municipal de Bienestar Animal).  
Desarrollado en **Angular 17** (Standalone Components, Signals, Resolvers, Guards e Interceptors).

---

## 📋 Requisitos Previos

- **Node.js:** Versión 18.x o superior.
- **npm:** Versión 9.x o superior.
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

2. **Instalar dependencias (solo la primera vez):**
   ```powershell
   npm install
   ```

3. **Iniciar el servidor de desarrollo:**
   ```powershell
   npm start
   # o:
   ng serve
   ```

4. **Acceder a la aplicación:**
   Abrir en el navegador: **`http://localhost:4200/`**

---

## 🔨 Compilación para Producción

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
| **Ana Carolina Morales** | `27-38123456-9` | `pass123` | **Hogar Animal Córdoba** (Sin publicaciones iniciales) |
| **Juan Carlos Pérez** | `20-28555666-3` | `pass123` | *Sin refugio* (Prueba de acceso denegado - 401) |

---

## 🏛️ Arquitectura del FrontEnd

Siguiendo las pautas de diseño y buenas prácticas del curso:

- **API Resources (`src/app/api/resources/`):**
  - `AuthResource`: Invocación a `POST /auth/login`.
  - `PublicacionesResource`: Consultas de estados, publicaciones del refugio, mascotas disponibles y transiciones de estado.
- **Interceptors (`src/app/interceptors/`):**
  - `appHttpInterceptor`: Inyecta la cabecera `X-API-Key`, activa el spinner global de carga reactivo y cancela peticiones pendientes ante cambios de ruta.
- **Error Handler (`src/app/handlers/`):**
  - `AppErrorHandler`: Captura global de errores HTTP y de tiempo de ejecución, desplegando un modal accesible (`ModalMessageComponent`).
- **Guards (`src/app/guards/`):**
  - `authGuard`: Protege el acceso a rutas privadas (`/publicaciones`) requiriendo sesión iniciada.
  - `controlarCambiosGuard`: Previene la pérdida de cambios sin guardar en formularios y modales (`CanDeactivateFn`).
- **Resolvers (`src/app/resolvers/`):**
  - `publicacionesResolver`: Pre-carga las publicaciones del refugio autenticado antes de activar la ruta.
  - `estadosResolver`: Pre-carga el catálogo dinámico de estados de publicación.
- **Manejo de Estado Reactivo:**
  - `AuthService`, `LoaderService`, `AppMessageService`, `ConfirmDialogService` utilizando Angular Signals.
