-- Mueve la lógica de negocio de las publicaciones al servicio (PublicacionesService):
-- el alta de la mascota y el de la publicación quedan como procedimientos simples
-- que el servicio invoca dentro de una misma transacción (@Transactional),
-- usando únicamente año_nacimiento conforme al modelo lógico municipal.
-- Es idempotente: se puede ejecutar más de una vez.
USE [das-mascotas-cordoba];
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
   Procedimiento: get_publicaciones_refugio (RF13)
   Obtiene el listado de publicaciones de adopción administradas por un refugio.
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

IF COL_LENGTH('dbo.mascotas', 'fecha_nacimiento') IS NOT NULL
    ALTER TABLE dbo.mascotas DROP COLUMN fecha_nacimiento;
GO
