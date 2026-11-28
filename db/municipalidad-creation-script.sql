-- =============================================================================
-- Plataforma Mascotas Córdoba - Programa Municipal de Bienestar Animal
-- Script DDL de Creación de Base de Datos y Tablas (Municipalidad)
-- =============================================================================

DROP DATABASE IF EXISTS [das-mascotas-cordoba];
CREATE DATABASE [das-mascotas-cordoba];
USE [das-mascotas-cordoba];

-- =============================================================================
-- TABLA: perfiles (RF15 - Perfiles de acceso a la plataforma)
-- =============================================================================
CREATE TABLE perfiles (
    cod_perfil  VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(128) NOT NULL,
    CONSTRAINT pk_perfiles PRIMARY KEY (cod_perfil)
);

-- =============================================================================
-- TABLA: ciudadanos
-- =============================================================================
CREATE TABLE ciudadanos (
    id_ciudadano INT IDENTITY(1,1) NOT NULL,
    apellido VARCHAR(64) NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    cuil VARCHAR(20) NOT NULL,
    clave VARCHAR(128) NOT NULL,
    correo VARCHAR(128) NOT NULL,
    telefono VARCHAR(32) NOT NULL,
    domicilio VARCHAR(128) NOT NULL,
    cod_perfil VARCHAR(30) NOT NULL DEFAULT 'CIUDADANO',
    habilitado BIT NOT NULL DEFAULT 1,
    CONSTRAINT pk_ciudadanos PRIMARY KEY (id_ciudadano),
    CONSTRAINT uq_ciudadanos_cuil UNIQUE (cuil),
    CONSTRAINT fk_ciudadanos_perfiles FOREIGN KEY (cod_perfil)
        REFERENCES perfiles(cod_perfil)
);

-- =============================================================================
-- TABLA: veterinarias
-- =============================================================================
CREATE TABLE veterinarias (
    id_veterinaria INT IDENTITY(1,1) NOT NULL,
    nombre VARCHAR(128) NOT NULL,
    razon_social VARCHAR(128) NOT NULL,
    correo VARCHAR(128) NOT NULL,
    telefono VARCHAR(32) NOT NULL,
    domicilio VARCHAR(128) NOT NULL,
    habilitacion_municipal VARCHAR(64) NOT NULL,
    habilitada BIT NOT NULL DEFAULT 1,
    horarios VARCHAR(128) NOT NULL,
    servicios_prestados VARCHAR(500) NOT NULL,
    id_responsable INT NULL,
    CONSTRAINT pk_veterinarias PRIMARY KEY (id_veterinaria),
    CONSTRAINT fk_veterinarias_ciudadanos FOREIGN KEY (id_responsable)
        REFERENCES ciudadanos(id_ciudadano)
);

-- =============================================================================
-- TABLA: atributos_sistema
-- =============================================================================
CREATE TABLE atributos_sistema (
    cod_atributo INT NOT NULL,
    desc_atributo VARCHAR(128) NOT NULL,
    tipo_dato VARCHAR(32) NOT NULL,
    observ_atributo VARCHAR(255) NULL,
    CONSTRAINT pk_atributos_sistema PRIMARY KEY (cod_atributo)
);

-- =============================================================================
-- TABLA: configuracion_veterinarias
-- =============================================================================
CREATE TABLE configuracion_veterinarias (
    id_veterinaria INT NOT NULL,
    cod_atributo INT NOT NULL,
    valor VARCHAR(255) NOT NULL,
    CONSTRAINT pk_configuracion_veterinarias PRIMARY KEY (id_veterinaria, cod_atributo),
    CONSTRAINT fk_config_vet_veterinarias FOREIGN KEY (id_veterinaria)
        REFERENCES veterinarias(id_veterinaria)
        ON DELETE CASCADE,
    CONSTRAINT fk_config_vet_atributos FOREIGN KEY (cod_atributo)
        REFERENCES atributos_sistema(cod_atributo)
        ON DELETE CASCADE
);

-- =============================================================================
-- TABLA: profesionales_veterinarias
-- =============================================================================
CREATE TABLE profesionales_veterinarias (
    id_veterinaria INT NOT NULL,
    id_profesional INT NOT NULL,
    baja BIT NOT NULL DEFAULT 0,
    CONSTRAINT pk_profesionales_veterinarias PRIMARY KEY (id_veterinaria, id_profesional),
    CONSTRAINT fk_prof_vet_veterinarias FOREIGN KEY (id_veterinaria)
        REFERENCES veterinarias(id_veterinaria)
        ON DELETE CASCADE
);

-- =============================================================================
-- TABLA: refugios
-- =============================================================================
CREATE TABLE refugios (
    id_refugio INT IDENTITY(1,1) NOT NULL,
    nombre VARCHAR(128) NULL,
    razon_social VARCHAR(128) NOT NULL,
    correo VARCHAR(128) NOT NULL,
    telefono VARCHAR(32) NOT NULL,
    domicilio VARCHAR(128) NOT NULL,
    habilitacion_municipal VARCHAR(64) NOT NULL,
    id_responsable INT NOT NULL,
    horarios VARCHAR(128) NULL,
    CONSTRAINT pk_refugios PRIMARY KEY (id_refugio),
    CONSTRAINT fk_refugios_ciudadanos FOREIGN KEY (id_responsable)
        REFERENCES ciudadanos(id_ciudadano)
);

-- =============================================================================
-- TABLA: rasgos_mascotas
-- =============================================================================
CREATE TABLE rasgos_mascotas (
    cod_rasgo INT NOT NULL,
    nom_rasgo VARCHAR(64) NOT NULL,
    CONSTRAINT pk_rasgos_mascotas PRIMARY KEY (cod_rasgo)
);

-- =============================================================================
-- TABLA: dominio_rasgos_mascotas
-- =============================================================================
CREATE TABLE dominio_rasgos_mascotas (
    cod_rasgo INT NOT NULL,
    nro_valor_dominio INT NOT NULL,
    nom_valor_dominio VARCHAR(64) NOT NULL,
    CONSTRAINT pk_dominio_rasgos_mascotas PRIMARY KEY (cod_rasgo, nro_valor_dominio),
    CONSTRAINT fk_dominio_rasgos_mascotas FOREIGN KEY (cod_rasgo)
        REFERENCES rasgos_mascotas(cod_rasgo)
        ON DELETE CASCADE
);

-- =============================================================================
-- TABLA: mascotas
-- =============================================================================
CREATE TABLE mascotas (
    nro_reg_municipal INT NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    sexo CHAR(1) NOT NULL,
    [año_nacimiento] INT NOT NULL,
    microchip VARCHAR(64) NULL,
    castrado BIT NOT NULL DEFAULT 0,
    vive BIT NOT NULL DEFAULT 1,
    id_responsable INT NULL,
    id_refugio INT NULL,
    CONSTRAINT pk_mascotas PRIMARY KEY (nro_reg_municipal),
    CONSTRAINT chk_mascotas_sexo CHECK (sexo IN ('m', 'h', 'M', 'H')),
    CONSTRAINT chk_mascotas_vive CHECK (vive IN (1, 0)),
    CONSTRAINT fk_mascotas_ciudadanos FOREIGN KEY (id_responsable)
        REFERENCES ciudadanos(id_ciudadano),
    CONSTRAINT fk_mascotas_refugios FOREIGN KEY (id_refugio)
        REFERENCES refugios(id_refugio)
);

-- =============================================================================
-- TABLA: caracteristicas_mascotas
-- =============================================================================
CREATE TABLE caracteristicas_mascotas (
    nro_reg_municipal INT NOT NULL,
    cod_rasgo INT NOT NULL,
    nro_caracteristica INT NOT NULL,
    nro_valor_dominio INT NULL,
    valor_caracteristica VARCHAR(128) NULL,
    CONSTRAINT pk_caracteristicas_mascotas PRIMARY KEY (nro_reg_municipal, cod_rasgo, nro_caracteristica),
    CONSTRAINT fk_caract_mascotas_mascotas FOREIGN KEY (nro_reg_municipal)
        REFERENCES mascotas(nro_reg_municipal)
        ON DELETE CASCADE,
    CONSTRAINT fk_caract_mascotas_rasgos FOREIGN KEY (cod_rasgo)
        REFERENCES rasgos_mascotas(cod_rasgo),
    CONSTRAINT fk_caract_mascotas_dominio FOREIGN KEY (cod_rasgo, nro_valor_dominio)
        REFERENCES dominio_rasgos_mascotas(cod_rasgo, nro_valor_dominio)
);

-- =============================================================================
-- TABLA: tipos_atencion_sanitaria
-- =============================================================================
CREATE TABLE tipos_atencion_sanitaria (
    cod_tipo_atencion INT NOT NULL,
    desc_tipo_atencion VARCHAR(64) NOT NULL,
    CONSTRAINT pk_tipos_atencion_sanitaria PRIMARY KEY (cod_tipo_atencion)
);

