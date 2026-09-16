-- =============================================================================
-- Plataforma Mascotas Córdoba - Programa Municipal de Bienestar Animal
-- Script DDL de Creación de Base de Datos y Tablas (Veterinaria)
-- =============================================================================

DROP DATABASE IF EXISTS [das-mascotas-veterinaria];
CREATE DATABASE [das-mascotas-veterinaria];
USE [das-mascotas-veterinaria];

-- =============================================================================
-- TABLA: especies
-- =============================================================================
CREATE TABLE especies (
    cod_especie INT NOT NULL,
    desc_especie VARCHAR(128) NOT NULL,
    CONSTRAINT pk_especies PRIMARY KEY (cod_especie)
);

-- =============================================================================
-- TABLA: razas
-- =============================================================================
CREATE TABLE razas (
    cod_especie INT NOT NULL,
    id_raza INT NOT NULL,
    nom_raza VARCHAR(64) NOT NULL,
    CONSTRAINT pk_razas PRIMARY KEY (cod_especie, id_raza),
    CONSTRAINT fk_razas_especies FOREIGN KEY (cod_especie)
        REFERENCES especies(cod_especie)
        ON DELETE CASCADE
);

-- =============================================================================
-- TABLA: mascotas
-- =============================================================================
CREATE TABLE mascotas (
    id_mascota INT NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    sexo CHAR(1) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    color VARCHAR(32) NOT NULL,
    pelaje VARCHAR(32) NOT NULL,
    microchip VARCHAR(64) NULL,
    vive BIT NOT NULL DEFAULT 1, 
    nro_reg_municipal INT UNIQUE NULL,
    cod_especie INT NOT NULL,
    id_raza INT NOT NULL,
    CONSTRAINT pk_mascotas PRIMARY KEY (id_mascota),
    CONSTRAINT chk_mascotas_sexo CHECK (sexo IN ('m', 'h', 'M', 'H')),
    CONSTRAINT chk_mascotas_vive CHECK (vive IN (1, 0)),
    CONSTRAINT fk_mascotas_razas FOREIGN KEY (cod_especie, id_raza)
        REFERENCES razas(cod_especie, id_raza)
        ON DELETE CASCADE
);

-- =============================================================================
-- TABLA: personas
-- =============================================================================
CREATE TABLE personas (
    id_persona INT NOT NULL,
    apellido VARCHAR(64) NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    tipo_documento VARCHAR(32) NOT NULL,
    nro_documento VARCHAR(32) NOT NULL,
    correo VARCHAR(64) NOT NULL,
    telefono VARCHAR(32) NOT NULL,
    domicilio VARCHAR(128) NOT NULL,
    CONSTRAINT pk_personas PRIMARY KEY (id_persona)
);

-- =============================================================================
-- TABLA: responsables_mascotas
-- =============================================================================
CREATE TABLE responsables_mascotas (
    id_mascota INT NOT NULL,
    id_persona INT NOT NULL,
    fecha_desde DATE NOT NULL,
    fecha_hasta DATE NULL,
    observaciones VARCHAR(MAX) NULL,
    principal VARCHAR(64) NULL,
    CONSTRAINT pk_responsables_mascotas PRIMARY KEY (id_mascota, id_persona),
    CONSTRAINT fk_responsables_mascotas__mascotas FOREIGN KEY (id_mascota)
        REFERENCES mascotas(id_mascota)
        ON DELETE CASCADE,
    CONSTRAINT fk_responsables_mascotas__personas FOREIGN KEY (id_persona)
        REFERENCES personas(id_persona)
        ON DELETE CASCADE
);

