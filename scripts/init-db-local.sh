#!/usr/bin/env bash
set -euo pipefail

sqlcmd=(/opt/mssql-tools18/bin/sqlcmd -S sqlserver -U sa -C -b)
exists=$("${sqlcmd[@]}" -h -1 -W -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.databases WHERE name = N'das-mascotas-cordoba';")

if [[ "${exists//[[:space:]]/}" == "1" ]]; then
  echo "La base das-mascotas-cordoba ya existe; se conservan sus datos."
  echo "Si necesitás aplicar cambios de esquema, recreala con: docker compose --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml down -v"
  exit 0
fi

# El script original elimina la base. Para este arranque se usa solo su esquema
# y sus datos iniciales, una vez comprobado que la base todavia no existe.
sed '1,/^USE \[das-mascotas-cordoba\];/d' /scripts/db/municipalidad-creation-script.sql > /tmp/schema.sql
test -s /tmp/schema.sql
"${sqlcmd[@]}" -Q 'CREATE DATABASE [das-mascotas-cordoba];'
"${sqlcmd[@]}" -d das-mascotas-cordoba -f 65001 -i /tmp/schema.sql
echo "Base das-mascotas-cordoba inicializada con los datos de prueba del repositorio."