-- =============================================================================
-- TABLA: informacion_sanitaria
-- =============================================================================
CREATE TABLE informacion_sanitaria (
    nro_reg_municipal INT NOT NULL,
    nro_registro INT NOT NULL,
    fecha_atencion DATE NOT NULL,
    cod_tipo_atencion INT NOT NULL,
    detalle_atencion VARCHAR(255) NOT NULL,
    fecha_vencimiento DATE NULL,
    id_veterinaria INT NOT NULL,
    id_profesional INT NULL,
    profesional_texto VARCHAR(128) NULL,
    nro_atencion_externa INT NULL,
    nro_detalle_externo INT NULL,
    CONSTRAINT pk_informacion_sanitaria PRIMARY KEY (nro_reg_municipal, nro_registro),
    CONSTRAINT fk_info_sanitaria_mascotas FOREIGN KEY (nro_reg_municipal)
        REFERENCES mascotas(nro_reg_municipal)
        ON DELETE CASCADE,
    CONSTRAINT fk_info_sanitaria_tipos FOREIGN KEY (cod_tipo_atencion)
        REFERENCES tipos_atencion_sanitaria(cod_tipo_atencion),
    CONSTRAINT fk_info_sanitaria_veterinarias FOREIGN KEY (id_veterinaria)
        REFERENCES veterinarias(id_veterinaria)
);

CREATE UNIQUE INDEX uq_info_sanitaria_externa
    ON informacion_sanitaria (id_veterinaria, nro_atencion_externa, nro_detalle_externo)
    WHERE nro_atencion_externa IS NOT NULL AND nro_detalle_externo IS NOT NULL;

-- =============================================================================
-- TABLA: carnets_sanitarios (RF10 / RF11)
-- =============================================================================
CREATE TABLE carnets_sanitarios (
    id_carnet INT IDENTITY(1,1) NOT NULL,
    nro_reg_municipal INT NOT NULL,
    codigo_validacion VARCHAR(64) NOT NULL,
    fecha_emision DATE NOT NULL,
    vigente BIT NOT NULL DEFAULT 1,
    id_emisor INT NULL,
    CONSTRAINT pk_carnets_sanitarios PRIMARY KEY (id_carnet),
    CONSTRAINT uq_carnets_sanitarios_codigo UNIQUE (codigo_validacion),
    CONSTRAINT fk_carnets_sanitarios_mascotas FOREIGN KEY (nro_reg_municipal)
        REFERENCES mascotas(nro_reg_municipal)
        ON DELETE CASCADE,
    CONSTRAINT fk_carnets_sanitarios_emisor FOREIGN KEY (id_emisor)
        REFERENCES ciudadanos(id_ciudadano)
);

-- =============================================================================
-- TABLA: sincronizacion_mascotas_veterinaria (RF06)
-- Mapea el identificador interno de una clínica adherida al NRM municipal y
-- controla el acuse de recibo pendiente.
-- =============================================================================
CREATE TABLE sincronizacion_mascotas_veterinaria (
    id_veterinaria INT NOT NULL,
    id_mascota_externa VARCHAR(64) NOT NULL,
    nro_reg_municipal INT NOT NULL,
    nrm_confirmado BIT NOT NULL DEFAULT 0,
    fecha_sincronizacion DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT pk_sinc_mascotas_vet PRIMARY KEY (id_veterinaria, id_mascota_externa),
    CONSTRAINT fk_sinc_mascotas_vet_vet FOREIGN KEY (id_veterinaria)
        REFERENCES veterinarias(id_veterinaria),
    CONSTRAINT fk_sinc_mascotas_vet_mascota FOREIGN KEY (nro_reg_municipal)
        REFERENCES mascotas(nro_reg_municipal)
        ON DELETE CASCADE
);

-- =============================================================================
-- TABLA: sincronizacion_lotes_veterinaria (RF06 / RF09 / RNF06)
-- Registra la bitácora y marca de agua de cada corrida por lotes por clínica.
-- =============================================================================
CREATE TABLE sincronizacion_lotes_veterinaria (
    id_lote BIGINT IDENTITY(1,1) NOT NULL,
    id_veterinaria INT NOT NULL,
    tipo_lote VARCHAR(32) NOT NULL,
    fecha_desde DATE NOT NULL,
    fecha_hasta DATE NOT NULL,
    fecha_ejecucion DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    estado VARCHAR(20) NOT NULL,
    registros_procesados INT NOT NULL DEFAULT 0,
    registros_nuevos INT NOT NULL DEFAULT 0,
    registros_deduplicados INT NOT NULL DEFAULT 0,
    registros_confirmados INT NOT NULL DEFAULT 0,
    detalle_error VARCHAR(500) NULL,
    CONSTRAINT pk_sinc_lotes_vet PRIMARY KEY (id_lote),
    CONSTRAINT fk_sinc_lotes_vet_vet FOREIGN KEY (id_veterinaria)
        REFERENCES veterinarias(id_veterinaria)
);

-- =============================================================================
-- TABLA: estados_publicacion
-- =============================================================================
CREATE TABLE estados_publicacion (
    cod_estado VARCHAR(20) NOT NULL,
    descripcion VARCHAR(128) NOT NULL,
    CONSTRAINT pk_estados_publicacion PRIMARY KEY (cod_estado)
);

-- =============================================================================
-- TABLA: publicaciones_adopcion
-- =============================================================================
CREATE TABLE publicaciones_adopcion (
    nro_publicacion INT IDENTITY(1,1) NOT NULL,
    nro_reg_municipal INT NOT NULL,
    id_refugio INT NOT NULL,
    fecha_publicacion DATE NOT NULL,
    caracteristicas_mascota VARCHAR(MAX) NOT NULL,
    condicion_adopcion VARCHAR(MAX) NOT NULL,
    foto VARCHAR(255) NULL,
    estado_publicacion VARCHAR(20) NOT NULL,
    CONSTRAINT pk_publicaciones_adopcion PRIMARY KEY (nro_publicacion),
    CONSTRAINT fk_pub_adopcion_estados FOREIGN KEY (estado_publicacion)
        REFERENCES estados_publicacion(cod_estado),
    CONSTRAINT fk_pub_adopcion_mascotas FOREIGN KEY (nro_reg_municipal)
        REFERENCES mascotas(nro_reg_municipal)
        ON DELETE CASCADE,
    CONSTRAINT fk_pub_adopcion_refugios FOREIGN KEY (id_refugio)
        REFERENCES refugios(id_refugio)
);

-- =============================================================================
-- TABLA: auditoria_operaciones (Trazabilidad con identidad del usuario)
-- =============================================================================
CREATE TABLE auditoria_operaciones (
    id_auditoria BIGINT       IDENTITY(1,1) NOT NULL,
    fecha_hora   DATETIME2    NOT NULL DEFAULT SYSDATETIME(),
    id_ciudadano INT          NULL,
    cuil         VARCHAR(20)  NULL,
    perfil       VARCHAR(30)  NULL,
    canal        VARCHAR(20)  NOT NULL,
    operacion    VARCHAR(150) NOT NULL,
    resultado    VARCHAR(30)  NOT NULL,
    detalle      VARCHAR(500) NULL,
    CONSTRAINT pk_auditoria_operaciones PRIMARY KEY (id_auditoria)
);

-- =============================================================================
-- DATOS SEMILLA / INICIALES (Catálogos, Entidades y Tablas Intermedias)
-- =============================================================================

-- 0. Perfiles de Acceso (RF15)
INSERT INTO perfiles (cod_perfil, descripcion) VALUES
('CIUDADANO',     'Ciudadano propietario de mascotas o interesado en adopción'),
('REFUGIO',       'Usuario autorizado perteneciente a un refugio adherido'),
('VETERINARIA',   'Usuario autorizado perteneciente a una veterinaria adherida'),
('MUNICIPALIDAD', 'Personal autorizado de la Municipalidad de Córdoba');

-- 1. Ciudadanos (Propietarios, Responsables de Refugios, Veterinarias y Municipalidad)
SET IDENTITY_INSERT ciudadanos ON;
INSERT INTO ciudadanos (id_ciudadano, apellido, nombre, cuil, clave, correo, telefono, domicilio, cod_perfil, habilitado) VALUES
(1, 'Pérez', 'Juan Carlos', '20-28555666-3', 'pass123', 'juan.perez@gmail.com', '3514567890', 'San Jerónimo 450, Córdoba', 'CIUDADANO', 1),
(2, 'Gómez', 'María Belén', '27-34777888-4', 'pass123', 'maria.gomez@gmail.com', '3515678901', 'Av. General Paz 1230, Córdoba', 'CIUDADANO', 1),
(3, 'Sánchez', 'Roberto', '20-25999000-5', 'pass123', 'roberto.sanchez@patitasfelices.org', '3516789012', 'Camino a 60 Cuadras Km 8, Córdoba', 'REFUGIO', 1),
(4, 'Morales', 'Ana Carolina', '27-38123456-9', 'pass123', 'ana.morales@hogaranimal.org', '3517890123', 'Bv. San Juan 890, Córdoba', 'REFUGIO', 1),
(5, 'Quito', 'Esteban', '20-30111222-8', 'pass123', 'esteban.quito@sanantoniovet.com', '3514001122', 'Av. San Martín 120, Villa Carlos Paz', 'VETERINARIA', 1),
(6, 'Benítez', 'Laura', '27-22333444-1', 'pass123', 'laura.benitez@cordoba.gob.ar', '3514285600', 'Marcelo T. de Alvear 120, Córdoba', 'MUNICIPALIDAD', 1);
SET IDENTITY_INSERT ciudadanos OFF;

