DROP DATABASE IF EXISTS [das-mascotas-veterinaria];
CREATE DATABASE [das-mascotas-veterinaria];
USE [das-mascotas-veterinaria];

CREATE TABLE especies (
    cod_especie INT NOT NULL,
    desc_especie TEXT NOT NULL,
    CONSTRAINT pk_cod_especie PRIMARY KEY (cod_especie)
);

CREATE TABLE razas (
    cod_especie INT NOT NULL,
    id_raza INT NOT NULL,
    nom_raza VARCHAR(64) NOT NULL,
    CONSTRAINT fk_razas_especies FOREIGN KEY (cod_especie)
        REFERENCES especies(cod_especie)
        ON DELETE CASCADE,
    CONSTRAINT pk_razas PRIMARY KEY (cod_especie, id_raza)
);

CREATE TABLE mascotas (
    id_mascota INT NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    sexo CHAR(1) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    color VARCHAR(32) NOT NULL,
    pelaje VARCHAR(32) NOT NULL,
    microchip VARCHAR(64) NOT NULL,
    vive BIT NOT NULL DEFAULT 1, 
    nro_reg_municipal INT UNIQUE NULL,
    cod_especie INT NOT NULL,
    id_raza INT NOT NULL,
    CONSTRAINT pk_mascotas PRIMARY KEY (id_mascota),
    CONSTRAINT chk_sexo CHECK (sexo IN ('m', 'h')),
    CONSTRAINT chk_vive CHECK (vive IN (1, 0)),
    CONSTRAINT fk_mascotas_razas FOREIGN KEY (cod_especie, id_raza)
        REFERENCES razas(cod_especie, id_raza)
        ON DELETE CASCADE
);

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

CREATE TABLE responsables_mascotas (
    id_mascota INT NOT NULL,
    id_persona INT NOT NULL,
    fecha_desde DATE NOT NULL,
    fecha_hasta DATE NOT NULL,
    observaciones TEXT NULL,
    principal VARCHAR(64) NULL,
    CONSTRAINT pk_responsables_mascotas PRIMARY KEY (id_mascota, id_persona),
    CONSTRAINT fk_responsables_mascotas__mascotas FOREIGN KEY (id_mascota)
        REFERENCES mascotas(id_mascota)
        ON DELETE CASCADE,
    CONSTRAINT fk_responsables_mascotas__personas FOREIGN KEY (id_persona)
        REFERENCES personas(id_persona)
        ON DELETE CASCADE
);

CREATE TABLE 