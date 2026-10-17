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

# Claves de Seguridad (API Keys)
security.soap.api-key=${SOAP_API_KEY:UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT}
security.rest.api-key=${REST_API_KEY:UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6}

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

2. **Compilar el proyecto:**
   ```powershell
   # En Windows PowerShell:
   .\mvnw.cmd compile
   # o:
   ./mvnw compile
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

## 🔌 Servicios Expuestos

### RESTful API (JSON) - Header `X-API-Key: UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
- `POST /auth/login`: Autenticación de responsables de refugios (RF15).
- `GET /refugios/publicaciones/estados`: Catálogo de estados válidos para publicaciones (RF13).
- `GET /refugios/{idRefugio}/publicaciones`: Listado de publicaciones administradas por el refugio (RF13).
- `GET /refugios/{idRefugio}/mascotas-disponibles`: Mascotas del refugio listas para publicar (RF13).
- `POST /refugios/publicaciones`: Crear nueva publicación de adopción (RF13).
- `PUT /refugios/publicaciones/{nroPublicacion}/estado`: Cambiar estado (Activa / Pausada / Finalizada) (RF13).

### SOAP / XML (Apache CXF) - Header SOAP `<apiKey>UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT</apiKey>`
- `POST /services/auth`: Operación `login`.
- `POST /services/publicaciones`: Operaciones `getEstadosPublicacion`, `getPublicacionesPorRefugio`, `crearPublicacion`, `actualizarEstado`.

---

## 🧪 Usuarios de Prueba (Script de Base de Datos)

| CUIL | Contraseña | Refugio | Rol |
| :--- | :--- | :--- | :--- |
| `20-25999000-5` | `pass123` | Refugio Patitas Felices (ID: 1) | Responsable habilitado |
| `27-38123456-9` | `pass123` | Hogar Animal Córdoba (ID: 2) | Responsable habilitado |
| `20-28555666-3` | `pass123` | *Ninguno* | Ciudadano (Acceso denegado a refugios) |