-- 2. Veterinarias Adheridas (RF17)
SET IDENTITY_INSERT veterinarias ON;
INSERT INTO veterinarias (id_veterinaria, nombre, razon_social, correo, telefono, domicilio, habilitacion_municipal, habilitada, horarios, servicios_prestados, id_responsable) VALUES
(1, 'San Antonio Vet', 'Veterinaria San Antonio S.R.L.', 'sanantonio@gmail.com', '543423431', 'Roca 221, Villa Carlos Paz, Córdoba', 'HAB-MUNI-00123', 1, '13:00 - 22:00', 'Castración, Desparasitación, Vacunación, Guardia 24hs', 5),
(2, 'Veterinaria Central Córdoba', 'Centro Veterinario Cba S.A.', 'contacto@vetcentralcba.com', '3514223344', 'Av. Colón 750, Córdoba', 'HAB-MUNI-00456', 1, '08:00 - 20:00', 'Vacunación, Esterilización, Consulta clínica', NULL),
(3, 'Clínica Veterinaria Alta Córdoba', 'Alta Córdoba Mascotas S.A.', 'info@vetaltacba.com', '3514712000', 'Mariano Fragueiro 1950, Córdoba', 'HAB-MUNI-00789', 1, '09:00 - 19:00', 'Vacunación, Castración, Cirugía general', NULL);
SET IDENTITY_INSERT veterinarias OFF;

-- 3. Atributos de Sistema (Metadatos de integración técnica - RF04)
INSERT INTO atributos_sistema (cod_atributo, desc_atributo, tipo_dato, observ_atributo) VALUES
(1, 'URL del servicio', 'STRING', 'Endpoint base para invocar el servicio de la veterinaria'),
(2, 'Tecnología servicio', 'STRING', 'Protocolo de integración: REST o SOAP'),
(3, 'Parametros servicio', 'STRING', 'Parámetros o headers adicionales de configuración'),
(4, 'Api Key', 'STRING', 'Clave de autenticación / token de la veterinaria');

-- 4. Configuración de Veterinarias (Tabla Intermedia: Veterinarias <-> Atributos de Sistema)
-- Configuración de Vet 1 (Integración REST)
INSERT INTO configuracion_veterinarias (id_veterinaria, cod_atributo, valor) VALUES
(1, 1, 'http://localhost:8081/api/veterinaria'),
(1, 2, 'REST'),
(1, 3, 'timeout=5000;format=json'),
(1, 4, 'UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6');

-- Configuración de Vet 2 (Integración SOAP)
INSERT INTO configuracion_veterinarias (id_veterinaria, cod_atributo, valor) VALUES
(2, 1, 'http://localhost:8082/services/VeterinariaSoapService'),
(2, 2, 'SOAP'),
(2, 3, 'wsdl=http://localhost:8082/services/VeterinariaSoapService?wsdl'),
(2, 4, 'UBPqnXyjsMPkbYN4Qj1ag3zveiimsMAPjyT');

-- Configuración de Vet 3 (Integración REST)
INSERT INTO configuracion_veterinarias (id_veterinaria, cod_atributo, valor) VALUES
(3, 1, 'http://localhost:8083/api/v1/integracion'),
(3, 2, 'REST'),
(3, 3, 'timeout=4000;version=v1'),
(3, 4, 'VET_ALTA_CBA_TOKEN_9988');

-- 5. Profesionales por Veterinaria (Tabla Intermedia: Veterinarias <-> Profesionales)
INSERT INTO profesionales_veterinarias (id_veterinaria, id_profesional, baja) VALUES
(1, 1, 0), -- Dr. Esteban Quito en San Antonio Vet
(1, 2, 0), -- Dra. Laura Martínez en San Antonio Vet
(2, 3, 0), -- Dr. Martín Ferrero en Veterinaria Central
(3, 4, 0); -- Dra. Sofía Valenzuela en Alta Córdoba

-- 6. Refugios Adheridos (Vinculados a Ciudadanos Responsables)
SET IDENTITY_INSERT refugios ON;
INSERT INTO refugios (id_refugio, nombre, razon_social, correo, telefono, domicilio, habilitacion_municipal, id_responsable, horarios) VALUES
(1, 'Refugio Patitas Felices', 'Asociación Civil Patitas Felices Córdoba', 'contacto@patitasfelices.org', '3516789012', 'Camino a 60 Cuadras Km 8, Córdoba', 'REF-MUNI-0001', 3, 'Sábados y Domingos 10:00 - 17:00'),
(2, 'Hogar Animal Córdoba', 'Fundación Hogar Animal Cba', 'adopciones@hogaranimalcba.org', '3518901234', 'Ruta 20 Km 12, Malagueño', 'REF-MUNI-0002', 4, 'Lunes a Viernes 14:00 - 18:00');
SET IDENTITY_INSERT refugios OFF;

-- 7. Rasgos de Mascotas
INSERT INTO rasgos_mascotas (cod_rasgo, nom_rasgo) VALUES
(1, 'Especie'),
(2, 'Raza'),
(3, 'Pelaje'),
(4, 'Color'),
(5, 'Tamaño');

-- 8. Dominio de Rasgos de Mascotas (Tabla Intermedia / Dominio de Valores)
INSERT INTO dominio_rasgos_mascotas (cod_rasgo, nro_valor_dominio, nom_valor_dominio) VALUES
-- Especie (cod_rasgo = 1)
(1, 1, 'Perro'),
(1, 2, 'Gato'),
(1, 3, 'Conejo'),
(1, 4, 'Otro'),
-- Raza (cod_rasgo = 2)
(2, 1, 'Border Collie'),
(2, 2, 'Labrador'),
(2, 3, 'Mestizo Canino'),
(2, 4, 'Siamés'),
(2, 5, 'Mestizo Felino'),
-- Pelaje (cod_rasgo = 3)
(3, 1, 'Corto'),
(3, 2, 'Medio'),
(3, 3, 'Largo'),
(3, 4, 'Rizado'),
-- Color (cod_rasgo = 4)
(4, 1, 'Negro'),
(4, 2, 'Blanco'),
(4, 3, 'Dorado'),
(4, 4, 'Marrón'),
(4, 5, 'Bicolor / Manchado'),
-- Tamaño (cod_rasgo = 5)
(5, 1, 'Pequeño'),
(5, 2, 'Mediano'),
(5, 3, 'Grande'),
(5, 4, 'Gigante');

-- 9. Mascotas (Registro Único Municipal con NRM)
INSERT INTO mascotas (nro_reg_municipal, nombre, sexo, [año_nacimiento], microchip, castrado, vive, id_responsable, id_refugio) VALUES
(100001, 'Firulais', 'M', 2021, '981098104523123', 0, 1, 1, NULL),
(100002, 'Milo', 'M', 2022, '981098107845612', 1, 1, 2, NULL),
(100003, 'Luna', 'H', 2023, '981098109923841', 0, 1, 1, NULL),
(100004, 'Rocco', 'M', 2023, '981098101112233', 1, 1, NULL, 1),
(100005, 'Mía', 'H', 2024, NULL, 0, 1, NULL, 1),
(100006, 'Thor', 'M', 2022, '981098104455667', 0, 1, NULL, 2);

-- 10. Características de Mascotas (Tabla Intermedia: Mascotas <-> Rasgos <-> Dominio)
INSERT INTO caracteristicas_mascotas (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica) VALUES
-- Firulais (100001)
(100001, 1, 1, 1, 'Perro'),
(100001, 2, 1, 1, 'Border Collie'),
(100001, 3, 1, 3, 'Largo'),
(100001, 4, 1, 3, 'Dorado brillante'),
(100001, 5, 1, 2, 'Mediano'),
-- Milo (100002)
(100002, 1, 1, 1, 'Perro'),
(100002, 2, 1, 3, 'Mestizo Canino'),
(100002, 3, 1, 1, 'Corto'),
(100002, 4, 1, 5, 'Negro con pecho blanco'),
(100002, 5, 1, 2, 'Mediano'),
-- Luna (100003)
(100003, 1, 1, 2, 'Gato'),
(100003, 2, 1, 4, 'Siamés'),
(100003, 3, 1, 1, 'Corto'),
(100003, 4, 1, NULL, 'Crema con extremidades oscuras'),
(100003, 5, 1, 1, 'Pequeño'),
-- Rocco (100004 - En Refugio)
(100004, 1, 1, 1, 'Perro'),
(100004, 2, 1, 3, 'Mestizo Canino'),
(100004, 3, 1, 1, 'Corto'),
(100004, 4, 1, 1, 'Negro'),
(100004, 5, 1, 3, 'Grande'),
-- Mía (100005 - En Refugio)
(100005, 1, 1, 2, 'Gato'),
(100005, 2, 1, 5, 'Mestizo Felino'),
(100005, 3, 1, 2, 'Medio'),
(100005, 4, 1, 5, 'Tricolor / Manchada'),
(100005, 5, 1, 1, 'Pequeño'),
-- Thor (100006 - En Hogar Animal)
(100006, 1, 1, 1, 'Perro'),
(100006, 2, 1, 3, 'Mestizo Canino'),
(100006, 3, 1, 1, 'Corto'),
(100006, 4, 1, 3, 'Dorado'),
(100006, 5, 1, 2, 'Mediano');

