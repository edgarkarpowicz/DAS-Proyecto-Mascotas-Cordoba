-- Agrega la fecha de nacimiento exacta de las mascotas (opcional) a las bases
-- creadas antes de que existiera la columna, y actualiza los procedimientos que
-- la devuelven. Debe ejecutarse antes que 002, cuyo ins_mascota_refugio la usa.
-- Es idempotente: se puede ejecutar más de una vez.
USE [das-mascotas-cordoba];
GO

IF COL_LENGTH('dbo.mascotas', 'fecha_nacimiento') IS NULL
    ALTER TABLE dbo.mascotas ADD fecha_nacimiento DATE NULL;
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
           fechaNacimiento       = CONVERT(VARCHAR(10), m.fecha_nacimiento, 23),
           edadAproximada        = CASE
               WHEN m.fecha_nacimiento IS NOT NULL THEN
                   CASE
                       WHEN DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE))
                            - CASE WHEN DATEADD(YEAR, DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE)), m.fecha_nacimiento) > CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END < 0
                       THEN 0
                       ELSE DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE))
                            - CASE WHEN DATEADD(YEAR, DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE)), m.fecha_nacimiento) > CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END
                   END
               ELSE (YEAR(GETDATE()) - m.[año_nacimiento])
           END,
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
           fechaNacimiento = CONVERT(VARCHAR(10), m.fecha_nacimiento, 23),
           edadAproximada  = CASE
               WHEN m.fecha_nacimiento IS NOT NULL THEN
                   CASE
                       WHEN DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE))
                            - CASE WHEN DATEADD(YEAR, DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE)), m.fecha_nacimiento) > CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END < 0
                       THEN 0
                       ELSE DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE))
                            - CASE WHEN DATEADD(YEAR, DATEDIFF(YEAR, m.fecha_nacimiento, CAST(GETDATE() AS DATE)), m.fecha_nacimiento) > CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END
                   END
               ELSE (YEAR(GETDATE()) - m.[año_nacimiento])
           END,
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