-- =============================================================================
-- TABLA: profesionales
-- =============================================================================
CREATE TABLE profesionales (
    id_persona INT NOT NULL,
    matricula_profesional VARCHAR(32) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    CONSTRAINT pk_profesionales PRIMARY KEY (id_persona),
    CONSTRAINT fk_profesionales_personas FOREIGN KEY (id_persona)
        REFERENCES personas(id_persona)
        ON DELETE CASCADE
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
-- TABLA: vacunas
-- =============================================================================
CREATE TABLE vacunas (
    cod_vacuna INT NOT NULL,
    nom_vacuna VARCHAR(64) NOT NULL,
    desc_vacuna VARCHAR(255) NULL,
    CONSTRAINT pk_vacunas PRIMARY KEY (cod_vacuna)
);

-- =============================================================================
-- TABLA: atencion_sanitaria
-- =============================================================================
CREATE TABLE atencion_sanitaria (
    id_mascota INT NOT NULL,
    nro_atencion INT NOT NULL,
    id_profesional INT NOT NULL,
    fecha_atencion DATE NOT NULL,
    fecha_prox_atencion DATE NULL,
    observ_atencion VARCHAR(MAX) NULL,
    CONSTRAINT pk_atencion_sanitaria PRIMARY KEY (id_mascota, nro_atencion),
    CONSTRAINT fk_atencion_sanitaria_mascotas FOREIGN KEY (id_mascota)
        REFERENCES mascotas(id_mascota)
        ON DELETE CASCADE,
    CONSTRAINT fk_atencion_sanitaria_profesionales FOREIGN KEY (id_profesional)
        REFERENCES profesionales(id_persona)
);

-- =============================================================================
-- TABLA: detalle_atencion_sanitaria
-- =============================================================================
CREATE TABLE detalle_atencion_sanitaria (
    id_mascota INT NOT NULL,
    nro_atencion INT NOT NULL,
    cod_tipo_atencion INT NOT NULL,
    nro_detalle INT NOT NULL,
    cod_vacuna INT NULL,
    lote_vacuna VARCHAR(64) NULL,
    fecha_prox_vacunacion DATE NULL,
    indicaciones VARCHAR(MAX) NULL,
    intervencion VARCHAR(255) NULL,
    CONSTRAINT pk_detalle_atencion_sanitaria PRIMARY KEY (id_mascota, nro_atencion, cod_tipo_atencion, nro_detalle),
    CONSTRAINT fk_detalle_atencion_atencion FOREIGN KEY (id_mascota, nro_atencion)
        REFERENCES atencion_sanitaria(id_mascota, nro_atencion)
        ON DELETE CASCADE,
    CONSTRAINT fk_detalle_atencion_tipos FOREIGN KEY (cod_tipo_atencion)
        REFERENCES tipos_atencion_sanitaria(cod_tipo_atencion),
    CONSTRAINT fk_detalle_atencion_vacunas FOREIGN KEY (cod_vacuna)
        REFERENCES vacunas(cod_vacuna)
);

-- =============================================================================
-- TABLA: configuracion_sistema
-- =============================================================================
CREATE TABLE configuracion_sistema (
    clave_primaria VARCHAR(64) NOT NULL,
    clave_secundaria VARCHAR(64) NOT NULL,
    desc_clave VARCHAR(255) NOT NULL,
    tipo_dato VARCHAR(32) NOT NULL,
    valor VARCHAR(255) NOT NULL,
    CONSTRAINT pk_configuracion_sistema PRIMARY KEY (clave_primaria, clave_secundaria)
);

-- =============================================================================
-- DATOS SEMILLA / INICIALES (Catálogos del Sistema de Veterinaria)
-- Basados en los ejemplos del modelo lógico (02-Modelo-logico-veterinaria.pdf)
-- =============================================================================

-- 1. Especies
INSERT INTO especies (cod_especie, desc_especie) VALUES
(1, 'Perro'),
(2, 'Gato'),
(3, 'Conejo');

-- 2. Razas
INSERT INTO razas (cod_especie, id_raza, nom_raza) VALUES
(1, 1, 'Border Collie'),
(1, 2, 'Labrador'),
(1, 3, 'Mestizo'),
(2, 1, 'Siamés'),
(2, 2, 'Mestizo');

-- 3. Tipos de Atención Sanitaria
INSERT INTO tipos_atencion_sanitaria (cod_tipo_atencion, desc_tipo_atencion) VALUES
(1, 'Consulta clínica'),
(2, 'Control / seguimiento'),
(3, 'Vacunación'),
(4, 'Desparasitación'),
(5, 'Esterilización'),
(6, 'Castración'),
(7, 'Cirugía');

-- 4. Vacunas
INSERT INTO vacunas (cod_vacuna, nom_vacuna, desc_vacuna) VALUES
(1, 'Antirrábica', 'Vacuna contra el virus de la rabia'),
(2, 'Séxtuple Canina', 'Protección contra moquillo, parvovirus, hepatitis, etc.'),
(3, 'Triple Felina', 'Protección contra panleucopenia, rinotraqueítis y calicivirus');

-- 5. Personas (Veterinarios y Propietarios)
INSERT INTO personas (id_persona, apellido, nombre, tipo_documento, nro_documento, correo, telefono, domicilio) VALUES
(1, 'Quito', 'Esteban', 'DNI', '30111222', 'esteban.quito@vet.com', '3514001122', 'Av. San Martín 120, Villa Carlos Paz'),
(2, 'Martínez', 'Laura', 'DNI', '32333444', 'laura.martinez@vet.com', '3514003344', 'Av. Libertad 450, Villa Carlos Paz'),
(3, 'Pérez', 'Juan Carlos', 'DNI', '28555666', 'juan.perez@gmail.com', '3514567890', 'San Jerónimo 450, Córdoba'),
(4, 'Gómez', 'María Belén', 'DNI', '34777888', 'maria.gomez@gmail.com', '3515678901', 'Av. General Paz 1230, Córdoba'),
(5, 'Romero', 'Carlos Alberto', 'DNI', '25999000', 'carlos.romero@gmail.com', '3516789123', 'Mariano Moreno 78, Córdoba');

-- 6. Profesionales (Especialización de personas)
INSERT INTO profesionales (id_persona, matricula_profesional, fecha_inicio, fecha_fin) VALUES
(1, 'MP-4821', '2015-03-01', NULL),
(2, 'MP-5930', '2019-06-15', NULL);

-- 7. Mascotas (algunas sincronizadas con el NRM de la Municipalidad)
INSERT INTO mascotas (id_mascota, nombre, sexo, fecha_nacimiento, color, pelaje, microchip, vive, nro_reg_municipal, cod_especie, id_raza) VALUES
(1, 'Firulais', 'M', '2021-04-10', 'Dorado', 'Largo', '981098104523123', 1, 100001, 1, 1),
(2, 'Milo', 'M', '2022-08-20', 'Negro y blanco', 'Corto', '981098107845612', 1, 100002, 1, 3),
(3, 'Luna', 'H', '2023-01-15', 'Crema', 'Corto', '981098109923841', 1, 100003, 2, 1),
(4, 'Toby', 'M', '2020-11-05', 'Marrón', 'Medio', NULL, 1, NULL, 1, 2);

-- 8. Responsables de Mascotas (Tabla intermedia: relación Muchos a Muchos entre Personas y Mascotas)
INSERT INTO responsables_mascotas (id_mascota, id_persona, fecha_desde, fecha_hasta, observaciones, principal) VALUES
(1, 3, '2021-06-01', NULL, 'Propietario responsable desde cachorro', 'SI'),
(2, 4, '2022-10-15', NULL, 'Adoptado formalmente en jornada comunitaria', 'SI'),
(3, 3, '2023-03-01', NULL, 'Segunda mascota del mismo grupo familiar', 'SI'),
(4, 5, '2021-01-10', NULL, 'Atención clínica veterinaria regular', 'SI');

-- 9. Atención Sanitaria (Historial de consultas e intervenciones)
INSERT INTO atencion_sanitaria (id_mascota, nro_atencion, id_profesional, fecha_atencion, fecha_prox_atencion, observ_atencion) VALUES
(1, 1, 1, '2024-03-10', '2025-03-10', 'Chequeo anual completo y plan de vacunación antirrábica.'),
(1, 2, 2, '2024-09-15', '2025-03-15', 'Control semestral y administración de desparasitante.'),
(2, 1, 1, '2024-05-20', NULL, 'Cirugía programada: Castración bajo anestesia general. Recuperación exitosa.'),
(3, 1, 2, '2024-04-12', '2025-04-12', 'Vacunación anual Triple Felina.');

-- 10. Detalle de Atención Sanitaria (Tabla intermedia / detalle entre Atención, Tipos de Atención y Vacunas)
INSERT INTO detalle_atencion_sanitaria (id_mascota, nro_atencion, cod_tipo_atencion, nro_detalle, cod_vacuna, lote_vacuna, fecha_prox_vacunacion, indicaciones, intervencion) VALUES
(1, 1, 3, 1, 1, 'RAB-2024-X9', '2025-03-10', 'Reposo y no bañar por 48 horas', 'Inmunización antirrábica anual'),
(1, 1, 2, 2, NULL, NULL, NULL, 'Alimentación balanceada acorde al peso y edad', 'Examen físico general y control de peso'),
(1, 2, 4, 1, NULL, 'DESP-552', '2025-03-15', 'Comprimido palatable monodosis', 'Desparasitación interna y externa de amplio espectro'),
(2, 1, 6, 1, NULL, NULL, NULL, 'Collar isabelino 10 días, curaciones locales con iodopovidona', 'Orquiectomía electiva'),
(3, 1, 3, 1, 3, 'FEL-3312', '2025-04-12', 'Monitoreo post-vacunal habitual', 'Vacunación preventiva felina');

-- 11. Configuración del Sistema (Parámetros técnicos y de integración de la Veterinaria)
INSERT INTO configuracion_sistema (clave_primaria, clave_secundaria, desc_clave, tipo_dato, valor) VALUES
('SISTEMA', 'NOMBRE_ESTABLECIMIENTO', 'Nombre de fantasía de la veterinaria', 'STRING', 'Veterinaria San Antonio'),
('SISTEMA', 'DIRECTOR_TECNICO', 'Matrícula del profesional a cargo', 'STRING', 'MP-4821'),
('MUNICIPALIDAD', 'URL_SERVICIO_CENTRAL', 'Endpoint de la API Central de Mascotas Córdoba', 'STRING', 'http://localhost:8080/api/municipalidad'),
('MUNICIPALIDAD', 'API_KEY', 'Credencial de autenticación emitida por la Municipalidad', 'STRING', 'UBPU5i0Kq7Cne3p3mzdrwEpBj7XyQsaiXq6');