-- 11. Tipos de Prácticas Sanitarias
INSERT INTO tipos_atencion_sanitaria (cod_tipo_atencion, desc_tipo_atencion) VALUES
(1, 'Vacunación'),
(2, 'Desparasitación'),
(3, 'Esterilización'),
(4, 'Castración'),
(5, 'Consulta clínica');

-- 12. Información Sanitaria (Tabla Intermedia: Mascotas <-> Prácticas <-> Veterinarias/Profesionales)
INSERT INTO informacion_sanitaria (nro_reg_municipal, nro_registro, fecha_atencion, cod_tipo_atencion, detalle_atencion, fecha_vencimiento, id_veterinaria, id_profesional, profesional_texto) VALUES
(100001, 1, '2026-04-10', 1, 'Vacuna Antirrábica Obligatoria Anual - Lote RAB-2026-X9', '2027-04-10', 1, 1, 'Dr. Esteban Quito'),
(100001, 2, '2026-09-15', 2, 'Desparasitación interna de amplio espectro - Lote DESP-552', '2027-03-15', 1, 2, 'Dra. Laura Martínez'),
(100002, 1, '2026-05-20', 4, 'Castración electiva bajo Programa Municipal de Esterilización', NULL, 1, 1, 'Dr. Esteban Quito'),
(100003, 1, '2026-04-12', 1, 'Vacunación Triple Felina Preventiva - Lote FEL-3312', '2027-04-12', 1, 2, 'Dra. Laura Martínez'),
(100004, 1, '2026-06-18', 1, 'Vacunación quíntuple canina y antirrábica de ingreso al refugio', '2027-06-18', 2, 3, 'Dr. Martín Ferrero'),
(100004, 2, '2026-03-01', 4, 'Castración quirúrgica previa a habilitación de adopción', NULL, 2, 3, 'Dr. Martín Ferrero'),
(100006, 1, '2024-04-10', 1, 'Vacunación quíntuple canina anual de ingreso a Hogar Animal', '2025-04-10', 2, 3, 'Dr. Martín Ferrero'),
(100006, 2, '2024-05-02', 2, 'Desparasitación interna preventiva', '2024-11-02', 2, 3, 'Dr. Martín Ferrero');

-- 12b. Carnets Sanitarios Digitales (RF10 / RF11)
INSERT INTO carnets_sanitarios (nro_reg_municipal, codigo_validacion, fecha_emision, vigente, id_emisor) VALUES
(100001, 'CBA-100001-SEED01', '2026-10-01', 1, 1);

-- 13. Estados de Publicaciones de Adopción
INSERT INTO estados_publicacion (cod_estado, descripcion) VALUES
('Activa', 'Publicación visible y disponible para adopción'),
('Pausada', 'Publicación pausada temporalmente por el refugio'),
('Finalizada', 'Proceso de adopción concretado exitosamente');

-- 14. Publicaciones de Adopción (Tabla Intermedia: Mascotas <-> Refugios)
SET IDENTITY_INSERT publicaciones_adopcion ON;
INSERT INTO publicaciones_adopcion (nro_publicacion, nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, foto, estado_publicacion) VALUES
(1, 100004, 1, '2024-03-05', 'Rocco es un perro macho mestizo de 1 año y medio, muy enérgico, cariñoso, castrado y con plan sanitario al día. Se lleva excelente con otros perros y niños.', 'Hogar con patio cerrado, compromiso de seguimiento y paseos diarios. Firma de acta de adopción responsable.', '/uploads/publicaciones/rocco_100004.jpg', 'Activa'),
(2, 100005, 1, '2024-04-01', 'Mía es una gatita de 6 meses, juguetona y muy dulce, desparasitada y con primera dosis de vacunas. Lista para integrarse a una familia.', 'Depto o casa con protecciones en balcones/ventanas. Compromiso de castración al cumplir la edad adecuada.', '/uploads/publicaciones/mia_100005.jpg', 'Activa');
SET IDENTITY_INSERT publicaciones_adopcion OFF;
GO

-- =============================================================================
-- PROCEDIMIENTOS ALMACENADOS (Stored Procedures)
-- =============================================================================

