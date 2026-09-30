-- =============================================================================
-- Plataforma Mascotas Córdoba - Programa Municipal de Bienestar Animal
-- Script DDL de Creación de Base de Datos y Tablas (Municipalidad)
-- =============================================================================

DROP DATABASE IF EXISTS [das-mascotas-cordoba];
CREATE DATABASE [das-mascotas-cordoba];
USE [das-mascotas-cordoba];

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
    habilitado BIT NOT NULL DEFAULT 1,
    CONSTRAINT pk_ciudadanos PRIMARY KEY (id_ciudadano),
    CONSTRAINT uq_ciudadanos_cuil UNIQUE (cuil)
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
    CONSTRAINT pk_veterinarias PRIMARY KEY (id_veterinaria)
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
    id_profesional INT NOT NULL,
    CONSTRAINT pk_informacion_sanitaria PRIMARY KEY (nro_reg_municipal, nro_registro),
    CONSTRAINT fk_info_sanitaria_mascotas FOREIGN KEY (nro_reg_municipal)
        REFERENCES mascotas(nro_reg_municipal)
        ON DELETE CASCADE,
    CONSTRAINT fk_info_sanitaria_tipos FOREIGN KEY (cod_tipo_atencion)
        REFERENCES tipos_atencion_sanitaria(cod_tipo_atencion),
    CONSTRAINT fk_info_sanitaria_prof_vet FOREIGN KEY (id_veterinaria, id_profesional)
        REFERENCES profesionales_veterinarias(id_veterinaria, id_profesional)
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
    CONSTRAINT chk_publicaciones_estado CHECK (estado_publicacion IN ('Activa', 'Pausada', 'Finalizada')),
    CONSTRAINT fk_pub_adopcion_mascotas FOREIGN KEY (nro_reg_municipal)
        REFERENCES mascotas(nro_reg_municipal)
        ON DELETE CASCADE,
    CONSTRAINT fk_pub_adopcion_refugios FOREIGN KEY (id_refugio)
        REFERENCES refugios(id_refugio)
);

-- =============================================================================
-- DATOS SEMILLA / INICIALES (Catálogos, Entidades y Tablas Intermedias)
-- =============================================================================

-- 1. Ciudadanos (Propietarios y Responsables de Refugios para CiDi)
SET IDENTITY_INSERT ciudadanos ON;
INSERT INTO ciudadanos (id_ciudadano, apellido, nombre, cuil, clave, correo, telefono, domicilio, habilitado) VALUES
(1, 'Pérez', 'Juan Carlos', '20-28555666-3', 'pass123', 'juan.perez@gmail.com', '3514567890', 'San Jerónimo 450, Córdoba', 1),
(2, 'Gómez', 'María Belén', '27-34777888-4', 'pass123', 'maria.gomez@gmail.com', '3515678901', 'Av. General Paz 1230, Córdoba', 1),
(3, 'Sánchez', 'Roberto', '20-25999000-5', 'pass123', 'roberto.sanchez@patitasfelices.org', '3516789012', 'Camino a 60 Cuadras Km 8, Córdoba', 1),
(4, 'Morales', 'Ana Carolina', '27-38123456-9', 'pass123', 'ana.morales@hogaranimal.org', '3517890123', 'Bv. San Juan 890, Córdoba', 1);
SET IDENTITY_INSERT ciudadanos OFF;

