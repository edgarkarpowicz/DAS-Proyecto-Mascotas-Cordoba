# Plataforma Mascotas Córdoba
### Programa Municipal de Bienestar Animal

Proyecto desarrollado en el marco de la asignatura **Diseño Avanzado de Software (DAS)** — 10° Cuatrimestre, **Ingeniería en Informática (Plan 2014)**, **Universidad Blas Pascal (UBP)**.

---

## 📋 Descripción del Proyecto

La Municipalidad de Córdoba impulsa el **Programa Municipal de Bienestar Animal** con el propósito de promover el cuidado y la tenencia responsable de mascotas a través de campañas de vacunación, esterilización, adopciones responsables y concientización comunitaria.

Actualmente, las distintas entidades involucradas (veterinarias, refugios de animales y dependencias municipales) operan de manera aislada utilizando sus propios sistemas, planillas o registros manuales. Esta fragmentación impide disponer de una visión unificada del estado sanitario y dificulta la prestación de servicios digitales ágiles a la comunidad.

La plataforma **Mascotas Córdoba** tiene como fin consolidar un **Registro Único de Mascotas** con su correspondiente **Carnet Sanitario Digital**, integrando a las organizaciones participantes sin forzarlas a reemplazar sus sistemas de gestión internos.

---

## 🎯 Objetivos Principales

- **Registro Único de Mascotas:** Consolidar un padrón municipal donde cada animal cuenta con un Número de Registro Municipal (**NRM**) único, evitando duplicidades mediante validación por microchip, datos del propietario y rasgos físicos.
- **Carnet Sanitario Digital:** Proveer una credencial digital oficial y verificable que certifique las intervenciones sanitarias vigentes (vacunación, desparasitación, esterilización, etc.).
- **Interoperabilidad Heterogénea:** Habilitar la integración de veterinarias con distintos niveles de madurez tecnológica mediante contratos de servicios **REST** y **SOAP**.
- **Gestión de Refugios y Adopciones:** Permitir a los refugios adheridos administrar los animales a su cargo y publicar mascotas disponibles para adopción responsable.
- **Portal Ciudadano Unificado:** Centralizar el acceso para los vecinos con autenticación mediante **Ciudadano Digital (CiDi)**.
- **Asistente Virtual con IA:** Brindar atención y orientación al ciudadano mediante un asistente virtual basado en Inteligencia Artificial Generativa.

---

## 👥 Actores y Stakeholders

| Actor | Rol e Interés |
| :--- | :--- |
| **Municipalidad de Córdoba** | Organismo titular y administrador del programa. Consolida la información para la planificación sanitaria y políticas de bienestar animal. |
| **Dirección de Tecnología (Muni)** | Responsable de la infraestructura, seguridad, administración técnica y coordinación de integraciones con terceros. |
| **Ciudadanos** | Propietarios de mascotas o interesados en adopción. Acceden con su cuenta de **CiDi** a sus mascotas, carnet sanitario, mapa de veterinarias/refugios y asistente virtual. |
| **Veterinarias Adheridas** | Establecimientos que participan voluntariamente reportando atenciones sanitarias mediante servicios web (REST / SOAP) o interfaces de carga. |
| **Refugios Adheridos** | Organizaciones dedicadas al rescate y cuidado animal que administran animales alojados y gestionan publicaciones de adopción. |

---

## 🧩 Módulos y Requerimientos del Sistema

El sistema se divide en las siguientes áreas funcionales:

| Área Funcional | Requerimientos | Alcance Obligatorio (1ª Entrega) |
| :--- | :--- | :---: |
| **Administración del Programa** | RF01 (Adhesión vet), RF02 (Adhesión refugios), RF03 (Aprobación org), RF04 (Configuración integración), RF05 (Campañas) | *Configuración inicial pre-cargada* |
| **Registro Único de Mascotas** | RF06 (Registrar mascotas), RF07 (Actualizar datos), RF08 (Consultar) | **RF06** (Registrar mascotas con NRM y control de duplicados) |
| **Gestión Sanitaria** | RF09 (Registrar atención), RF10 (Generar carnet digital), RF11 (Consultar/validar carnet) | **RF09, RF10, RF11** |
| **Gestión de Refugios** | RF12 (Animales bajo cuidado), RF13 (Publicaciones de adopción), RF14 (Procesos de adopción) | **RF13** (Publicar mascotas en adopción) |
| **Acceso a la Plataforma** | RF15 (Autenticación y perfiles: Ciudadano, Refugio, Veterinaria, Municipio) | **RF15** |
| **Servicios al Ciudadano** | RF16 (Portal web), RF17 (Consultar veterinarias), RF18 (Consultar refugios), RF19 (Campañas), RF20 (Mis mascotas) | **RF16, RF17, RF18, RF20** |
| **Asistente Virtual** | RF21 (Consultas en lenguaje natural con IA desacoplada) | **RF21** |

---

## 🏛️ Arquitectura y Tecnologías

La solución adopta una **Arquitectura Orientada a Servicios (SOA)** con bajo acoplamiento entre componentes, desacoplando la lógica de negocio municipal de los protocolos de integración, proveedores de identidad y modelos de IA.

