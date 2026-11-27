# Arranque local completo con Docker

Requiere Docker Desktop iniciado y los puertos 4200, 8080 y 1433 libres.
Las dependencias de Java y Angular se instalan dentro de las imágenes; no hace falta instalar Maven, Java o Node en Windows para esta modalidad.

## Primera ejecución

Desde la raíz del repositorio, en PowerShell, crear una contraseña local solo si el archivo todavía no existe:

```powershell
if (-not (Test-Path .env.local)) {
    $dbLocalPassword = 'Mc!' + [Guid]::NewGuid().ToString('N') + '9'
    $jwtLocalSecret = [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
    [System.IO.File]::WriteAllText((Join-Path (Get-Location) '.env.local'), "DB_PASSWORD=$dbLocalPassword`nJWT_SECRET=$jwtLocalSecret`n")
}
docker compose --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml up -d --build
```

La primera compilación descarga las imágenes y las dependencias, por lo que puede tardar varios minutos.
El archivo `.env.local` está excluido de Git. Conservarlo: contiene la contraseña de la base creada y la clave de firma de los JWT.

El arranque espera a que SQL Server responda, crea `das-mascotas-cordoba` con el esquema y los datos iniciales del repositorio y luego inicia el backend y el frontend. Si la base ya existe, conserva sus datos y aplica las migraciones de `db/migrations/`, que actualizan los perfiles, la autenticación y la auditoría sin recrearla.
El servicio `db-init` debe terminar con código 0; es una tarea de inicialización, no un servidor permanente.

## Acceso

- Aplicación: http://localhost:4200
- API REST: http://localhost:8080
- Contrato SOAP de autenticación: http://localhost:8080/services/auth?wsdl
- SQL Server: `localhost,1433`, usuario `sa`, contraseña en `.env.local`.

Usuario de prueba del Refugio Patitas Felices:

- CUIL: `20-25999000-5`
- Contraseña: `pass123`

## Uso posterior

Volver a iniciar sin recompilar:

```powershell
docker compose --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml up -d
```

Consultar el estado y los registros:

```powershell
docker compose --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml ps -a
docker compose --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml logs --tail 100 backend db-init
```

Detener:

```powershell
docker compose --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml down
```

Los datos permanecen en el volumen `mascotas_sqlserver_data` del proyecto Compose. Las fotos permanecen en `uploads/`. No agregar `-v` al comando de detención si se quiere conservar la base.

Este entorno levanta la base municipal utilizada por el backend actual. El script de la base de veterinarias queda disponible en `db/`, pero no es una dependencia de los servicios implementados actualmente.

## Actualizaciones del repositorio

Antes de aplicar cambios al esquema de una base con datos propios, hacer un respaldo de SQL Server.
El script `db/municipalidad-creation-script.sql` elimina la base antes de crearla; no ejecutarlo sobre una base que se quiera conservar.
Para actualizar código y contenedores, repetir el comando de arranque con `--build`.
Al pasar de los tokens antiguos a JWT, volver a iniciar sesión en el navegador.