-- 2. Veterinarias Adheridas (RF17)
SET IDENTITY_INSERT veterinarias ON;
INSERT INTO veterinarias (id_veterinaria, nombre, razon_social, correo, telefono, domicilio, habilitacion_municipal, habilitada, horarios, servicios_prestados) VALUES
(1, 'San Antonio Vet', 'Veterinaria San Antonio S.R.L.', 'sanantonio@gmail.com', '543423431', 'Roca 221, Villa Carlos Paz, Córdoba', 'HAB-MUNI-00123', 1, '13:00 - 22:00', 'Castración, Desparasitación, Vacunación, Guardia 24hs'),
(2, 'Veterinaria Central Córdoba', 'Centro Veterinario Cba S.A.', 'contacto@vetcentralcba.com', '3514223344', 'Av. Colón 750, Córdoba', 'HAB-MUNI-00456', 1, '08:00 - 20:00', 'Vacunación, Esterilización, Consulta clínica'),
(3, 'Clínica Veterinaria Alta Córdoba', 'Alta Córdoba Mascotas S.A.', 'info@vetaltacba.com', '3514712000', 'Mariano Fragueiro 1950, Córdoba', 'HAB-MUNI-00789', 1, '09:00 - 19:00', 'Vacunación, Castración, Cirugía general');
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
INSERT INTO mascotas (nro_reg_municipal, nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio) VALUES
(100001, 'Firulais', 'M', 2021, '981098104523123', 1, 1, NULL),
(100002, 'Milo', 'M', 2022, '981098107845612', 1, 2, NULL),
(100003, 'Luna', 'H', 2023, '981098109923841', 1, 1, NULL),
(100004, 'Rocco', 'M', 2023, '981098101112233', 1, NULL, 1),
(100005, 'Mía', 'H', 2024, NULL, 1, NULL, 1);

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
(100005, 5, 1, 1, 'Pequeño');

-- 11. Tipos de Prácticas Sanitarias
INSERT INTO tipos_atencion_sanitaria (cod_tipo_atencion, desc_tipo_atencion) VALUES
(1, 'Vacunación'),
(2, 'Desparasitación'),
(3, 'Esterilización'),
(4, 'Castración'),
(5, 'Consulta clínica');

-- 12. Información Sanitaria (Tabla Intermedia: Mascotas <-> Prácticas <-> Veterinarias/Profesionales)
INSERT INTO informacion_sanitaria (nro_reg_municipal, nro_registro, fecha_atencion, cod_tipo_atencion, detalle_atencion, fecha_vencimiento, id_veterinaria, id_profesional) VALUES
(100001, 1, '2024-03-10', 1, 'Vacuna Antirrábica Obligatoria Anual - Lote RAB-2024-X9', '2025-03-10', 1, 1),
(100001, 2, '2024-09-15', 2, 'Desparasitación interna de amplio espectro - Lote DESP-552', '2025-03-15', 1, 2),
(100002, 1, '2024-05-20', 4, 'Castración electiva bajo Programa Municipal de Esterilización', NULL, 1, 1),
(100003, 1, '2024-04-12', 1, 'Vacunación Triple Felina Preventiva - Lote FEL-3312', '2025-04-12', 1, 2),
(100004, 1, '2024-02-18', 1, 'Vacunación quíntuple canina y antirrábica de ingreso al refugio', '2025-02-18', 2, 3),
(100004, 2, '2024-03-01', 4, 'Castración quirúrgica previa a habilitación de adopción', NULL, 2, 3);

-- 13. Publicaciones de Adopción (Tabla Intermedia: Mascotas <-> Refugios)
SET IDENTITY_INSERT publicaciones_adopcion ON;
INSERT INTO publicaciones_adopcion (nro_publicacion, nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, foto, estado_publicacion) VALUES
(1, 100004, 1, '2024-03-05', 'Rocco es un perro macho mestizo de 1 año y medio, muy enérgico, cariñoso, castrado y con plan sanitario al día. Se lleva excelente con otros perros y niños.', 'Hogar con patio cerrado, compromiso de seguimiento y paseos diarios. Firma de acta de adopción responsable.', 'https://mascotas.cordoba.gob.ar/uploads/adopciones/rocco_100004.jpg', 'Activa'),
(2, 100005, 1, '2024-04-01', 'Mía es una gatita de 6 meses, juguetona y muy dulce, desparasitada y con primera dosis de vacunas. Lista para integrarse a una familia.', 'Depto o casa con protecciones en balcones/ventanas. Compromiso de castración al cumplir la edad adecuada.', 'https://mascotas.cordoba.gob.ar/uploads/adopciones/mia_100005.jpg', 'Activa');
SET IDENTITY_INSERT publicaciones_adopcion OFF;
GO

-- =============================================================================
-- PROCEDIMIENTOS ALMACENADOS (Stored Procedures)
-- =============================================================================

