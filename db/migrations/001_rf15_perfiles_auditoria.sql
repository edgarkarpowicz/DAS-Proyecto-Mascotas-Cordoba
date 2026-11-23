-- Actualiza bases anteriores a RF15 con JWT sin borrar usuarios ni publicaciones.
USE [das-mascotas-cordoba];
GO
SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF OBJECT_ID(N'dbo.perfiles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.perfiles (
        cod_perfil VARCHAR(30) NOT NULL,
        descripcion VARCHAR(128) NOT NULL,
        CONSTRAINT pk_perfiles PRIMARY KEY (cod_perfil)
    );
END;

INSERT INTO dbo.perfiles (cod_perfil, descripcion)
SELECT p.cod_perfil, p.descripcion
FROM (VALUES
    ('CIUDADANO', 'Ciudadano propietario de mascotas o interesado en adopción'),
    ('REFUGIO', 'Usuario autorizado perteneciente a un refugio adherido'),
    ('VETERINARIA', 'Usuario autorizado perteneciente a una veterinaria adherida'),
    ('MUNICIPALIDAD', 'Personal autorizado de la Municipalidad de Córdoba')
) AS p(cod_perfil, descripcion)
WHERE NOT EXISTS (SELECT 1 FROM dbo.perfiles existente WHERE existente.cod_perfil = p.cod_perfil);

IF COL_LENGTH(N'dbo.ciudadanos', N'cod_perfil') IS NULL
BEGIN
    ALTER TABLE dbo.ciudadanos ADD cod_perfil VARCHAR(30) NOT NULL
        CONSTRAINT df_ciudadanos_cod_perfil DEFAULT 'CIUDADANO' WITH VALUES;
    -- Conserva la pertenencia a refugios de los usuarios que ya existian.
    EXEC(N'UPDATE c SET cod_perfil = ''REFUGIO''
           FROM dbo.ciudadanos c
           WHERE EXISTS (SELECT 1 FROM dbo.refugios r WHERE r.id_responsable = c.id_ciudadano);');
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'fk_ciudadanos_perfiles')
    ALTER TABLE dbo.ciudadanos ADD CONSTRAINT fk_ciudadanos_perfiles
        FOREIGN KEY (cod_perfil) REFERENCES dbo.perfiles(cod_perfil);

IF COL_LENGTH(N'dbo.veterinarias', N'id_responsable') IS NULL
    ALTER TABLE dbo.veterinarias ADD id_responsable INT NULL;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'fk_veterinarias_ciudadanos')
    ALTER TABLE dbo.veterinarias ADD CONSTRAINT fk_veterinarias_ciudadanos
        FOREIGN KEY (id_responsable) REFERENCES dbo.ciudadanos(id_ciudadano);

IF OBJECT_ID(N'dbo.auditoria_operaciones', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.auditoria_operaciones (
        id_auditoria BIGINT IDENTITY(1,1) NOT NULL,
        fecha_hora DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        id_ciudadano INT NULL,
        cuil VARCHAR(20) NULL,
        perfil VARCHAR(30) NULL,
        canal VARCHAR(20) NOT NULL,
        operacion VARCHAR(150) NOT NULL,
        resultado VARCHAR(30) NOT NULL,
        detalle VARCHAR(500) NULL,
        CONSTRAINT pk_auditoria_operaciones PRIMARY KEY (id_auditoria)
    );
END;
GO

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

COMMIT TRANSACTION;
GO