/* -----------------------------------------------------------------------------
   Procedimiento: autenticar_usuario (RF15)
   Autentica a un usuario por CUIL y clave distinguiendo los perfiles:
   CIUDADANO, REFUGIO, VETERINARIA y MUNICIPALIDAD.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.autenticar_usuario
(
    @cuil  VARCHAR(20),
    @clave VARCHAR(128)
)
AS
BEGIN
    SET NOCOUNT ON;

    -- Normalizar CUIL removiendo guiones y espacios si los hubiere
    DECLARE @cuil_clean VARCHAR(20) = REPLACE(REPLACE(TRIM(@cuil), '-', ''), ' ', '');

    SELECT idCiudadano           = c.id_ciudadano,
           apellido              = c.apellido,
           nombre                = c.nombre,
           cuil                  = c.cuil,
           correo                = c.correo,
           perfil                = UPPER(c.cod_perfil),
           idRefugio             = r.id_refugio,
           nombreRefugio         = COALESCE(r.nombre, r.razon_social),
           razonSocialRefugio    = r.razon_social,
           habilitacionMunicipal = COALESCE(r.habilitacion_municipal, v.habilitacion_municipal),
           idVeterinaria         = v.id_veterinaria,
           nombreVeterinaria     = v.nombre
      FROM dbo.ciudadanos c (NOLOCK)
      LEFT JOIN dbo.refugios r (NOLOCK)
        ON r.id_responsable = c.id_ciudadano
       AND UPPER(c.cod_perfil) = 'REFUGIO'
      LEFT JOIN dbo.veterinarias v (NOLOCK)
        ON v.id_responsable = c.id_ciudadano
       AND v.habilitada = 1
       AND UPPER(c.cod_perfil) = 'VETERINARIA'
     WHERE (c.cuil = @cuil OR REPLACE(REPLACE(c.cuil, '-', ''), ' ', '') = @cuil_clean)
       AND c.clave = @clave
       AND c.habilitado = 1
       AND (
           UPPER(c.cod_perfil) IN ('CIUDADANO', 'MUNICIPALIDAD')
           OR (UPPER(c.cod_perfil) = 'REFUGIO' AND r.id_refugio IS NOT NULL)
           OR (UPPER(c.cod_perfil) = 'VETERINARIA' AND v.id_veterinaria IS NOT NULL)
       );
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: registrar_auditoria_operacion (RF15 / RNF08)
   Registra la identidad del usuario y el resultado de operaciones trazables.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.registrar_auditoria_operacion
(
    @id_ciudadano INT = NULL,
    @cuil         VARCHAR(20) = NULL,
    @perfil       VARCHAR(30) = NULL,
    @canal        VARCHAR(20),
    @operacion    VARCHAR(150),
    @resultado    VARCHAR(30),
    @detalle      VARCHAR(500) = NULL
)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.auditoria_operaciones
        (fecha_hora, id_ciudadano, cuil, perfil, canal, operacion, resultado, detalle)
    VALUES
        (SYSDATETIME(), @id_ciudadano, @cuil, @perfil, @canal, @operacion, @resultado, @detalle);
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_publicaciones_refugio (RF13)
   Obtiene el listado de publicaciones de adopción administradas por un refugio,
   incluyendo datos y rasgos principales de cada mascota para la pantalla principal.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_publicaciones_refugio
(
    @id_refugio INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroPublicacion        = p.nro_publicacion,
           nroRegMunicipal       = p.nro_reg_municipal,
           idRefugio             = p.id_refugio,
           fechaPublicacion      = p.fecha_publicacion,
           caracteristicasMascota= p.caracteristicas_mascota,
           condicionAdopcion     = p.condicion_adopcion,
           foto                  = p.foto,
           estadoPublicacion     = p.estado_publicacion,
           descripcionEstado     = ep.descripcion,
           nombreMascota         = m.nombre,
           sexo                  = m.sexo,
           añoNacimiento         = m.[año_nacimiento],
           edadAproximada        = (YEAR(GETDATE()) - m.[año_nacimiento]),
           especie               = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica 
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK) 
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1), 
               'Perro'
           ),
           raza                  = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica 
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK) 
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2), 
               'Mestizo'
           )
      FROM dbo.publicaciones_adopcion p (NOLOCK)
      JOIN dbo.mascotas m (NOLOCK)
        ON m.nro_reg_municipal = p.nro_reg_municipal
      JOIN dbo.estados_publicacion ep (NOLOCK)
        ON ep.cod_estado = p.estado_publicacion
     WHERE p.id_refugio = @id_refugio
     ORDER BY p.nro_publicacion DESC;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: ins_mascota_refugio (RF06 / RF13)
   Da de alta en el Registro Único una mascota bajo cuidado del refugio, con su
   especie (cod_rasgo = 1) y su raza (cod_rasgo = 2).
   Solo accede a datos: las validaciones y la decisión de dar el alta están en
   PublicacionesService, que lo invoca dentro de una transacción.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.ins_mascota_refugio
(
    @id_refugio         INT,
    @nombre             VARCHAR(64),
    @sexo               CHAR(1),
    @año_nacimiento     INT,
    @especie            VARCHAR(64) = NULL,
    @raza               VARCHAR(64) = NULL,
    @nro_reg_municipal  INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;

    -- UPDLOCK/HOLDLOCK: dos altas simultáneas no pueden obtener el mismo número
    -- mientras dure la transacción del servicio.
    SELECT @nro_reg_municipal = COALESCE(MAX(nro_reg_municipal), 100000) + 1
      FROM dbo.mascotas WITH (UPDLOCK, HOLDLOCK);

    INSERT INTO dbo.mascotas (nro_reg_municipal, nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
    VALUES (@nro_reg_municipal, @nombre, @sexo, @año_nacimiento, NULL, 1, NULL, @id_refugio);

    IF @especie IS NOT NULL AND TRIM(@especie) <> ''
        INSERT INTO dbo.caracteristicas_mascotas (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica)
        VALUES (@nro_reg_municipal, 1, 1, NULL, @especie);

    IF @raza IS NOT NULL AND TRIM(@raza) <> ''
        INSERT INTO dbo.caracteristicas_mascotas (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica)
        VALUES (@nro_reg_municipal, 2, 1, NULL, @raza);
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: ins_publicacion_adopcion (RF13)
   Registra una publicación de adopción para una mascota ya registrada.
   El alta de la mascota, si hace falta, la hace antes el servicio con
   ins_mascota_refugio dentro de la misma transacción.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.ins_publicacion_adopcion
(
    @id_refugio               INT,
    @nro_reg_municipal        INT,
    @fecha_publicacion        DATE = NULL,
    @caracteristicas_mascota  VARCHAR(MAX),
    @condicion_adopcion       VARCHAR(MAX),
    @foto                     VARCHAR(255) = NULL,
    @estado_publicacion       VARCHAR(20),
    @nro_publicacion          INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.publicaciones_adopcion
        (nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, foto, estado_publicacion)
    VALUES
        (@nro_reg_municipal, @id_refugio, COALESCE(@fecha_publicacion, CAST(GETDATE() AS DATE)),
         @caracteristicas_mascota, @condicion_adopcion, @foto, @estado_publicacion);

    SET @nro_publicacion = SCOPE_IDENTITY();
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_publicacion_refugio (RF13)
   Devuelve una publicación del refugio con su estado actual, para que el
   servicio valide la transición de estado antes de actualizarla.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_publicacion_refugio
(
    @nro_publicacion INT,
    @id_refugio      INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroPublicacion    = p.nro_publicacion,
           nroRegMunicipal   = p.nro_reg_municipal,
           idRefugio         = p.id_refugio,
           estadoPublicacion = p.estado_publicacion,
           nombreMascota     = m.nombre
      FROM dbo.publicaciones_adopcion p WITH (UPDLOCK)
      JOIN dbo.mascotas m ON m.nro_reg_municipal = p.nro_reg_municipal
     WHERE p.nro_publicacion = @nro_publicacion
       AND p.id_refugio = @id_refugio;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: upd_estado_publicacion_adopcion (RF13)
   Actualiza el estado de una publicación ('Activa', 'Pausada', 'Finalizada')
   validando la pertenencia al refugio responsable. Las transiciones permitidas
   (Finalizada no se reactiva) las valida PublicacionesService.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.upd_estado_publicacion_adopcion
(
    @nro_publicacion   INT,
    @id_refugio        INT,
    @nuevo_estado      VARCHAR(20),
    @filas_afectadas   INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE dbo.publicaciones_adopcion
       SET estado_publicacion = @nuevo_estado
     WHERE nro_publicacion = @nro_publicacion
       AND id_refugio = @id_refugio;

    SET @filas_afectadas = @@ROWCOUNT;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_mascotas_disponibles_refugio (RF13)
   Devuelve las mascotas del refugio que no tienen una publicación Activa.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_mascotas_disponibles_refugio
(
    @id_refugio INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroRegMunicipal = m.nro_reg_municipal,
           nombre          = m.nombre,
           sexo            = m.sexo,
           añoNacimiento   = m.[año_nacimiento],
           edadAproximada  = (YEAR(GETDATE()) - m.[año_nacimiento]),
           especie         = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica 
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK) 
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1), 
               'Perro'
           ),
           raza            = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica 
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK) 
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2), 
               'Mestizo'
           )
      FROM dbo.mascotas m (NOLOCK)
     WHERE m.id_refugio = @id_refugio
       AND m.vive = 1
       AND NOT EXISTS (
           SELECT 1 
             FROM dbo.publicaciones_adopcion p (NOLOCK)
            WHERE p.nro_reg_municipal = m.nro_reg_municipal
              AND p.estado_publicacion = 'Activa'
       )
     ORDER BY m.nombre;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_estados_publicacion (RF13)
   Devuelve el catálogo de estados válidos para publicaciones de adopción.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_estados_publicacion
AS
BEGIN
    SET NOCOUNT ON;

    SELECT codEstado   = cod_estado,
           descripcion = descripcion
      FROM dbo.estados_publicacion (NOLOCK)
     ORDER BY cod_estado;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_especies_mascotas (RF13 / RF06)
   Devuelve el catálogo dinámico de especies disponibles desde el dominio de rasgos (cod_rasgo = 1).
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_especies_mascotas
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroValorDominio = drm.nro_valor_dominio,
           nomEspecie      = drm.nom_valor_dominio
      FROM dbo.dominio_rasgos_mascotas drm (NOLOCK)
     WHERE drm.cod_rasgo = 1
     ORDER BY drm.nro_valor_dominio;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_resumen_portal (RF16)
   Devuelve métricas públicas en vivo del Programa Municipal para el portal.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_resumen_portal
AS
BEGIN
    SET NOCOUNT ON;

    SELECT publicacionesActivas    = (SELECT COUNT(1) FROM dbo.publicaciones_adopcion (NOLOCK) WHERE estado_publicacion = 'Activa'),
           veterinariasHabilitadas = (SELECT COUNT(1) FROM dbo.veterinarias (NOLOCK) WHERE habilitada = 1),
           refugiosAdheridos       = (SELECT COUNT(1) FROM dbo.refugios (NOLOCK));
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_veterinarias_habilitadas (RF17)
   Devuelve clínicas veterinarias habilitadas con filtro opcional y paginación.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_veterinarias_habilitadas
(
    @filtro VARCHAR(128) = NULL,
    @offset INT = 0,
    @limit  INT = 20
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @filtro_limpio VARCHAR(128) = NULLIF(TRIM(@filtro), '');

    SELECT idVeterinaria      = v.id_veterinaria,
           nombre             = v.nombre,
           domicilio          = v.domicilio,
           telefono           = v.telefono,
           correo             = v.correo,
           horarios           = v.horarios,
           serviciosPrestados = v.servicios_prestados
      FROM dbo.veterinarias v (NOLOCK)
     WHERE v.habilitada = 1
       AND (@filtro_limpio IS NULL
            OR v.nombre LIKE '%' + @filtro_limpio + '%'
            OR v.domicilio LIKE '%' + @filtro_limpio + '%'
            OR v.servicios_prestados LIKE '%' + @filtro_limpio + '%')
     ORDER BY v.nombre
     OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: count_veterinarias_habilitadas (RF17)
   Cuenta las clínicas veterinarias habilitadas que coinciden con el filtro.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.count_veterinarias_habilitadas
(
    @filtro VARCHAR(128) = NULL
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @filtro_limpio VARCHAR(128) = NULLIF(TRIM(@filtro), '');

    SELECT COUNT(1)
      FROM dbo.veterinarias v (NOLOCK)
     WHERE v.habilitada = 1
       AND (@filtro_limpio IS NULL
            OR v.nombre LIKE '%' + @filtro_limpio + '%'
            OR v.domicilio LIKE '%' + @filtro_limpio + '%'
            OR v.servicios_prestados LIKE '%' + @filtro_limpio + '%');
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_refugios_publicos (RF18)
   Devuelve directorio público de refugios sin exponer datos privados de responsables.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_refugios_publicos
(
    @filtro VARCHAR(128) = NULL,
    @offset INT = 0,
    @limit  INT = 20
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @filtro_limpio VARCHAR(128) = NULLIF(TRIM(@filtro), '');

    SELECT idRefugio            = r.id_refugio,
           nombre               = COALESCE(r.nombre, r.razon_social),
           domicilio            = r.domicilio,
           telefono             = r.telefono,
           correo               = r.correo,
           horarios             = r.horarios,
           publicacionesActivas = (
               SELECT COUNT(1)
                 FROM dbo.publicaciones_adopcion p (NOLOCK)
                WHERE p.id_refugio = r.id_refugio
                  AND p.estado_publicacion = 'Activa'
           )
      FROM dbo.refugios r (NOLOCK)
     WHERE (@filtro_limpio IS NULL
            OR COALESCE(r.nombre, r.razon_social) LIKE '%' + @filtro_limpio + '%'
            OR r.domicilio LIKE '%' + @filtro_limpio + '%')
     ORDER BY COALESCE(r.nombre, r.razon_social)
     OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: count_refugios_publicos (RF18)
   Cuenta los refugios que coinciden con el filtro de búsqueda pública.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.count_refugios_publicos
(
    @filtro VARCHAR(128) = NULL
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @filtro_limpio VARCHAR(128) = NULLIF(TRIM(@filtro), '');

    SELECT COUNT(1)
      FROM dbo.refugios r (NOLOCK)
     WHERE (@filtro_limpio IS NULL
            OR COALESCE(r.nombre, r.razon_social) LIKE '%' + @filtro_limpio + '%'
            OR r.domicilio LIKE '%' + @filtro_limpio + '%');
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_publicaciones_activas_refugio (RF18 / RF13)
   Devuelve únicamente las publicaciones Activas de un refugio para consulta pública.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_publicaciones_activas_refugio
(
    @id_refugio INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroPublicacion         = p.nro_publicacion,
           nroRegMunicipal        = p.nro_reg_municipal,
           idRefugio              = p.id_refugio,
           fechaPublicacion       = p.fecha_publicacion,
           caracteristicasMascota = p.caracteristicas_mascota,
           condicionAdopcion      = p.condicion_adopcion,
           foto                   = p.foto,
           estadoPublicacion      = p.estado_publicacion,
           descripcionEstado      = ep.descripcion,
           nombreMascota          = m.nombre,
           sexo                   = m.sexo,
           añoNacimiento          = m.[año_nacimiento],
           edadAproximada         = (YEAR(GETDATE()) - m.[año_nacimiento]),
           especie                = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1),
               'Perro'
           ),
           raza                   = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2),
               'Mestizo'
           )
      FROM dbo.publicaciones_adopcion p (NOLOCK)
      JOIN dbo.mascotas m (NOLOCK)
        ON m.nro_reg_municipal = p.nro_reg_municipal
      JOIN dbo.estados_publicacion ep (NOLOCK)
        ON ep.cod_estado = p.estado_publicacion
     WHERE p.id_refugio = @id_refugio
       AND p.estado_publicacion = 'Activa'
     ORDER BY p.nro_publicacion DESC;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_mascotas_ciudadano (RF20)
   Devuelve las mascotas del ciudadano autenticado con estado sanitario general.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_mascotas_ciudadano
(
    @id_ciudadano INT,
    @offset       INT = 0,
    @limit        INT = 20
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroRegMunicipal    = m.nro_reg_municipal,
           nombre             = m.nombre,
           sexo               = m.sexo,
           añoNacimiento      = m.[año_nacimiento],
           microchip          = m.microchip,
           castrado           = m.castrado,
           vive               = m.vive,
           idResponsable      = m.id_responsable,
           idRefugio          = m.id_refugio,
           especie            = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1),
               'Perro'
           ),
           raza               = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2),
               'Mestizo'
           ),
           cantidadAtenciones = (
               SELECT COUNT(1)
                 FROM dbo.informacion_sanitaria s (NOLOCK)
                WHERE s.nro_reg_municipal = m.nro_reg_municipal
           ),
           estadoSanitario    = CASE
               WHEN NOT EXISTS (
                   SELECT 1
                     FROM dbo.informacion_sanitaria s (NOLOCK)
                    WHERE s.nro_reg_municipal = m.nro_reg_municipal
               ) THEN 'Sin registros'
               WHEN EXISTS (
                   SELECT 1
                     FROM dbo.informacion_sanitaria s (NOLOCK)
                    WHERE s.nro_reg_municipal = m.nro_reg_municipal
                      AND s.fecha_vencimiento IS NOT NULL
                      AND s.fecha_vencimiento < CAST(GETDATE() AS DATE)
               ) THEN 'Con vencimientos'
               ELSE 'Al día'
           END
      FROM dbo.mascotas m (NOLOCK)
     WHERE m.id_responsable = @id_ciudadano
       AND m.vive = 1
     ORDER BY m.nro_reg_municipal
     OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: count_mascotas_ciudadano (RF20)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.count_mascotas_ciudadano
(
    @id_ciudadano INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT COUNT(1)
      FROM dbo.mascotas m (NOLOCK)
     WHERE m.id_responsable = @id_ciudadano
       AND m.vive = 1;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: buscar_ciudadano_por_cuil (RF06)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.buscar_ciudadano_por_cuil
(
    @cuil VARCHAR(20)
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @cuil_clean VARCHAR(20) = REPLACE(REPLACE(TRIM(@cuil), '-', ''), ' ', '');

    SELECT TOP 1 c.id_ciudadano
      FROM dbo.ciudadanos c (NOLOCK)
     WHERE c.habilitado = 1
       AND (c.cuil = @cuil OR REPLACE(REPLACE(c.cuil, '-', ''), ' ', '') = @cuil_clean);
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: buscar_mascota_por_microchip (RF06)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.buscar_mascota_por_microchip
(
    @microchip VARCHAR(64)
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1
           nroRegMunicipal = m.nro_reg_municipal,
           nombre          = m.nombre,
           sexo            = m.sexo,
           añoNacimiento   = m.[año_nacimiento],
           microchip       = m.microchip,
           castrado        = m.castrado,
           vive            = m.vive,
           idResponsable   = m.id_responsable,
           idRefugio       = m.id_refugio,
           especie         = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1),
               'Perro'
           ),
           raza            = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2
           )
      FROM dbo.mascotas m (NOLOCK)
     WHERE m.vive = 1
       AND UPPER(TRIM(m.microchip)) = UPPER(TRIM(@microchip));
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: buscar_mascota_coincidencia (RF06)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.buscar_mascota_coincidencia
(
    @id_responsable INT = NULL,
    @id_refugio     INT = NULL,
    @nombre         VARCHAR(64),
    @sexo           CHAR(1),
    @año_nacimiento INT,
    @especie        VARCHAR(64)
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1
           nroRegMunicipal = m.nro_reg_municipal,
           nombre          = m.nombre,
           sexo            = m.sexo,
           añoNacimiento   = m.[año_nacimiento],
           microchip       = m.microchip,
           castrado        = m.castrado,
           vive            = m.vive,
           idResponsable   = m.id_responsable,
           idRefugio       = m.id_refugio,
           especie         = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1),
               @especie
           ),
           raza            = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2
           )
      FROM dbo.mascotas m (NOLOCK)
     WHERE m.vive = 1
       AND LOWER(TRIM(m.nombre)) = LOWER(TRIM(@nombre))
       AND UPPER(m.sexo) = UPPER(@sexo)
       AND m.[año_nacimiento] = @año_nacimiento
       AND LOWER(TRIM(COALESCE(
           (SELECT TOP 1 cm.valor_caracteristica
              FROM dbo.caracteristicas_mascotas cm (NOLOCK)
             WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1),
           ''
       ))) = LOWER(TRIM(@especie))
       AND (
           (@id_responsable IS NOT NULL AND m.id_responsable = @id_responsable)
           OR (@id_refugio IS NOT NULL AND m.id_refugio = @id_refugio)
       );
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: ins_mascota_registro_unico (RF06)
   Inserta una mascota en el Registro Único asignando NRM consecutivo seguro.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.ins_mascota_registro_unico
(
    @nombre             VARCHAR(64),
    @sexo               CHAR(1),
    @año_nacimiento     INT,
    @microchip          VARCHAR(64) = NULL,
    @castrado           BIT = 0,
    @id_responsable     INT = NULL,
    @id_refugio         INT = NULL,
    @especie            VARCHAR(64),
    @raza               VARCHAR(64) = NULL,
    @nro_reg_municipal  INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT @nro_reg_municipal = COALESCE(MAX(nro_reg_municipal), 100000) + 1
      FROM dbo.mascotas WITH (UPDLOCK, HOLDLOCK);

    INSERT INTO dbo.mascotas
        (nro_reg_municipal, nombre, sexo, [año_nacimiento], microchip, castrado, vive, id_responsable, id_refugio)
    VALUES
        (@nro_reg_municipal, @nombre, @sexo, @año_nacimiento, NULLIF(TRIM(@microchip), ''), @castrado, 1, @id_responsable, @id_refugio);

    IF @especie IS NOT NULL AND TRIM(@especie) <> ''
        INSERT INTO dbo.caracteristicas_mascotas (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica)
        VALUES (@nro_reg_municipal, 1, 1, NULL, TRIM(@especie));

    IF @raza IS NOT NULL AND TRIM(@raza) <> ''
        INSERT INTO dbo.caracteristicas_mascotas (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica)
        VALUES (@nro_reg_municipal, 2, 1, NULL, TRIM(@raza));
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_tipos_atencion_sanitaria (RF09)
   Devuelve el catálogo oficial de prácticas sanitarias cubiertas por el programa.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_tipos_atencion_sanitaria
AS
BEGIN
    SET NOCOUNT ON;

    SELECT codTipoAtencion  = t.cod_tipo_atencion,
           descTipoAtencion = t.desc_tipo_atencion
      FROM dbo.tipos_atencion_sanitaria t (NOLOCK)
     ORDER BY t.cod_tipo_atencion;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_mascota_por_nrm (RF06 / RF09 / RF10 / RF11)
   Obtiene el resumen completo de una mascota del Registro Único por NRM.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_mascota_por_nrm
(
    @nro_reg_municipal INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroRegMunicipal    = m.nro_reg_municipal,
           nombre             = m.nombre,
           sexo               = m.sexo,
           añoNacimiento      = m.[año_nacimiento],
           microchip          = m.microchip,
           castrado           = m.castrado,
           vive               = m.vive,
           idResponsable      = m.id_responsable,
           idRefugio          = m.id_refugio,
           especie            = COALESCE(
               (SELECT TOP 1 cm.valor_caracteristica
                  FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                 WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1),
               'Perro'
           ),
           raza               = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2
           ),
           cantidadAtenciones = (
               SELECT COUNT(1)
                 FROM dbo.informacion_sanitaria s (NOLOCK)
                WHERE s.nro_reg_municipal = m.nro_reg_municipal
           ),
           estadoSanitario    = CASE
               WHEN NOT EXISTS (
                   SELECT 1
                     FROM dbo.informacion_sanitaria s (NOLOCK)
                    WHERE s.nro_reg_municipal = m.nro_reg_municipal
               ) THEN 'Sin registros'
               WHEN EXISTS (
                   SELECT 1
                     FROM dbo.informacion_sanitaria s (NOLOCK)
                    WHERE s.nro_reg_municipal = m.nro_reg_municipal
                      AND s.fecha_vencimiento IS NOT NULL
                      AND s.fecha_vencimiento < CAST(GETDATE() AS DATE)
               ) THEN 'Con vencimientos'
               ELSE 'Al día'
           END
      FROM dbo.mascotas m (NOLOCK)
     WHERE m.nro_reg_municipal = @nro_reg_municipal;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_informacion_sanitaria_mascota (RF09 / RF10 / RF11)
   Lista el historial sanitario de una mascota ordenado del más reciente al más antiguo.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_informacion_sanitaria_mascota
(
    @nro_reg_municipal INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nroRegMunicipal  = s.nro_reg_municipal,
           nroRegistro      = s.nro_registro,
           fechaAtencion    = s.fecha_atencion,
           tipoAtencion     = t.desc_tipo_atencion,
           detalleAtencion  = s.detalle_atencion,
           fechaVencimiento = s.fecha_vencimiento,
           idVeterinaria    = s.id_veterinaria,
           veterinaria      = v.nombre,
           profesional      = COALESCE(
               NULLIF(TRIM(s.profesional_texto), ''),
               CASE WHEN s.id_profesional IS NOT NULL THEN CONCAT('Profesional #', s.id_profesional) END,
               v.nombre
           )
      FROM dbo.informacion_sanitaria s (NOLOCK)
      JOIN dbo.tipos_atencion_sanitaria t (NOLOCK)
        ON t.cod_tipo_atencion = s.cod_tipo_atencion
      JOIN dbo.veterinarias v (NOLOCK)
        ON v.id_veterinaria = s.id_veterinaria
     WHERE s.nro_reg_municipal = @nro_reg_municipal
     ORDER BY s.fecha_atencion DESC, s.nro_registro DESC;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: ins_informacion_sanitaria (RF09)
   Registra una práctica sanitaria de manera idempotente (si viene clave externa)
   y marca castrado=1 cuando corresponde a Esterilización o Castración.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.ins_informacion_sanitaria
(
    @nro_reg_municipal    INT,
    @fecha_atencion       DATE,
    @cod_tipo_atencion    INT,
    @detalle_atencion     VARCHAR(255),
    @fecha_vencimiento    DATE = NULL,
    @id_veterinaria       INT,
    @id_profesional       INT = NULL,
    @profesional_texto    VARCHAR(128) = NULL,
    @nro_atencion_externa INT = NULL,
    @nro_detalle_externo  INT = NULL
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @nro_registro_existente INT = NULL;

    IF @nro_atencion_externa IS NOT NULL AND @nro_detalle_externo IS NOT NULL
    BEGIN
        SELECT TOP 1 @nro_registro_existente = s.nro_registro
          FROM dbo.informacion_sanitaria s (NOLOCK)
         WHERE s.id_veterinaria = @id_veterinaria
           AND s.nro_atencion_externa = @nro_atencion_externa
           AND s.nro_detalle_externo = @nro_detalle_externo;
    END

    IF @nro_registro_existente IS NULL
    BEGIN
        DECLARE @nuevo_nro_registro INT;

        SELECT @nuevo_nro_registro = COALESCE(MAX(s.nro_registro), 0) + 1
          FROM dbo.informacion_sanitaria s WITH (UPDLOCK, HOLDLOCK)
         WHERE s.nro_reg_municipal = @nro_reg_municipal;

        INSERT INTO dbo.informacion_sanitaria
            (nro_reg_municipal, nro_registro, fecha_atencion, cod_tipo_atencion, detalle_atencion,
             fecha_vencimiento, id_veterinaria, id_profesional, profesional_texto,
             nro_atencion_externa, nro_detalle_externo)
        VALUES
            (@nro_reg_municipal, @nuevo_nro_registro, @fecha_atencion, @cod_tipo_atencion, TRIM(@detalle_atencion),
             @fecha_vencimiento, @id_veterinaria, @id_profesional, NULLIF(TRIM(@profesional_texto), ''),
             @nro_atencion_externa, @nro_detalle_externo);

        IF @cod_tipo_atencion IN (3, 4)
        BEGIN
            UPDATE dbo.mascotas
               SET castrado = 1
             WHERE nro_reg_municipal = @nro_reg_municipal;
        END

        SET @nro_registro_existente = @nuevo_nro_registro;
    END

    SELECT nroRegMunicipal  = s.nro_reg_municipal,
           nroRegistro      = s.nro_registro,
           fechaAtencion    = s.fecha_atencion,
           tipoAtencion     = t.desc_tipo_atencion,
           detalleAtencion  = s.detalle_atencion,
           fechaVencimiento = s.fecha_vencimiento,
           idVeterinaria    = s.id_veterinaria,
           veterinaria      = v.nombre,
           profesional      = COALESCE(
               NULLIF(TRIM(s.profesional_texto), ''),
               CASE WHEN s.id_profesional IS NOT NULL THEN CONCAT('Profesional #', s.id_profesional) END,
               v.nombre
           )
      FROM dbo.informacion_sanitaria s (NOLOCK)
      JOIN dbo.tipos_atencion_sanitaria t (NOLOCK)
        ON t.cod_tipo_atencion = s.cod_tipo_atencion
      JOIN dbo.veterinarias v (NOLOCK)
        ON v.id_veterinaria = s.id_veterinaria
     WHERE s.nro_reg_municipal = @nro_reg_municipal
       AND s.nro_registro = @nro_registro_existente;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: emitir_carnet_sanitario (RF10)
   Emite un carnet digital único vigente e invalida emisiones anteriores.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.emitir_carnet_sanitario
(
    @nro_reg_municipal INT,
    @codigo_validacion VARCHAR(64),
    @id_emisor         INT = NULL
)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE dbo.carnets_sanitarios
       SET vigente = 0
     WHERE nro_reg_municipal = @nro_reg_municipal
       AND vigente = 1;

    INSERT INTO dbo.carnets_sanitarios
        (nro_reg_municipal, codigo_validacion, fecha_emision, vigente, id_emisor)
    VALUES
        (@nro_reg_municipal, @codigo_validacion, CAST(GETDATE() AS DATE), 1, @id_emisor);

    SELECT nroRegMunicipal   = m.nro_reg_municipal,
           codigoValidacion  = cs.codigo_validacion,
           nombreMascota     = m.nombre,
           especie           = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1
           ),
           raza              = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2
           ),
           castrado          = m.castrado,
           microchip         = m.microchip,
           vive              = m.vive,
           responsableNombre = COALESCE(CONCAT(c.nombre, ' ', c.apellido), r.nombre, r.razon_social),
           fechaEmision      = cs.fecha_emision,
           vigente           = cs.vigente
      FROM dbo.carnets_sanitarios cs (NOLOCK)
      JOIN dbo.mascotas m (NOLOCK)
        ON m.nro_reg_municipal = cs.nro_reg_municipal
      LEFT JOIN dbo.ciudadanos c (NOLOCK)
        ON c.id_ciudadano = m.id_responsable
      LEFT JOIN dbo.refugios r (NOLOCK)
        ON r.id_refugio = m.id_refugio
     WHERE cs.codigo_validacion = @codigo_validacion;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_carnet_activo_por_nrm (RF11)
   Obtiene el carnet sanitario digital vigente para una mascota por su NRM.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_carnet_activo_por_nrm
(
    @nro_reg_municipal INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1
           nroRegMunicipal   = m.nro_reg_municipal,
           codigoValidacion  = cs.codigo_validacion,
           nombreMascota     = m.nombre,
           especie           = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1
           ),
           raza              = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2
           ),
           castrado          = m.castrado,
           microchip         = m.microchip,
           vive              = m.vive,
           responsableNombre = COALESCE(CONCAT(c.nombre, ' ', c.apellido), r.nombre, r.razon_social),
           fechaEmision      = cs.fecha_emision,
           vigente           = cs.vigente
      FROM dbo.carnets_sanitarios cs (NOLOCK)
      JOIN dbo.mascotas m (NOLOCK)
        ON m.nro_reg_municipal = cs.nro_reg_municipal
      LEFT JOIN dbo.ciudadanos c (NOLOCK)
        ON c.id_ciudadano = m.id_responsable
      LEFT JOIN dbo.refugios r (NOLOCK)
        ON r.id_refugio = m.id_refugio
     WHERE cs.nro_reg_municipal = @nro_reg_municipal
       AND cs.vigente = 1
     ORDER BY cs.id_carnet DESC;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_carnet_por_codigo (RF11)
   Consulta un carnet por su código único para validación pública o por terceros.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_carnet_por_codigo
(
    @codigo_validacion VARCHAR(64)
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1
           nroRegMunicipal   = m.nro_reg_municipal,
           codigoValidacion  = cs.codigo_validacion,
           nombreMascota     = m.nombre,
           especie           = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 1
           ),
           raza              = (
               SELECT TOP 1 cm.valor_caracteristica
                 FROM dbo.caracteristicas_mascotas cm (NOLOCK)
                WHERE cm.nro_reg_municipal = m.nro_reg_municipal AND cm.cod_rasgo = 2
           ),
           castrado          = m.castrado,
           microchip         = m.microchip,
           vive              = m.vive,
           responsableNombre = COALESCE(CONCAT(c.nombre, ' ', c.apellido), r.nombre, r.razon_social),
           fechaEmision      = cs.fecha_emision,
           vigente           = cs.vigente
      FROM dbo.carnets_sanitarios cs (NOLOCK)
      JOIN dbo.mascotas m (NOLOCK)
        ON m.nro_reg_municipal = cs.nro_reg_municipal
      LEFT JOIN dbo.ciudadanos c (NOLOCK)
        ON c.id_ciudadano = m.id_responsable
      LEFT JOIN dbo.refugios r (NOLOCK)
        ON r.id_refugio = m.id_refugio
     WHERE UPPER(TRIM(cs.codigo_validacion)) = UPPER(TRIM(@codigo_validacion));
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_veterinarias_integradas (RF06 / RF09)
   Devuelve veterinarias habilitadas con su configuración técnica REST/SOAP y
   la última fecha procesada con éxito.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_veterinarias_integradas
AS
BEGIN
    SET NOCOUNT ON;

    SELECT idVeterinaria        = v.id_veterinaria,
           nombreVeterinaria    = v.nombre,
           urlServicio          = MAX(CASE WHEN cv.cod_atributo = 1 THEN cv.valor END),
           tecnologia           = UPPER(MAX(CASE WHEN cv.cod_atributo = 2 THEN cv.valor END)),
           parametros           = MAX(CASE WHEN cv.cod_atributo = 3 THEN cv.valor END),
           apiKey               = MAX(CASE WHEN cv.cod_atributo = 4 THEN cv.valor END),
           ultimaFechaProcesada = (
               SELECT MAX(sl.fecha_hasta)
                 FROM dbo.sincronizacion_lotes_veterinaria sl (NOLOCK)
                WHERE sl.id_veterinaria = v.id_veterinaria
                  AND sl.estado = 'EXITO'
           )
      FROM dbo.veterinarias v (NOLOCK)
      JOIN dbo.configuracion_veterinarias cv (NOLOCK)
        ON cv.id_veterinaria = v.id_veterinaria
     WHERE v.habilitada = 1
     GROUP BY v.id_veterinaria, v.nombre
     ORDER BY v.id_veterinaria;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_mapeo_mascota_veterinaria (RF06 / RF09)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_mapeo_mascota_veterinaria
(
    @id_veterinaria     INT,
    @id_mascota_externa VARCHAR(64)
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1 nro_reg_municipal
      FROM dbo.sincronizacion_mascotas_veterinaria (NOLOCK)
     WHERE id_veterinaria = @id_veterinaria
       AND id_mascota_externa = TRIM(@id_mascota_externa);
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: upsert_mapeo_mascota_veterinaria (RF06)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.upsert_mapeo_mascota_veterinaria
(
    @id_veterinaria     INT,
    @id_mascota_externa VARCHAR(64),
    @nro_reg_municipal  INT,
    @nrm_confirmado     BIT = 0
)
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
          FROM dbo.sincronizacion_mascotas_veterinaria (NOLOCK)
         WHERE id_veterinaria = @id_veterinaria
           AND id_mascota_externa = TRIM(@id_mascota_externa)
    )
    BEGIN
        UPDATE dbo.sincronizacion_mascotas_veterinaria
           SET nro_reg_municipal   = @nro_reg_municipal,
               nrm_confirmado      = CASE WHEN @nrm_confirmado = 1 THEN 1 ELSE nrm_confirmado END,
               fecha_sincronizacion = SYSDATETIME()
         WHERE id_veterinaria = @id_veterinaria
           AND id_mascota_externa = TRIM(@id_mascota_externa);
    END
    ELSE
    BEGIN
        INSERT INTO dbo.sincronizacion_mascotas_veterinaria
            (id_veterinaria, id_mascota_externa, nro_reg_municipal, nrm_confirmado, fecha_sincronizacion)
        VALUES
            (@id_veterinaria, TRIM(@id_mascota_externa), @nro_reg_municipal, @nrm_confirmado, SYSDATETIME());
    END
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_nrms_pendientes_veterinaria (RF06)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_nrms_pendientes_veterinaria
(
    @id_veterinaria INT
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT idMascotaExterna = s.id_mascota_externa,
           nroRegMunicipal  = s.nro_reg_municipal
      FROM dbo.sincronizacion_mascotas_veterinaria s (NOLOCK)
     WHERE s.id_veterinaria = @id_veterinaria
       AND s.nrm_confirmado = 0
     ORDER BY s.fecha_sincronizacion, s.id_mascota_externa;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: marcar_nrm_confirmado_veterinaria (RF06)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.marcar_nrm_confirmado_veterinaria
(
    @id_veterinaria     INT,
    @id_mascota_externa VARCHAR(64)
)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE dbo.sincronizacion_mascotas_veterinaria
       SET nrm_confirmado       = 1,
           fecha_sincronizacion = SYSDATETIME()
     WHERE id_veterinaria = @id_veterinaria
       AND id_mascota_externa = TRIM(@id_mascota_externa);
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: ins_lote_veterinaria (RF06 / RF09 / RNF06)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.ins_lote_veterinaria
(
    @id_veterinaria         INT,
    @tipo_lote              VARCHAR(32),
    @fecha_desde            DATE,
    @fecha_hasta            DATE,
    @estado                 VARCHAR(20),
    @registros_procesados   INT,
    @registros_nuevos       INT,
    @registros_deduplicados INT,
    @registros_confirmados  INT,
    @detalle_error          VARCHAR(500) = NULL
)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.sincronizacion_lotes_veterinaria
        (id_veterinaria, tipo_lote, fecha_desde, fecha_hasta, fecha_ejecucion, estado,
         registros_procesados, registros_nuevos, registros_deduplicados, registros_confirmados, detalle_error)
    VALUES
        (@id_veterinaria, @tipo_lote, @fecha_desde, @fecha_hasta, SYSDATETIME(), @estado,
         @registros_procesados, @registros_nuevos, @registros_deduplicados, @registros_confirmados, @detalle_error);
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: get_ultimos_lotes_veterinaria (RF06 / RF09)
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.get_ultimos_lotes_veterinaria
(
    @top INT = 20
)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP (@top)
           idLote                = sl.id_lote,
           idVeterinaria         = sl.id_veterinaria,
           nombreVeterinaria     = v.nombre,
           tipoLote              = sl.tipo_lote,
           fechaDesde            = sl.fecha_desde,
           fechaHasta            = sl.fecha_hasta,
           fechaEjecucion        = sl.fecha_ejecucion,
           estado                = sl.estado,
           registrosProcesados   = sl.registros_procesados,
           registrosNuevos       = sl.registros_nuevos,
           registrosDeduplicados = sl.registros_deduplicados,
           registrosConfirmados  = sl.registros_confirmados,
           detalleError          = sl.detalle_error
      FROM dbo.sincronizacion_lotes_veterinaria sl (NOLOCK)
      JOIN dbo.veterinarias v (NOLOCK)
        ON v.id_veterinaria = sl.id_veterinaria
     ORDER BY sl.id_lote DESC;
END
GO