/* -----------------------------------------------------------------------------
   Procedimiento: autenticar_usuario_refugio (RF15)
   Autentica a un usuario por CUIL y clave, validando que sea el responsable
   de un refugio habilitado en el programa.
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.autenticar_usuario_refugio
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
           perfil                = 'REFUGIO',
           idRefugio             = r.id_refugio,
           nombreRefugio         = COALESCE(r.nombre, r.razon_social),
           razonSocialRefugio    = r.razon_social,
           habilitacionMunicipal = r.habilitacion_municipal
      FROM dbo.ciudadanos c (NOLOCK)
      JOIN dbo.refugios r (NOLOCK)
        ON r.id_responsable = c.id_ciudadano
     WHERE (c.cuil = @cuil OR REPLACE(REPLACE(c.cuil, '-', ''), ' ', '') = @cuil_clean)
       AND c.clave = @clave
       AND c.habilitado = 1;
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
     WHERE p.id_refugio = @id_refugio
     ORDER BY p.nro_publicacion DESC;
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: ins_publicacion_adopcion (RF13 / RF06)
   Registra una nueva publicación de adopción. Si la mascota no está registrada,
   crea previamente el registro en dbo.mascotas y dbo.caracteristicas_mascotas (RF06).
----------------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE dbo.ins_publicacion_adopcion
(
    @id_refugio               INT,
    @nro_reg_municipal        INT = NULL,
    @nombre_mascota           VARCHAR(64) = NULL,
    @sexo                     CHAR(1) = 'M',
    @año_nacimiento           INT = NULL,
    @especie                  VARCHAR(64) = NULL,
    @raza                     VARCHAR(64) = NULL,
    @fecha_publicacion        DATE = NULL,
    @caracteristicas_mascota  VARCHAR(MAX),
    @condicion_adopcion       VARCHAR(MAX),
    @foto                     VARCHAR(255) = NULL,
    @estado_publicacion       VARCHAR(20) = 'Activa',
    @nro_publicacion          INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @nrm INT = @nro_reg_municipal;

    -- Si la mascota no está previamente registrada en el sistema municipal,
    -- realizamos el alta en el Registro Único asociada al refugio (RF06)
    IF @nrm IS NULL OR @nrm <= 0
    BEGIN
        SELECT @nrm = COALESCE(MAX(nro_reg_municipal), 100000) + 1 FROM dbo.mascotas;

        IF @año_nacimiento IS NULL
            SET @año_nacimiento = YEAR(GETDATE());

        INSERT INTO dbo.mascotas (nro_reg_municipal, nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (@nrm, COALESCE(@nombre_mascota, 'Sin Nombre'), @sexo, @año_nacimiento, NULL, 1, NULL, @id_refugio);

        -- Registrar Especie (cod_rasgo = 1)
        IF @especie IS NOT NULL AND TRIM(@especie) <> ''
        BEGIN
            INSERT INTO dbo.caracteristicas_mascotas (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica)
            VALUES (@nrm, 1, 1, NULL, @especie);
        END

        -- Registrar Raza (cod_rasgo = 2)
        IF @raza IS NOT NULL AND TRIM(@raza) <> ''
        BEGIN
            INSERT INTO dbo.caracteristicas_mascotas (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica)
            VALUES (@nrm, 2, 1, NULL, @raza);
        END
    END

    IF @fecha_publicacion IS NULL
        SET @fecha_publicacion = CAST(GETDATE() AS DATE);

    INSERT INTO dbo.publicaciones_adopcion 
        (nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, foto, estado_publicacion)
    VALUES 
        (@nrm, @id_refugio, @fecha_publicacion, @caracteristicas_mascota, @condicion_adopcion, @foto, @estado_publicacion);

    SET @nro_publicacion = SCOPE_IDENTITY();
END
GO

/* -----------------------------------------------------------------------------
   Procedimiento: upd_estado_publicacion_adopcion (RF13)
   Actualiza el estado de una publicación ('Activa', 'Pausada', 'Finalizada')
   validando la pertenencia al refugio responsable.
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

