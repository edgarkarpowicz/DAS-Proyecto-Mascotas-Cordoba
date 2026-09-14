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
├── mascotas-cordoba-project/          # Aplicación Backend Spring Boot (Maven)
│   ├── pom.xml                        # Configuración de dependencias (Java 17, Spring Boot, CXF, MSSQL)
│   └── src/                           # Código fuente (Java, Recursos, Tests)
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

### Ejecución del Backend
1. Navegar al directorio del proyecto:
   ```bash
   cd mascotas-cordoba-project
   ```
2. Configurar la conexión a la base de datos en `src/main/resources/application.properties` (o variables de entorno correspondientes).
3. Compilar y ejecutar la aplicación con Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   En Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

---

## 📚 Referencias y Documentos
- [Especificación de Requerimientos](docs/proyecto/01-Especificacion-de-Requerimientos.pdf)
- [Modelo Lógico - Sistema Veterinaria](docs/proyecto/02-Modelo-logico-veterinaria.pdf)
- [Modelo Lógico - Mascotas Córdoba](docs/proyecto/03-Modelo-logico-mascotas-cba.pdf)
- [Detalle de Requerimientos Funcionales](docs/requerimientos_funcionales/README.md)