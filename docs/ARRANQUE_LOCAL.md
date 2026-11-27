# Arranque Local (Backend y Frontend por Consola + Docker Auxiliar)

El **Backend municipal** y el **Frontend Angular** se ejecutan por separado desde consola y se comunican en `localhost`.
Docker se utiliza únicamente para levantar opcionalmente SQL Server en desarrollo y, en etapas posteriores, los **sistemas externos de Veterinarias** (`8081`, `8082`, `8083`).

## Requisitos
- **Java JDK 17+** (`PATH` configurado).
- **Node.js 20.19+ o 22+** y **npm**.
- **SQL Server** corriendo en `localhost:1433` (instalado localmente o iniciado con Docker Compose).

---

## 1. Base de Datos (`das-mascotas-cordoba`)

### Opción A — SQL Server ya instalado en Windows
Ejecutar `db/municipalidad-creation-script.sql` desde SSMS o Azure Data Studio. Recreará `das-mascotas-cordoba` con el esquema, stored procedures y datos iniciales.

### Opción B — SQL Server en contenedor con Docker Compose (Opcional)
Desde la raíz del repositorio en PowerShell:

```powershell
if (-not (Test-Path .env.local)) {
    $dbLocalPassword = 'Mc!' + [Guid]::NewGuid().ToString('N') + '9'
    [System.IO.File]::WriteAllText((Join-Path (Get-Location) '.env.local'), "DB_PASSWORD=$dbLocalPassword`n")
}
docker compose --env-file .env.local up -d
```

- `db-init` inicializa `das-mascotas-cordoba` desde `db/municipalidad-creation-script.sql` si aún no existe y termina con código `0`.
- Los datos quedan en el volumen `mascotas_sqlserver_data`. Para recrear la base desde cero ante cambios en el script SQL:

```powershell
docker compose --env-file .env.local down -v
docker compose --env-file .env.local up -d
```

---

## 2. Ejecutar Backend en Consola (Terminal 1)

```powershell
cd backend/mascotas-cordoba-project
```

Si usás el contenedor SQL Server (`Opción B`, usuario `sa` y contraseña de `.env.local`), definí las variables antes de iniciar:

```powershell
$env:DB_HOST = "localhost"
$env:DB_USER = "sa"
$env:DB_PASSWORD = "<tu DB_PASSWORD de .env.local>"
.\mvnw.cmd spring-boot:run
```

*(Si usás SQL Server local con los valores por defecto de `application.properties` — `master` / `123` — ejecutá directamente `.\mvnw.cmd spring-boot:run`.)*

- REST disponible en: `http://localhost:8080`
- SOAP WSDL disponible en: `http://localhost:8080/services/auth?wsdl` y `http://localhost:8080/services/publicaciones?wsdl`

---

## 3. Ejecutar Frontend en Consola (Terminal 2)

```powershell
cd frontend/Mascotas-Cordoba-FrontEnd
npm install
npm start
```

- Aplicación web disponible en: `http://localhost:4200/`

---

## 4. Credenciales de Prueba y Roles
- **Refugio Patitas Felices (`REFUGIO`):** CUIL `20-25999000-5` / clave `pass123`
- **Hogar Animal Córdoba (`REFUGIO`):** CUIL `27-38123456-9` / clave `pass123`
- **Ciudadano (`CIUDADANO`):** CUIL `20-28555666-3` / clave `pass123`

## Próximos Contenedores: Veterinarias Adheridas
Cuando se implementen las integraciones **RF06** y **RF09**, los servicios simulados de las veterinarias adheridas se levantarán en contenedores mediante `docker-compose.yml` en los puertos configurados en `db/municipalidad-creation-script.sql` (`8081` REST, `8082` SOAP, `8083` REST) para que el backend ejecutado en consola los consuma por lotes.