### Stack Tecnológico
- **Backend:** [Java 17](https://www.oracle.com/java/) con [Spring Boot](https://spring.io/projects/spring-boot)
  - `spring-boot-starter-webmvc` para endpoints RESTful
  - `cxf-spring-boot-starter-jaxws` ([Apache CXF](https://cxf.apache.org/)) para servicios web SOAP
  - `spring-boot-starter-data-jdbc` / JDBC Templates para persistencia de datos
- **Base de Datos:** [Microsoft SQL Server](https://www.microsoft.com/sql-server)
  - Manejo de tipos de datos relacionales y dinámicos para rasgos de mascotas (`rasgos_mascotas`, `dominio_rasgos_mascotas`, `caracteristicas_mascotas`).
- **Frontend:** [Angular](https://angular.dev/) (Portal web responsivo para ciudadanos y actores del sistema).
- **Interoperabilidad:** REST (JSON) y SOAP (XML/WSDL).
- **Autenticación:** Integración con **Ciudadano Digital (CiDi)** para ciudadanos; API Keys / tokens para veterinarias y refugios.
- **Inteligencia Artificial:** Servicio de LLM generativo integrado desacoplado mediante adaptadores.

---

## 🗄️ Modelos de Datos

El repositorio cuenta con la especificación de dos modelos lógicos fundamentales:

1. **Modelo Lógico Municipal (`Mascotas Córdoba`):**
   - Gestión de `ciudadanos` (CUIL, datos de contacto).
   - `veterinarias` y `refugios` con sus configuraciones de integración (`configuracion_veterinarias` para endpoint, protocolo, credenciales/API Key).
   - `mascotas` identificadas con clave primaria `nro_reg_municipal`, asociadas a un responsable o refugio.
   - Modelo extensible de rasgos (`rasgos_mascotas`, `caracteristicas_mascotas`) para especie, raza, pelaje, etc.
   - `informacion_sanitaria` vinculada a intervenciones sanitarias (`tipos_atencion_sanitaria`) y veterinaria/profesional actuante.
   - `publicaciones_adopcion` para difusión pública de adopciones.

2. **Modelo Lógico Externo (`Veterinaria`):**
   - Representa el sistema de gestión transaccional interno de una veterinaria típica (`personas`, `profesionales`, `mascotas`, `atencion_sanitaria`, `detalle_atencion_sanitaria`, `vacunas`).
   - Sirve como referencia para el mapeo e integración de datos hacia el servicio central de la Municipalidad.

---

## 📁 Estructura del Repositorio

```text
DAS-Proyecto-Mascotas-Cordoba/
│
├── backend/mascotas-cordoba-project/  # Aplicación Backend Spring Boot (REST + SOAP + AOP)
│   ├── pom.xml                        # Dependencias (Java 17, Spring Boot, Apache CXF, AspectJ, MSSQL)
│   └── src/                           # Código fuente (Java, Recursos, Tests)
├── frontend/                          # Portal Cliente Web (Angular)
├── db/                                # Scripts SQL, esquemas DDL y datos de prueba para MS SQL Server
├── docs/                              # Documentación del proyecto
│   ├── proyecto/                      # Documentación oficial de la cátedra
│   │   ├── 01-Especificacion-de-Requerimientos.pdf
│   │   ├── 02-Modelo-logico-veterinaria.pdf
│   │   └── 03-Modelo-logico-mascotas-cba.pdf
│   └── requerimientos_funcionales/    # Detalle y análisis por requerimiento funcional
│       ├── obligatorios/              # RF obligatorios de la primera versión
│       └── no_obligatorios/           # RF complementarios
│
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🚀 Puesta en Marcha (Entorno de Desarrollo)

### Prerrequisitos
- **Java JDK 17** o superior instalado y configurado en el `PATH`.
- **Microsoft SQL Server** (local o contenedor Docker) con base de datos configurada.
- **Maven 3.8+** (o utilizar el wrapper `./mvnw` incluido).
- **Node.js** (v18+) y **Angular CLI** (para el portal cliente).

### 1. Base de Datos (Microsoft SQL Server)
1. Abrir **SQL Server Management Studio (SSMS)** o **Azure Data Studio**.
2. Abrir y ejecutar el script ubicado en:
   ```text
   db/municipalidad-creation-script.sql
   ```
   *Crea la base de datos `das-mascotas-cordoba` con todas las tablas relacionales, catálogos, stored procedures y datos semilla.*

### 2. Ejecución del Backend (Spring Boot + CXF SOAP + POA)
1. Navegar al directorio del backend:
   ```powershell
   cd backend/mascotas-cordoba-project
   ```
2. Verificar o ajustar credenciales en `src/main/resources/application.properties` (por defecto usuario `master`, base `das-mascotas-cordoba`, puerto `1433`).
3. Compilar y ejecutar la aplicación con Maven Wrapper:
   ```powershell
   # En Windows PowerShell:
   .\mvnw.cmd spring-boot:run
   # o:
   ./mvnw spring-boot:run
   ```
   *El backend quedará escuchando en `http://localhost:8080` (REST) y `http://localhost:8080/services` (SOAP).*

### 3. Ejecución del FrontEnd (Angular 17)
1. Abrir otra terminal y navegar al directorio del frontend:
   ```powershell
   cd frontend/Mascotas-Cordoba-FrontEnd
   ```
2. Instalar dependencias (solo la primera vez):
   ```powershell
   npm install
   ```
3. Iniciar el servidor de desarrollo:
   ```powershell
   npm start
   ```
4. Acceder en el navegador a: **`http://localhost:4200/`**

### 4. Credenciales de Prueba para Login
- **Refugio Principal (Patitas Felices - ID 1):** CUIL: `20-25999000-5` | Clave: `pass123` *(Carga 2 publicaciones activas iniciales)*.
- **Refugio Secundario (Hogar Animal - ID 2):** CUIL: `27-38123456-9` | Clave: `pass123` *(Sin publicaciones iniciales)*.
- **Ciudadano sin refugio:** CUIL: `20-28555666-3` | Clave: `pass123` *(Prueba de validación de rol / 401)*.

---

## 🔌 Ejemplos de Invocación de Servicios (REST y SOAP / XML)

El backend expone simultáneamente endpoints **REST (JSON)** y servicios web **SOAP (XML)** sobre el puerto `8080`.

### 🔐 Seguridad y Autenticación
* **REST:** Se envía la cabecera HTTP `X-API-Key`.  
  *Clave por defecto:* `UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
* **SOAP:** Se incluye el elemento `<apiKey>` en la cabecera SOAP (`<soapenv:Header>`).  
  *Clave por defecto:* `UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT`

---

### 🌐 1. Servicios RESTful (JSON)

#### A. Autenticación de Refugio (RF15)
* **URL:** `POST http://localhost:8080/auth/login`
* **Headers:**
  * `Content-Type: application/json`
  * `X-API-Key: UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
* **Request Body:**
  ```json
  {
    "cuil": "20-12345678-9",
    "clave": "secret123"
  }
  ```
* **Response Body (200 OK):**
  ```json
  {
    "codigoRespuesta": 1,
    "mensajeRespuesta": "Usuario autenticado exitosamente",
    "token": "4a7b9c2e1f3d4a5b6c7d8e9f0a1b2c3d",
    "usuario": {
      "idCiudadano": 5,
      "apellido": "Pérez",
      "nombre": "Juan",
      "cuil": "20-12345678-9",
      "correo": "juan.perez@refugiohuellitas.org",
      "perfil": "Refugio",
      "idRefugio": 1,
      "nombreRefugio": "Refugio Huellitas Felices",
      "razonSocialRefugio": "Asociación Civil Huellitas",
      "habilitacionMunicipal": "HAB-REF-2024-001"
    }
  }
  ```

#### B. Catálogo de Estados de Publicación (RF13)
* **URL:** `GET http://localhost:8080/refugios/publicaciones/estados`
* **Headers:**
  * `X-API-Key: UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
* **Response Body (200 OK):**
  ```json
  [
    { "codEstado": "Activa", "nomEstado": "Publicación activa para adopción" },
    { "codEstado": "Pausada", "nomEstado": "Publicación temporalmente en pausa" },
    { "codEstado": "Finalizada", "nomEstado": "Adopción concretada o retirada" }
  ]
  ```

#### C. Consultar Publicaciones de un Refugio (RF13)
* **URL:** `GET http://localhost:8080/refugios/1/publicaciones`
* **Headers:**
  * `X-API-Key: UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
* **Response Body (200 OK):**
  ```json
  [
    {
      "nroPublicacion": 101,
      "nroRegMunicipal": 5001,
      "nombreMascota": "Milo",
      "sexo": "M",
      "especie": "Canino",
      "raza": "Mestizo",
      "estadoPublicacion": "Activa",
      "fechaPublicacion": "2024-10-01"
    }
  ]
  ```

#### D. Crear Publicación de Adopción (RF13)
* **URL:** `POST http://localhost:8080/refugios/publicaciones`
* **Headers:**
  * `Content-Type: application/json`
  * `X-API-Key: UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
* **Request Body:**
  ```json
  {
    "idRefugio": 1,
    "nroRegMunicipal": 5001,
    "nombreMascota": "Milo",
    "sexo": "M",
    "añoNacimiento": 2022,
    "especie": "Canino",
    "raza": "Mestizo",
    "caracteristicasMascota": "Dócil, vacunado y castrado",
    "condicionAdopcion": "Patio cerrado y compromiso de seguimiento",
    "foto": "https://mascotascordoba.gob.ar/fotos/milo.jpg",
    "estadoPublicacion": "Activa"
  }
  ```
* **Response Body (200 OK):**
  ```json
  {
    "codigoRespuesta": 1,
    "mensajeRespuesta": "Publicación de adopción creada exitosamente",
    "nroPublicacion": 102
  }
  ```

#### E. Actualizar Estado de Publicación (RF13)
* **URL:** `PUT http://localhost:8080/refugios/publicaciones/102/estado`
* **Headers:**
  * `Content-Type: application/json`
  * `X-API-Key: UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6`
* **Request Body:**
  ```json
  {
    "idRefugio": 1,
    "nuevoEstado": "Finalizada"
  }
  ```
* **Response Body (200 OK):**
  ```json
  {
    "codigoRespuesta": 0,
    "mensajeRespuesta": "Estado de publicación actualizado a Finalizada",
    "nroPublicacion": 102
  }
  ```

---

### 📦 2. Servicios Web SOAP / XML (Apache CXF)

#### Contratos WSDL Disponibles (Inspección en Navegador):
* **Auth Service WSDL:** `http://localhost:8080/services/auth?wsdl`
* **Publicaciones Service WSDL:** `http://localhost:8080/services/publicaciones?wsdl`

#### A. Autenticación de Refugio (SOAP)
* **URL:** `POST http://localhost:8080/services/auth`
* **Headers:**
  * `Content-Type: text/xml;charset=UTF-8`
  * `SOAPAction: ""`
* **Request Envelope:**
  ```xml
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:auth="http://auth.services.mascotas.das.ubp.edu.ar/">
     <soapenv:Header>
        <apiKey>UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT</apiKey>
     </soapenv:Header>
     <soapenv:Body>
        <auth:login>
           <request>
              <cuil>20-12345678-9</cuil>
              <clave>secret123</clave>
           </request>
        </auth:login>
     </soapenv:Body>
  </soapenv:Envelope>
  ```
* **Response Envelope:**
  ```xml
  <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body>
        <ns2:loginResponse xmlns:ns2="http://auth.services.mascotas.das.ubp.edu.ar/">
           <response>
              <codigoRespuesta>1</codigoRespuesta>
              <mensajeRespuesta>Usuario autenticado exitosamente</mensajeRespuesta>
              <token>4a7b9c2e1f3d4a5b6c7d8e9f0a1b2c3d</token>
              <usuario>
                 <idCiudadano>5</idCiudadano>
                 <nombre>Juan</nombre>
                 <apellido>Pérez</apellido>
                 <cuil>20-12345678-9</cuil>
                 <correo>juan.perez@refugiohuellitas.org</correo>
                 <perfil>Refugio</perfil>
                 <idRefugio>1</idRefugio>
                 <nombreRefugio>Refugio Huellitas Felices</nombreRefugio>
                 <razonSocialRefugio>Asociación Civil Huellitas</razonSocialRefugio>
                 <habilitacionMunicipal>HAB-REF-2024-001</habilitacionMunicipal>
              </usuario>
           </response>
        </ns2:loginResponse>
     </soap:Body>
  </soap:Envelope>
  ```

#### B. Consultar Estados de Publicación (SOAP)
* **URL:** `POST http://localhost:8080/services/publicaciones`
* **Headers:**
  * `Content-Type: text/xml;charset=UTF-8`
* **Request Envelope:**
  ```xml
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:adop="http://adopciones.services.mascotas.das.ubp.edu.ar/">
     <soapenv:Header>
        <apiKey>UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT</apiKey>
     </soapenv:Header>
     <soapenv:Body>
        <adop:getEstadosPublicacion/>
     </soapenv:Body>
  </soapenv:Envelope>
  ```
* **Response Envelope:**
  ```xml
  <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body>
        <ns2:getEstadosPublicacionResponse xmlns:ns2="http://adopciones.services.mascotas.das.ubp.edu.ar/">
           <estado>
              <codEstado>Activa</codEstado>
              <nomEstado>Publicación activa para adopción</nomEstado>
           </estado>
           <estado>
              <codEstado>Pausada</codEstado>
              <nomEstado>Publicación temporalmente en pausa</nomEstado>
           </estado>
           <estado>
              <codEstado>Finalizada</codEstado>
              <nomEstado>Adopción concretada o retirada</nomEstado>
           </estado>
        </ns2:getEstadosPublicacionResponse>
     </soap:Body>
  </soap:Envelope>
  ```

#### C. Consultar Publicaciones de un Refugio (SOAP)
* **URL:** `POST http://localhost:8080/services/publicaciones`
* **Headers:**
  * `Content-Type: text/xml;charset=UTF-8`
* **Request Envelope:**
  ```xml
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:adop="http://adopciones.services.mascotas.das.ubp.edu.ar/">
     <soapenv:Header>
        <apiKey>UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT</apiKey>
     </soapenv:Header>
     <soapenv:Body>
        <adop:getPublicacionesPorRefugio>
           <idRefugio>1</idRefugio>
        </adop:getPublicacionesPorRefugio>
     </soapenv:Body>
  </soapenv:Envelope>
  ```

#### D. Crear Publicación de Adopción (SOAP)
* **URL:** `POST http://localhost:8080/services/publicaciones`
* **Headers:**
  * `Content-Type: text/xml;charset=UTF-8`
* **Request Envelope:**
  ```xml
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:adop="http://adopciones.services.mascotas.das.ubp.edu.ar/">
     <soapenv:Header>
        <apiKey>UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT</apiKey>
     </soapenv:Header>
     <soapenv:Body>
        <adop:crearPublicacion>
           <request>
              <idRefugio>1</idRefugio>
              <nroRegMunicipal>5001</nroRegMunicipal>
              <nombreMascota>Milo</nombreMascota>
              <sexo>M</sexo>
              <añoNacimiento>2022</añoNacimiento>
              <especie>Canino</especie>
              <raza>Mestizo</raza>
              <caracteristicasMascota>Dócil, vacunado y castrado</caracteristicasMascota>
              <condicionAdopcion>Patio cerrado y compromiso de seguimiento</condicionAdopcion>
              <foto>https://mascotascordoba.gob.ar/fotos/milo.jpg</foto>
              <estadoPublicacion>Activa</estadoPublicacion>
           </request>
        </adop:crearPublicacion>
     </soapenv:Body>
  </soapenv:Envelope>
  ```
* **Response Envelope:**
  ```xml
  <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body>
        <ns2:crearPublicacionResponse xmlns:ns2="http://adopciones.services.mascotas.das.ubp.edu.ar/">
           <response>
              <codigoRespuesta>1</codigoRespuesta>
              <mensajeRespuesta>Publicación de adopción creada exitosamente</mensajeRespuesta>
              <nroPublicacion>103</nroPublicacion>
           </response>
        </ns2:crearPublicacionResponse>
     </soap:Body>
  </soap:Envelope>
  ```

#### E. Actualizar Estado de Publicación (SOAP)
* **URL:** `POST http://localhost:8080/services/publicaciones`
* **Headers:**
  * `Content-Type: text/xml;charset=UTF-8`
* **Request Envelope:**
  ```xml
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:adop="http://adopciones.services.mascotas.das.ubp.edu.ar/">
     <soapenv:Header>
        <apiKey>UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT</apiKey>
     </soapenv:Header>
     <soapenv:Body>
        <adop:actualizarEstado>
           <nroPublicacion>103</nroPublicacion>
           <request>
              <idRefugio>1</idRefugio>
              <nuevoEstado>Finalizada</nuevoEstado>
           </request>
        </adop:actualizarEstado>
     </soapenv:Body>
  </soapenv:Envelope>
  ```
* **Response Envelope:**
  ```xml
  <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body>
        <ns2:actualizarEstadoResponse xmlns:ns2="http://adopciones.services.mascotas.das.ubp.edu.ar/">
           <response>
              <codigoRespuesta>0</codigoRespuesta>
              <mensajeRespuesta>Estado de publicación actualizado a Finalizada</mensajeRespuesta>
              <nroPublicacion>103</nroPublicacion>
           </response>
        </ns2:actualizarEstadoResponse>
     </soap:Body>
  </soap:Envelope>
  ```

---

## 📚 Referencias y Documentos
- [Especificación de Requerimientos](docs/proyecto/01-Especificacion-de-Requerimientos.pdf)
- [Modelo Lógico - Sistema Veterinaria](docs/proyecto/02-Modelo-logico-veterinaria.pdf)
- [Modelo Lógico - Mascotas Córdoba](docs/proyecto/03-Modelo-logico-mascotas-cba.pdf)
- [Detalle de Requerimientos Funcionales](docs/requerimientos_funcionales/README.md)