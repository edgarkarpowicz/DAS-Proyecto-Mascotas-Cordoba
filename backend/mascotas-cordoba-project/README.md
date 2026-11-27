# Mascotas Córdoba - Backend Service

Servicio Backend para la plataforma **Mascotas Córdoba** (Programa Municipal de Bienestar Animal).  
Desarrollado con **Java 17**, **Spring Boot**, **Apache CXF** (SOAP), **AspectJ** (POA) y **Microsoft SQL Server**.

---

## 📋 Requisitos Previos

- **Java Development Kit (JDK):** Versión 17 o superior.
- **Microsoft SQL Server:** Instancia local o remota en el puerto `1433`.
- **Base de Datos:** Ejecutar el script DDL y de datos semilla ubicado en:
  ```text
  ../../db/municipalidad-creation-script.sql
  ```
  > Si la base de datos ya existía de una versión anterior, aplicar en orden los scripts idempotentes de `../../db/migrations/` (`001_rf15_perfiles_auditoria.sql` y `002_publicaciones_logica_en_servicio.sql`).

---

## ⚙️ Configuración (`application.properties`)

El archivo se encuentra en `src/main/resources/application.properties`. Soporta variables de entorno con valores por defecto:

```properties
# Puerto del servidor
server.port=8080

# Conexión a Base de Datos SQL Server
spring.datasource.url=jdbc:sqlserver://${DB_HOST:localhost}:${DB_PORT:1433};databaseName=${DB_NAME:das-mascotas-cordoba};encrypt=false;trustServerCertificate=true
spring.datasource.username=${DB_USER:master}
spring.datasource.password=${DB_PASSWORD:123}
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Claves de Seguridad (API Keys y JWT)
security.soap.api-key=${SOAP_API_KEY:UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT}
security.rest.api-key=${REST_API_KEY:UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6}
security.jwt.secret=${JWT_SECRET:MascotasCordoba-DAS-UBP-2026-HMAC-SHA256-Secret-Key}
security.jwt.expiration-minutes=${JWT_EXPIRATION_MINUTES:480}

# Ruta base de Apache CXF para servicios SOAP
cxf.path=/services
```

> **Nota:** Puedes sobreescribir las credenciales sin modificar el archivo definiendo variables de entorno, por ejemplo en PowerShell:
> ```powershell
> $env:DB_USER="sa"
> $env:DB_PASSWORD="mi_password"
> $env:DB_NAME="das-mascotas-cordoba"
> ```

---

## 🚀 Cómo Ejecutar el Backend

1. **Abrir una terminal en este directorio:**
   ```powershell
   cd backend/mascotas-cordoba-project
   ```

2. **Compilar y ejecutar los tests:**
   ```powershell
   # En Windows PowerShell:
   .\mvnw.cmd test
   # o:
   ./mvnw test
   ```

3. **Iniciar la aplicación:**
   ```powershell
   # En Windows PowerShell:
   .\mvnw.cmd spring-boot:run
   # o:
   ./mvnw spring-boot:run
   ```

4. **Verificar que el servicio está activo:**
   - La aplicación responderá en: `http://localhost:8080`
   - Los contratos WSDL SOAP estarán disponibles en:
     - `http://localhost:8080/services/auth?wsdl`
     - `http://localhost:8080/services/publicaciones?wsdl`

---

## 🔌 Servicios Expuestos y Arquitectura

Los recursos REST (`AuthResource`, `PublicacionesResource`) delegan en los mismos servicios de negocio que expone Apache CXF (`AuthService`, `PublicacionesService`), donde viven las validaciones, las reglas de negocio (transiciones de estado, exclusividad de publicación activa, pertenencia al refugio, `añoNacimiento`), las transacciones `@Transactional` y la trazabilidad por AOP (`AuditAspect` + `ExceptionHandlingAspect`).

### RESTful API (JSON)
- Header requerido en todos los endpoints: `X-API-Key: UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
- Header requerido en endpoints protegidos (todos excepto `/auth/login`): `Authorization: Bearer <token>`
- `POST /auth/login`: Autenticación de usuarios y emisión de JWT (RF15).
- `GET /auth/me`: Identidad y perfil del usuario autenticado (RF15).
- `POST /auth/logout`: Revocación del JWT actual (RF15).
- `GET /refugios/publicaciones/estados`: Catálogo de estados válidos para publicaciones (RF13).
- `GET /refugios/especies` (alias `/refugios/publicaciones/especies`): Catálogo de especies (RF13 / RF06).
- `GET /refugios/{idRefugio}/publicaciones`: Listado de publicaciones administradas por el refugio (RF13).
- `GET /refugios/{idRefugio}/mascotas-disponibles`: Mascotas del refugio listas para publicar (RF13).
- `POST /refugios/publicaciones`: Crear nueva publicación de adopción y, si corresponde, registrar la mascota en el mismo bloque transaccional (RF13 / RF06).
- `PUT /refugios/publicaciones/{nroPublicacion}/estado`: Cambiar estado (`Activa` ⇄ `Pausada`, `Activa`/`Pausada` → `Finalizada`) (RF13).
- `POST /refugios/publicaciones/fotos`: Subida de foto de mascota (`multipart/form-data`, campo `archivo`) (RF13).

### SOAP / XML (Apache CXF)
- Header SOAP requerido: `<apiKey>UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT</apiKey>`
- En `/services/publicaciones` se requiere además `<token>...</token>` (o `Authorization: Bearer <token>`).
- `POST /services/auth`: Operaciones `login`, `logout`.
- `POST /services/publicaciones`: Operaciones `getEstadosPublicacion`, `getEspecies`, `getPublicacionesPorRefugio`, `getMascotasDisponibles`, `crearPublicacion`, `actualizarEstado`.

---

## 🧪 Usuarios de Prueba (Script de Base de Datos)

| CUIL | Contraseña | Refugio | Rol |
| :--- | :--- | :--- | :--- |
| `20-25999000-5` | `pass123` | Refugio Patitas Felices (ID: 1) | `REFUGIO` (Responsable habilitado) |
| `27-38123456-9` | `pass123` | Hogar Animal Córdoba (ID: 2) | `REFUGIO` (Responsable habilitado) |
| `20-28555666-3` | `pass123` | *Ninguno* | `CIUDADANO` (Acceso denegado a refugios) |
