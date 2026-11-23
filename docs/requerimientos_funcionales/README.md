# Documentación SOA de los requerimientos funcionales

Actualización del 11 de noviembre de 2026. Cada RF tiene una carpeta por versión, y cada versión contiene su contrato (DOCX) y su diagrama (PDF). La clasificación sigue siendo **11 obligatorios y 10 no obligatorios**, según la página 9 de la [especificación](../proyecto/01-Especificacion-de-Requerimientos.pdf).

## Estructura de carpetas y versiones

```
obligatorios/ y no_obligatorios/
└── rf_XX/
    ├── anterior/    contenido previo a la primera revisión (solo donde se recuperó)
    ├── renovado/    primera revisión: correcciones y diseño propuesto
    └── renovado2/   segunda revisión: integraciones entre sistemas (solo donde hubo cambios)
```

- Las versiones no se reemplazan entre sí: conviven para poder compararlas. Los nombres de archivo conservan el sufijo de la versión.
- Los RF no obligatorios y RF10/RF11 no tenían versión anterior. Su único par se movió a `renovado/`, sin cambios de contenido.
- La versión vigente de cada RF es la más alta que exista en su carpeta. En RF13 y RF15, renovado2 coincide con el código del Parcial III.

### Versiones previas no conservadas

Ninguna versión se eliminó al reorganizar: los archivos solo se movieron a su subcarpeta. Las nueve versiones `anterior` coinciden byte a byte con las del último commit de `dev`.

Hay un hueco previo a esta reorganización. El 11/11 entre las 13:16 y las 13:18, la primera revisión sobrescribió borradores que nunca llegaron a commitearse: los DOCX de RF01, RF07, RF08 y RF12, y el DOCX y el PDF de RF03, RF10, RF11 y RF14. Esos borradores no están en git. Si se recuperan del historial de versiones de OneDrive, van en una carpeta `anterior/` de cada RF.

## Estado de implementación (Parcial III)

Solo **RF13** y **RF15** están implementados en código (backend Spring Boot con REST y SOAP, frontend Angular y SQL Server). RF06 está implementado solo en parte: dentro del alta de una publicación (`ins_publicacion_adopcion`), cuando la mascota no tiene NRM se le asigna uno nuevo, sin verificar duplicados. El resto de los RF obligatorios (RF09, RF10, RF11, RF16, RF17, RF18, RF20 y RF21) está documentado pero no implementado.

- **RF13 y RF15: la versión `anterior` es la base de la implementación.** El código recibe `idRefugio` y `token` en la solicitud, igual que el contrato anterior. Las diferencias son que el código agrega la foto, la condición de adopción y los datos de una mascota nueva, y que el cambio de estado es un endpoint aparte.
- **La versión `renovado2` de RF13 y RF15 describe exactamente lo implementado**: las operaciones reales con su endpoint REST y su operación SOAP, los campos que procesa el código, la foto, la condición de adopción y el cambio de estado, sin CiDi. Es la versión para presentar. La `renovado` queda como diseño alternativo y no se modificó el código.

## Cambios de la versión renovado2

Todos los cambios son de documentación. En RF13 y RF15 se documentó el código tal como está. No se modificó código, base de datos ni especificación.

- **RF06 y RF09: se agrega la integración en la que Mascotas Córdoba consume a la veterinaria.** Además de la carga municipal y del envío de la veterinaria, se documenta la modalidad CONSULTA. En ella, un proceso programado de Mascotas Córdoba invoca el servicio REST o SOAP de cada veterinaria con la URL, la tecnología y la API Key configuradas en RF04. Nuevas operaciones del servicio *Integración Veterinaria*, que expone cada veterinaria:
  - `ObtenerMascotasSinNRM()`: devuelve las mascotas que todavía no tienen `nro_reg_municipal`.
  - `InformarNRM()`: comunica el NRM asignado. Es idempotente, así que si falla se reintenta sin generar duplicados.
  - `ObtenerAtencionesSanitarias(desde)`: devuelve las atenciones nuevas. Solo se consolidan prácticas del programa y se descartan las repetidas.
  - Si una veterinaria no responde, se registra la falla y se continúa con las demás (RNF06). Los diagramas tienen una página 2 con este proceso.
  - En RF09, `ID Veterinaria` pasa a ser condicional: en el envío se obtiene de la credencial de la veterinaria (RNF04).
- **RF13 y RF15:** documentan la implementación del Parcial III. RF15 tiene una sola operación, `AutenticarUsuario()`, con CUIL y clave locales y sin proveedor de identidad externo. RF13 tiene siete operaciones: `ConsultarPublicacionesRefugio()`, `ConsultarMascotasDisponibles()`, `ConsultarEstadosPublicacion()`, `ConsultarEspecies()`, `SubirFotoPublicacion()`, `CrearPublicacion()` y `ActualizarEstadoPublicacion()`. Las dos últimas implementan la operación lógica `AdministrarPublicacion()` de las versiones anteriores.
- **RF04:** las modalidades pasan a ser CONSULTA, ENVIO e IMPORTACION. Se enumeran los atributos del modelo lógico (URL del servicio, Tecnología del servicio, Parámetros del servicio, Api Key y fecha de última obtención) y se aclara que RF06/RF09 los usan para elegir entre REST y SOAP sin cambiar código (RNF09).
- **RF11:** la validación del carnet la invoca el **sistema del tercero autorizado** con su credencial propia (RNF04), sin token de sesión de usuario.
- **RF16:** `MostrarPortalWeb()` pasa a llamarse `ConsultarServiciosHabilitados()`, porque la operación devuelve datos de navegación y no una pantalla.
- **Nombre del cliente (RF04, RF06, RF09, RF10, RF11 y RF16):** se usa siempre *Portal Mascotas Córdoba* indicando el perfil, en lugar de "Plataforma Angular", "Portal ciudadano" o "Portal administrativo". En RF10 este es el único cambio y su diagrama se copió sin modificaciones. Los RF sin renovado2 conservan su nomenclatura.

## Versión renovado3: análisis SOA según la devolución de la cátedra

Después de la presentación del 13/11/2026, la cátedra indicó que el análisis SOA debe considerar **solo las integraciones entre sistemas distintos**. El portal Angular y la base de datos son parte de Mascotas Córdoba y no cuentan como integraciones. Los terceros autorizados de RF11 son actores, no sistemas. Las integraciones son tres, y en todas Mascotas Córdoba es el cliente:

| Integración | RF | Tipo | Operaciones del sistema externo |
| --- | --- | --- | --- |
| Mascotas Córdoba → Sistema de Gestión de la Veterinaria | RF06, RF09 (configurada en RF04) | Por lotes | `ObtenerMascotasSinNRM()`, `InformarNRM()`, `ObtenerAtencionesSanitarias()` |
| Mascotas Córdoba → Ciudadano Digital (CiDi) | RF15 | En línea | `ValidarIdentidadCiudadana()` |
| Mascotas Córdoba → Servicio de IA generativa | RF21 | En línea, con contexto fijo | `GenerarRespuestaAsistente()` |

Los contratos renovado3 analizan solo el proceso de Mascotas Córdoba con el sistema externo, sin pantallas ni el intercambio del portal con el backend:

- **RF06:** [DOCX](obligatorios/rf_06/renovado3/plantilla_rf06_renovado3.docx) · [PDF](obligatorios/rf_06/renovado3/RF06%20-%20Registrar%20Mascota%20-%20renovado3.pdf)
- **RF09:** [DOCX](obligatorios/rf_09/renovado3/plantilla_rf09_renovado3.docx) · [PDF](obligatorios/rf_09/renovado3/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado3.pdf)
- **RF15:** [DOCX](obligatorios/rf_15/renovado3/plantilla_rf15_renovado3.docx) · [PDF](obligatorios/rf_15/renovado3/RF15%20-%20Autenticar%20Usuarios%20-%20renovado3.pdf)
- **RF21:** [DOCX](obligatorios/rf_21/renovado3/plantilla_rf21_renovado3.docx) · [PDF](obligatorios/rf_21/renovado3/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado3.pdf)

Los diagramas muestran sólo el proceso de Mascotas Córdoba con el sistema externo: el lote que recorre las veterinarias (RF06 y RF09) y los flujos en línea de CiDi (RF15) y la IA (RF21). El rectángulo con doble barra marca la invocación al sistema externo.

## Versión renovado4: diagramas y contratos según el modelo de la cátedra

La versión renovado4 rehace los diagramas y contratos de las tres integraciones tomando como modelo los ejercicios resueltos por la cátedra (SUBE, procesos por lote). El renovado3 se conserva sin cambios.

| Integración | RF | Tipo | Operaciones del sistema externo |
| --- | --- | --- | --- |
| Mascotas Córdoba → Sistema de Gestión de la Veterinaria | RF06, RF09 (configurada en RF04) | Por lote | `ObtenerMascotasSinNRM()`, `InformarNRM()`, `ObtenerAtencionesSanitarias()` |
| Mascotas Córdoba → Ciudadano Digital (CiDi) | RF15 | En línea | `ValidarIdentidadCiudadana()` |
| Mascotas Córdoba → Servicio de IA generativa | RF21 | En línea | `GenerarRespuestaAsistente()` |

Cambios respecto del renovado3:

- **Procesos por lote (RF06 y RF09):** Inicio → determinar veterinarias configuradas → decisión "> 0" → obtener primera veterinaria → subproceso (invocación a la veterinaria) → decisión "Éxito" → base de datos (registrar y lote procesado) → decisión "Éxito" → vuelve a "> 0". Ante un error: "Enviar correo al administrador" → "Registrar log", y se continúa con la siguiente veterinaria. Un solo Fin.
- **Lotes en los contratos:** cada operación lleva `Token` e `idLote`; las mascotas, los NRM y las atenciones viajan como listas (JSON). `InformarNRM()` se invoca una vez por veterinaria, no una vez por mascota.
- **Procesos en línea (RF15 y RF21):** dos decisiones después del subproceso ("¿Transacción exitosa?" y el resultado de negocio), figura de base de datos con su decisión de éxito, un solo camino de error y un solo Fin. CiDi devuelve códigos de negocio (1, 2, 3, -1). La IA recibe un contexto público o según el perfil.
- **Documentos:** la Ubicación es el sistema externo, la Descripción indica "Este es un proceso por lote" o "en línea", y cada proceso suma una "Justificación del diseño".

Archivos:

- **RF06:** [DOCX](obligatorios/rf_06/renovado4/plantilla_rf06_renovado4.docx) · [PDF](obligatorios/rf_06/renovado4/RF06%20-%20Registrar%20Mascota%20-%20renovado4.pdf)
- **RF09:** [DOCX](obligatorios/rf_09/renovado4/plantilla_rf09_renovado4.docx) · [PDF](obligatorios/rf_09/renovado4/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado4.pdf)
- **RF15:** [DOCX](obligatorios/rf_15/renovado4/plantilla_rf15_renovado4.docx) · [PDF](obligatorios/rf_15/renovado4/RF15%20-%20Autenticar%20Usuarios%20-%20renovado4.pdf)
- **RF21:** [DOCX](obligatorios/rf_21/renovado4/plantilla_rf21_renovado4.docx) · [PDF](obligatorios/rf_21/renovado4/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado4.pdf)

## Versión renovado5: renovado4 con los ajustes de la revisión del equipo

La versión renovado5 mantiene la notación y el patrón por lote del renovado4 e incorpora cuatro mejoras surgidas de la revisión de los diagramas. El renovado4 se conserva sin cambios.

- **RF06:** `InformarNRM()` devuelve un acuse por mascota. El NRM se guarda primero como pendiente de informar y se confirma al recibir su acuse; lo no confirmado se informa en el próximo lote, sin deshacer el alta ni generar otro NRM.
- **RF09:** el avance se guarda con un cursor (`cursor` / `cursorSiguiente`) en lugar de una fecha. El cursor sólo se adelanta si todas las atenciones del conjunto se registraron con éxito.
- **RF15:** se agrega la decisión "¿Perfil habilitado?": CiDi identifica y Mascotas Córdoba autoriza.
- **RF21:** la primera decisión es "¿Requiere información personalizada?"; si la requiere, el usuario debe estar autenticado y autorizado. El contrato usa `tipoContexto` (PUBLICO o AUTORIZADO) y no envía datos identificatorios.

La lógica interna (duplicados, filtrado de prácticas, anonimizado) no se dibuja como subproceso: queda en la "Justificación del diseño" y en las observaciones de cada contrato.

- **RF06:** [DOCX](obligatorios/rf_06/renovado5/plantilla_rf06_renovado5.docx) · [PDF](obligatorios/rf_06/renovado5/RF06%20-%20Registrar%20Mascota%20-%20renovado5.pdf)
- **RF09:** [DOCX](obligatorios/rf_09/renovado5/plantilla_rf09_renovado5.docx) · [PDF](obligatorios/rf_09/renovado5/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado5.pdf)
- **RF15:** [DOCX](obligatorios/rf_15/renovado5/plantilla_rf15_renovado5.docx) · [PDF](obligatorios/rf_15/renovado5/RF15%20-%20Autenticar%20Usuarios%20-%20renovado5.pdf)
- **RF21:** [DOCX](obligatorios/rf_21/renovado5/plantilla_rf21_renovado5.docx) · [PDF](obligatorios/rf_21/renovado5/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado5.pdf)

## Versión renovado6: precisiones de texto sobre el renovado5

La versión renovado6 no cambia la estructura de los diagramas: precisa textos, notas y observaciones. El renovado5 se conserva sin cambios.

- **RF06 y RF09:** "Obtener siguiente veterinaria"; la veterinaria que se toma sale de las pendientes de la ejecución aunque falle, y se reintenta en el próximo lote.
- **RF06:** se aclara que al registrar se buscan coincidencias y se reutiliza el NRM existente, y que se informan también los NRM pendientes de lotes anteriores.
- **RF09:** cada atención se asocia a una mascota ya registrada; un reintento no duplica ni pierde historial; lo pendiente impide adelantar el cursor.
- **RF15:** el retorno de CiDi queda como precondición; el último paso es "Iniciar sesión con perfil y permisos; mostrar pantalla principal"; los códigos son la convención del contrato diseñado, no códigos propios de CiDi.
- **RF21:** se define "Respuesta válida", el mensaje al usuario depende de la causa y el filtrado de consulta y contexto queda como precondición del consumidor (lo hace Mascotas Córdoba antes de invocar).

- **RF06:** [DOCX](obligatorios/rf_06/renovado6/plantilla_rf06_renovado6.docx) · [PDF](obligatorios/rf_06/renovado6/RF06%20-%20Registrar%20Mascota%20-%20renovado6.pdf)
- **RF09:** [DOCX](obligatorios/rf_09/renovado6/plantilla_rf09_renovado6.docx) · [PDF](obligatorios/rf_09/renovado6/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado6.pdf)
- **RF15:** [DOCX](obligatorios/rf_15/renovado6/plantilla_rf15_renovado6.docx) · [PDF](obligatorios/rf_15/renovado6/RF15%20-%20Autenticar%20Usuarios%20-%20renovado6.pdf)
- **RF21:** [DOCX](obligatorios/rf_21/renovado6/plantilla_rf21_renovado6.docx) · [PDF](obligatorios/rf_21/renovado6/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado6.pdf)

## Versión renovado7: datos de RF06 exigidos por la especificación

El único cambio de contenido respecto del renovado6 está en el contrato de RF06: `MascotaVeterinaria` suma `castrado` y `edadAproximada` (alternativa a `fechaNacimiento`), y `responsable` suma `cuil`, porque RF06 los pide expresamente. RF09, RF15 y RF21 son iguales al renovado6 y se incluyen para que la versión esté completa. El renovado6 se conserva sin cambios.

- **RF06:** [DOCX](obligatorios/rf_06/renovado7/plantilla_rf06_renovado7.docx) · [PDF](obligatorios/rf_06/renovado7/RF06%20-%20Registrar%20Mascota%20-%20renovado7.pdf)
- **RF09:** [DOCX](obligatorios/rf_09/renovado7/plantilla_rf09_renovado7.docx) · [PDF](obligatorios/rf_09/renovado7/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado7.pdf)
- **RF15:** [DOCX](obligatorios/rf_15/renovado7/plantilla_rf15_renovado7.docx) · [PDF](obligatorios/rf_15/renovado7/RF15%20-%20Autenticar%20Usuarios%20-%20renovado7.pdf)
- **RF21:** [DOCX](obligatorios/rf_21/renovado7/plantilla_rf21_renovado7.docx) · [PDF](obligatorios/rf_21/renovado7/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado7.pdf)
- **RF07** (no obligatorio): [DOCX](no_obligatorios/rf_07/renovado7/plantilla_rf07_renovado7.docx) · [PDF](no_obligatorios/rf_07/renovado7/RF07%20-%20Actualizar%20Informaci%C3%B3n%20de%20Mascotas%20-%20renovado7.pdf)

RF07 se suma como tercer proceso por lote con las veterinarias: `ObtenerMascotasModificadas()` es una operación más del mismo servicio de RF06 y RF09. La especificación no fija el origen de las actualizaciones; se propone la veterinaria como una de las fuentes. No es una integración nueva: los sistemas externos siguen siendo tres.

Pendientes conocidos, no corregidos a propósito (RF09 no se implementa): la clave de duplicados de atenciones debería ser veterinaria + `nroAtencion`, y el cursor puede dejar atrás atenciones de mascotas que reciben su NRM más tarde.

## Renovado10 en el resto de los RF (sin integración)

Con la misma etiqueta **renovado10** se rehicieron los 16 RF que no son integraciones: RF01, RF02, RF03, RF04, RF05, RF08, RF10, RF11, RF12, RF13, RF14, RF16, RF17, RF18, RF19 y RF20. Sus versiones anteriores (renovado y renovado2) usaban el criterio que la cátedra rechazó (el portal como integración) y se conservan sin cambios.

Cada uno tiene, en `rf_XX/renovado10/`:

- **Diagrama** del proceso interno con la notación de la cátedra (entrada manual, proceso, base de datos, decisión, pantalla), sin subprocesos de integración.
- **DOCX de plantilla**: pasos 1 y 2 completos (la tabla de actividades indica "Integración: No"), pasos 3 a 5 y SERVICIOS como "No corresponde", y la ficha **"Por qué no es una integración"**. RF11, RF12 y RF13 remiten a las candidatas D, A y B.

Las candidatas A, B, E y F también se alinearon con las correcciones (rango de fechas, paginación y, en F, información planchada). Sus versiones anteriores están en `integraciones/candidatas/versiones_anteriores/`.

**Versión vigente de cada RF:** la carpeta `renovado10` (RF15, `renovado8`).

## Versión renovado10: correcciones de la profesora tras la presentación (23/11/2026)

**Es la versión vigente.** Rehace RF06, RF09 y RF21 (y alinea RF07, candidata); RF15 sigue en renovado8.

- **Veterinarias (RF06 y RF09):** se reemplazó `idLote` (y el cursor de RF09) por un **rango de fechas** (`fechaDesde`, `fechaHasta`). Mascotas Córdoba guarda la última fecha procesada de cada veterinaria y solo la adelanta cuando el lote se registró con éxito. Se agregó **paginación opcional** (`pagina`, `tamanioPagina`, `totalPaginas`).
- **Información sanitaria planchada (RF09):** la operación devuelve `practicas`, un registro plano por práctica, sin listas anidadas; los datos del profesional van concatenados en un solo campo.
- **IA (RF21):** el **RAG se hace al armar el prompt**, en un mismo paso ("Recuperar información (RAG) y armar prompt ReAct"). Se quitó el ciclo de acciones: hay una sola invocación por consulta, y el contrato vuelve a recibir `contexto`.

Archivos en `obligatorios/rf_06/renovado10/`, `rf_09/renovado10/`, `rf_21/renovado10/` y `no_obligatorios/rf_07/renovado10/`. El informe breve y el anexo de `integraciones/identificadas/` ya incluyen esta versión; los anteriores quedaron en `versiones_anteriores/`.

## Versión renovado9: RF21 como ReAct prompting y RAG

La devolución de la profesora indica dos cosas: que el registro de mascotas y la información sanitaria son procesos por lote (ya lo eran desde el renovado4) y que el proceso de IA debe diagramarse como **ReAct prompting y RAG**. El renovado9 rehace solo RF21; RF06, RF09 y RF15 siguen en renovado8.

- **ReAct:** el subproceso de IA queda dentro de un ciclo. En cada paso la IA devuelve una acción (pedir información) o la respuesta final. El contrato suma `herramientas`, `pasos`, `razonamiento`, `accion` y `entradaAccion`, y el código 2 "solicita una acción".
- **RAG:** la información que pide la IA se recupera de la base de la propia plataforma y se agrega al prompt. Las herramientas (`BuscarInformacionPrograma`, `ConsultarMisMascotas`) son consultas internas, no integraciones.
- Antes de recuperar se verifica que el usuario esté autorizado, y el ciclo tiene un máximo de pasos.

- **RF21:** [DOCX](obligatorios/rf_21/renovado9/plantilla_rf21_renovado9.docx) · [PDF](obligatorios/rf_21/renovado9/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado9.pdf)

El informe breve y el anexo de `integraciones/identificadas/` ya incluyen esta versión.

## Dónde está lo que se presenta

Todo el análisis de integraciones está reunido en **[integraciones/](integraciones/LEEME.md)**: `identificadas/` (las tres integraciones, con el informe, los diagramas y los contratos) y `candidatas/` (las evaluadas y no tomadas). Las carpetas por RF conservan el historial de versiones.

## Versión renovado8: ajustes de plantilla y documento breve

**Es la versión a presentar.** El renovado7 se conserva sin cambios.

Documentos de lectura:

- **[Integraciones - Mascotas Cordoba.pdf](integraciones/identificadas/Integraciones%20-%20Mascotas%20Cordoba.pdf)** (11 páginas): el documento para exponer. Una portada con las tres integraciones y las candidatas descartadas, y para cada integración: descripción, diagrama y contrato.
- **[Anexo - Analisis SOA detallado - Mascotas Cordoba.pdf](integraciones/identificadas/Anexo%20-%20Analisis%20SOA%20detallado%20-%20Mascotas%20Cordoba.pdf)** (25 páginas): el mismo contenido con criterios de diseño, lectura paso a paso de cada diagrama y RF07.
- `integraciones/identificadas/versiones_anteriores/Analisis SOA - Integraciones - Mascotas Cordoba.pdf` es la versión anterior del anexo (contenido renovado7) y se conserva.

Cambios respecto del renovado7, a partir de dos revisiones externas:

- **Plantilla:** la columna Tipo usa sólo Mandatorio y Opcional (se quitó "Condicional"); la columna de ejemplo tiene valores de muestra; "Ingresar consulta" usa la figura de entrada manual; el Alcance es una oración y el resto pasó a la Descripción.
- **RF09:** la veterinaria devuelve todas las atenciones a partir del cursor (ya no filtra por NRM), con `idMascota` y `nroDetalle`; la atención se identifica por veterinaria, mascota, número de atención, tipo y detalle. Así el cursor no deja atenciones atrás y se elimina una contradicción del contrato. `cursorSiguiente` admite vacío.
- **RF06:** primero se reconoce la mascota ya recibida de esa veterinaria (veterinaria e `idMascota`); se agregó un ejemplo de mascota.
- **RF15:** el último paso se separó en "Iniciar sesión con perfil y permisos" y "Mostrar pantalla principal"; se definió el nivel mínimo de cuenta como parámetro y la regla para el ciudadano recién registrado.
- **RF21:** se quitó `tipoContexto` de la solicitud (el proveedor no lo necesita) y se acotó `longitudMaxima`.
- **RF07:** se declaró la regla de precedencia entre fuentes y el requisito de seguimiento de cambios en la veterinaria. No se expone como integración; queda en el anexo.

Archivos:

- **RF06:** [DOCX](obligatorios/rf_06/renovado8/plantilla_rf06_renovado8.docx) · [PDF](obligatorios/rf_06/renovado8/RF06%20-%20Registrar%20Mascota%20-%20renovado8.pdf)
- **RF09:** [DOCX](obligatorios/rf_09/renovado8/plantilla_rf09_renovado8.docx) · [PDF](obligatorios/rf_09/renovado8/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado8.pdf)
- **RF15:** [DOCX](obligatorios/rf_15/renovado8/plantilla_rf15_renovado8.docx) · [PDF](obligatorios/rf_15/renovado8/RF15%20-%20Autenticar%20Usuarios%20-%20renovado8.pdf)
- **RF21:** [DOCX](obligatorios/rf_21/renovado8/plantilla_rf21_renovado8.docx) · [PDF](obligatorios/rf_21/renovado8/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado8.pdf)
- **RF07** (no obligatorio): [DOCX](no_obligatorios/rf_07/renovado8/plantilla_rf07_renovado8.docx) · [PDF](no_obligatorios/rf_07/renovado8/RF07%20-%20Actualizar%20Informaci%C3%B3n%20de%20Mascotas%20-%20renovado8.pdf)

## Integraciones candidatas (evaluadas y no tomadas)

Además de las tres integraciones identificadas, se prepararon el diagrama y el contrato de los puntos que **podrían** considerarse integraciones y que no se tomaron. Sirven de respaldo si en la defensa se señala alguno. Están en [integraciones/candidatas/](integraciones/candidatas/) y reunidos en **[Integraciones candidatas - Mascotas Cordoba.pdf](integraciones/candidatas/Integraciones%20candidatas%20-%20Mascotas%20Cordoba.pdf)** (18 páginas), que explica para cada una por qué podría ser una integración, por qué no se tomó y cómo sería.

| | Candidata | RF | Sistema externo | Tipo | Rol de Mascotas Córdoba | Operaciones |
| --- | --- | --- | --- | --- | --- | --- |
| A | Refugios: animales bajo cuidado | RF12 | Sistema de Gestión del Refugio | Por lote | Consumidora | `ObtenerAnimalesSinNRM()`, `InformarNRM()` |
| B | Refugios: publicaciones de adopción | RF13 | Sistema de Gestión del Refugio | Por lote | Consumidora | `ObtenerPublicacionesModificadas()` |
| C | Identificación del personal municipal | RF15 | Sistema de Identificación Municipal | En línea | Consumidora | `AutenticarPersonalMunicipal()` |
| D | Terceros autorizados: validar carnet | RF11 | Sistema del tercero autorizado | En línea | Proveedora | `ValidarCarnetSanitario()` |
| E | Veterinaria que envía mascotas | RF06 | Sistema de Gestión de la Veterinaria | Por lote | Proveedora | `RegistrarMascotas()` |
| F | Veterinaria que envía información sanitaria | RF09 | Sistema de Gestión de la Veterinaria | Por lote | Proveedora | `RegistrarAtencionesSanitarias()` |

RF07 también es una candidata evaluada; su material está en `no_obligatorios/rf_07/renovado8/` y en el anexo detallado.

## Archivos por RF

- **RF06** (obligatorios) — anterior: [DOCX](obligatorios/rf_06/anterior/plantilla_rf06_anterior.docx) · [PDF](obligatorios/rf_06/anterior/RF06%20-%20Registrar%20Mascota%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_06/renovado/plantilla_rf06_renovado.docx) · [PDF](obligatorios/rf_06/renovado/RF06%20-%20Registrar%20Mascota%20-%20renovado.pdf) — renovado2: [DOCX](obligatorios/rf_06/renovado2/plantilla_rf06_renovado2.docx) · [PDF](obligatorios/rf_06/renovado2/RF06%20-%20Registrar%20Mascota%20-%20renovado2.pdf)
- **RF09** (obligatorios) — anterior: [DOCX](obligatorios/rf_09/anterior/plantilla_rf09_anterior.docx) · [PDF](obligatorios/rf_09/anterior/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_09/renovado/plantilla_rf09_renovado.docx) · [PDF](obligatorios/rf_09/renovado/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado.pdf) — renovado2: [DOCX](obligatorios/rf_09/renovado2/plantilla_rf09_renovado2.docx) · [PDF](obligatorios/rf_09/renovado2/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado2.pdf)
- **RF10** (obligatorios) — renovado: [DOCX](obligatorios/rf_10/renovado/plantilla_rf10_renovado.docx) · [PDF](obligatorios/rf_10/renovado/RF10%20-%20Generar%20Carnet%20Sanitario%20Digital%20-%20renovado.pdf) — renovado2: [DOCX](obligatorios/rf_10/renovado2/plantilla_rf10_renovado2.docx) · [PDF](obligatorios/rf_10/renovado2/RF10%20-%20Generar%20Carnet%20Sanitario%20Digital%20-%20renovado2.pdf)
- **RF11** (obligatorios) — renovado: [DOCX](obligatorios/rf_11/renovado/plantilla_rf11_renovado.docx) · [PDF](obligatorios/rf_11/renovado/RF11%20-%20Consultar%20y%20Validar%20Carnet%20Sanitario%20Digital%20-%20renovado.pdf) — renovado2: [DOCX](obligatorios/rf_11/renovado2/plantilla_rf11_renovado2.docx) · [PDF](obligatorios/rf_11/renovado2/RF11%20-%20Consultar%20y%20Validar%20Carnet%20Sanitario%20Digital%20-%20renovado2.pdf)
- **RF13** (obligatorios) — anterior: [DOCX](obligatorios/rf_13/anterior/plantilla_rf13_anterior.docx) · [PDF](obligatorios/rf_13/anterior/RF13%20-%20Gestionar%20Publicaciones%20de%20Adopci%C3%B3n%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_13/renovado/plantilla_rf13_renovado.docx) · [PDF](obligatorios/rf_13/renovado/RF13%20-%20Gestionar%20Publicaciones%20de%20Adopci%C3%B3n%20-%20renovado.pdf) — renovado2: [DOCX](obligatorios/rf_13/renovado2/plantilla_rf13_renovado2.docx) · [PDF](obligatorios/rf_13/renovado2/RF13%20-%20Gestionar%20Publicaciones%20de%20Adopci%C3%B3n%20-%20renovado2.pdf)
- **RF15** (obligatorios) — anterior: [DOCX](obligatorios/rf_15/anterior/plantilla_rf15_anterior.docx) · [PDF](obligatorios/rf_15/anterior/RF15%20-%20Autenticar%20Usuarios%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_15/renovado/plantilla_rf15_renovado.docx) · [PDF](obligatorios/rf_15/renovado/RF15%20-%20Autenticar%20Usuarios%20-%20renovado.pdf) — renovado2: [DOCX](obligatorios/rf_15/renovado2/plantilla_rf15_renovado2.docx) · [PDF](obligatorios/rf_15/renovado2/RF15%20-%20Autenticar%20Usuarios%20-%20renovado2.pdf)
- **RF16** (obligatorios) — anterior: [DOCX](obligatorios/rf_16/anterior/plantilla_rf16_anterior.docx) · [PDF](obligatorios/rf_16/anterior/RF16%20-%20Acceder%20al%20Portal%20del%20Programa%20Municipal%20de%20Bienestar%20Animal%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_16/renovado/plantilla_rf16_renovado.docx) · [PDF](obligatorios/rf_16/renovado/RF16%20-%20Acceder%20al%20Portal%20del%20Programa%20Municipal%20de%20Bienestar%20Animal%20-%20renovado.pdf) — renovado2: [DOCX](obligatorios/rf_16/renovado2/plantilla_rf16_renovado2.docx) · [PDF](obligatorios/rf_16/renovado2/RF16%20-%20Acceder%20al%20Portal%20del%20Programa%20Municipal%20de%20Bienestar%20Animal%20-%20renovado2.pdf)
- **RF17** (obligatorios) — anterior: [DOCX](obligatorios/rf_17/anterior/plantilla_rf17_anterior.docx) · [PDF](obligatorios/rf_17/anterior/RF17%20-%20Consultar%20Veterinarias%20Adheridas%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_17/renovado/plantilla_rf17_renovado.docx) · [PDF](obligatorios/rf_17/renovado/RF17%20-%20Consultar%20Veterinarias%20Adheridas%20-%20renovado.pdf)
- **RF18** (obligatorios) — anterior: [DOCX](obligatorios/rf_18/anterior/plantilla_rf18_anterior.docx) · [PDF](obligatorios/rf_18/anterior/RF18%20-%20Consultar%20Refugios%20Adheridos%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_18/renovado/plantilla_rf18_renovado.docx) · [PDF](obligatorios/rf_18/renovado/RF18%20-%20Consultar%20Refugios%20Adheridos%20-%20renovado.pdf)
- **RF20** (obligatorios) — anterior: [DOCX](obligatorios/rf_20/anterior/plantilla_rf20_anterior.docx) · [PDF](obligatorios/rf_20/anterior/RF20%20-%20Consultar%20Mis%20Mascotas%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_20/renovado/plantilla_rf20_renovado.docx) · [PDF](obligatorios/rf_20/renovado/RF20%20-%20Consultar%20Mis%20Mascotas%20-%20renovado.pdf)
- **RF21** (obligatorios) — anterior: [DOCX](obligatorios/rf_21/anterior/plantilla_rf21_anterior.docx) · [PDF](obligatorios/rf_21/anterior/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20anterior.pdf) — renovado: [DOCX](obligatorios/rf_21/renovado/plantilla_rf21_renovado.docx) · [PDF](obligatorios/rf_21/renovado/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado.pdf)
- **RF01** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_01/renovado/plantilla_rf01_renovado.docx) · [PDF](no_obligatorios/rf_01/renovado/RF01%20-%20Solicitar%20Adhesi%C3%B3n%20de%20Veterinarias%20-%20renovado.pdf)
- **RF02** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_02/renovado/plantilla_rf02_renovado.docx) · [PDF](no_obligatorios/rf_02/renovado/RF02%20-%20Solicitar%20Adhesi%C3%B3n%20de%20Refugios%20-%20renovado.pdf)
- **RF03** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_03/renovado/plantilla_rf03_renovado.docx) · [PDF](no_obligatorios/rf_03/renovado/RF03%20-%20Aprobar%20Organizaciones%20Adheridas%20-%20renovado.pdf)
- **RF04** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_04/renovado/plantilla_rf04_renovado.docx) · [PDF](no_obligatorios/rf_04/renovado/RF04%20-%20Configurar%20Integraci%C3%B3n%20de%20Veterinarias%20-%20renovado.pdf) — renovado2: [DOCX](no_obligatorios/rf_04/renovado2/plantilla_rf04_renovado2.docx) · [PDF](no_obligatorios/rf_04/renovado2/RF04%20-%20Configurar%20Integraci%C3%B3n%20de%20Veterinarias%20-%20renovado2.pdf)
- **RF05** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_05/renovado/plantilla_rf05_renovado.docx) · [PDF](no_obligatorios/rf_05/renovado/RF05%20-%20Administrar%20Campa%C3%B1as%20Municipales%20-%20renovado.pdf)
- **RF07** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_07/renovado/plantilla_rf07_renovado.docx) · [PDF](no_obligatorios/rf_07/renovado/RF07%20-%20Actualizar%20Informaci%C3%B3n%20de%20Mascotas%20-%20renovado.pdf)
- **RF08** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_08/renovado/plantilla_rf08_renovado.docx) · [PDF](no_obligatorios/rf_08/renovado/RF08%20-%20Consultar%20Mascotas%20Registradas%20-%20renovado.pdf)
- **RF12** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_12/renovado/plantilla_rf12_renovado.docx) · [PDF](no_obligatorios/rf_12/renovado/RF12%20-%20Gestionar%20Animales%20Bajo%20Cuidado%20-%20renovado.pdf)
- **RF14** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_14/renovado/plantilla_rf14_renovado.docx) · [PDF](no_obligatorios/rf_14/renovado/RF14%20-%20Gestionar%20Procesos%20de%20Adopci%C3%B3n%20-%20renovado.pdf)
- **RF19** (no obligatorios) — renovado: [DOCX](no_obligatorios/rf_19/renovado/plantilla_rf19_renovado.docx) · [PDF](no_obligatorios/rf_19/renovado/RF19%20-%20Consultar%20Campa%C3%B1as%20Municipales%20-%20renovado.pdf)

## Ajustes de la versión renovado (primera revisión)

- Se unificaron NRM como entero positivo y Token como String [1, 64]. La identidad del usuario y del refugio se obtiene de la sesión en las operaciones protegidas.
- Se completaron castración, fotografía, condiciones de adopción y vencimiento sanitario opcional. Los objetos compartidos se reutilizan mediante referencias entre contratos.
- Se distinguió acceso público de acceso personalizado. Una API Key de aplicación no concede permisos de usuario ni acredita por sí sola a una organización integrada.
- Se corrigieron nombres de operaciones, mensajes y objetos copiados de otros RF. Cada operación mostrada tiene contrato o referencia al RF que lo define.
- Se incorporaron las integraciones externas de identidad e IA como contratos lógicos de adaptadores propuestos. No se presentan como APIs oficiales ni como funcionalidades ya programadas.
- Se corrigieron los caminos de consulta vacía y el cierre de la interacción con una adopción APROBADA aún no concretada.
- Se conservaron las cinco etapas de análisis, las tablas de la plantilla y la apariencia institucional. Los contratos antiguos se ajustaron al formato compacto de los recientes; los diagramas mantienen acciones breves y participantes indicados una vez.

Se ajustaron **17 DOCX y 13 PDF**; los restantes se revisaron y conservaron. El formato sencillo permite más de una operación cuando el problema lo necesita: por ejemplo, consultar y guardar son intercambios diferentes.

## Módulos

- Administración del Programa: RF01, RF02, RF03, RF04 y RF05.
- Registro Único de Mascotas: RF06, RF07 y RF08.
- Gestión Sanitaria: RF09, RF10 y RF11.
- Gestión de Refugios: RF12, RF13 y RF14.
- Acceso a la Plataforma: RF15.
- Servicios para los Ciudadanos: RF16, RF17, RF18, RF19 y RF20.
- Asistente Virtual: RF21.

Los servicios se identifican por su nombre funcional en el paso 4. El número “Identificador: 1” de la plantilla es local al documento, no un identificador global del catálogo. Los nombres de operaciones son propuestas lógicas; su mapeo a rutas REST o métodos SOAP corresponde al diseño técnico.

## Comprobación de los 21 RF

- **RF01 · No obligatorio.** Solicitar adhesión de veterinarias. La constancia queda PENDIENTE; RF03 evalúa y RF04 configura. Se igualó la longitud de la modalidad con RF04. [Contrato](no_obligatorios/rf_01/renovado/plantilla_rf01_renovado.docx) · [Diagrama](no_obligatorios/rf_01/renovado/RF01%20-%20Solicitar%20Adhesi%C3%B3n%20de%20Veterinarias%20-%20renovado.pdf).
- **RF02 · No obligatorio.** Solicitar adhesión de refugios. Identificación, contacto y responsable; la recepción no habilita al refugio. Se mantiene el par original. [Contrato](no_obligatorios/rf_02/renovado/plantilla_rf02_renovado.docx) · [Diagrama](no_obligatorios/rf_02/renovado/RF02%20-%20Solicitar%20Adhesi%C3%B3n%20de%20Refugios%20-%20renovado.pdf).
- **RF03 · No obligatorio.** Aprobar organizaciones. Consulta y evaluación municipal; aprobación, rechazo y pedido de información adicional son resultados de negocio. El dibujo contempla consulta vacía y no confunde OBSERVADA con error. [Contrato](no_obligatorios/rf_03/renovado/plantilla_rf03_renovado.docx) · [Diagrama](no_obligatorios/rf_03/renovado/RF03%20-%20Aprobar%20Organizaciones%20Adheridas%20-%20renovado.pdf).
- **RF04 · No obligatorio.** Configurar integración de veterinarias. Sólo veterinarias aprobadas; consulta y guardado comparten Configuracion. La ausencia de configuración permite el alta y los secretos no se devuelven. Se mantiene el par original. [Contrato](no_obligatorios/rf_04/renovado2/plantilla_rf04_renovado2.docx) · [Diagrama](no_obligatorios/rf_04/renovado2/RF04%20-%20Configurar%20Integraci%C3%B3n%20de%20Veterinarias%20-%20renovado2.pdf).
- **RF05 · No obligatorio.** Administrar campañas. Consulta administrativa y guardado con el mismo modelo que RF19; incluye campañas fuera de vigencia y participantes tipados. Se mantiene el par original. [Contrato](no_obligatorios/rf_05/renovado/plantilla_rf05_renovado.docx) · [Diagrama](no_obligatorios/rf_05/renovado/RF05%20-%20Administrar%20Campa%C3%B1as%20Municipales%20-%20renovado.pdf).
- **RF06 · Obligatorio.** Registrar mascotas. NRM entero positivo; microchip opcional y prioritario al deduplicar; se incorporó castrado. El código 0 reutiliza el NRM existente. DatosMascota se reutiliza en RF12/RF13. [Contrato](obligatorios/rf_06/renovado2/plantilla_rf06_renovado2.docx) · [Diagrama](obligatorios/rf_06/renovado2/RF06%20-%20Registrar%20Mascota%20-%20renovado2.pdf).
- **RF07 · No obligatorio.** Actualizar mascotas. Cambios parciales, castrado y datos del responsable coherentes con RF06/RF09; se conserva NRM e historial. La consulta previa se reutiliza de RF08. [Contrato](no_obligatorios/rf_07/renovado/plantilla_rf07_renovado.docx) · [Diagrama](no_obligatorios/rf_07/renovado/RF07%20-%20Actualizar%20Informaci%C3%B3n%20de%20Mascotas%20-%20renovado.pdf).
- **RF08 · No obligatorio.** Consultar mascotas. Búsqueda por NRM, nombre, CUIL o microchip, limitada por perfil tanto en registros como en campos. Castrado se comparte con RF06/RF07; [] es una consulta válida. [Contrato](no_obligatorios/rf_08/renovado/plantilla_rf08_renovado.docx) · [Diagrama](no_obligatorios/rf_08/renovado/RF08%20-%20Consultar%20Mascotas%20Registradas%20-%20renovado.pdf).
- **RF09 · Obligatorio.** Registrar información sanitaria. NRM e identificador de veterinaria enteros; vencimiento opcional; prácticas del programa, autorización de la fuente e historial preservado. Su proyección alimenta RF10/RF11. [Contrato](obligatorios/rf_09/renovado2/plantilla_rf09_renovado2.docx) · [Diagrama](obligatorios/rf_09/renovado2/RF09%20-%20Registrar%20Informaci%C3%B3n%20Sanitaria%20-%20renovado2.pdf).
- **RF10 · Obligatorio.** Generar carnet. Carnet único por NRM con responsable e información sanitaria vigente; se igualó Token a RF15 y se aclaró la relación entre código actual y códigos anteriores. [Contrato](obligatorios/rf_10/renovado2/plantilla_rf10_renovado2.docx) · [Diagrama](obligatorios/rf_10/renovado2/RF10%20-%20Generar%20Carnet%20Sanitario%20Digital%20-%20renovado2.pdf).
- **RF11 · Obligatorio.** Consultar y validar carnet. La consulta reutiliza exactamente Carnet de RF10. La validación de terceros devuelve sólo autenticidad, vigencia y NRM cuando procede. Token uniforme; resultado negativo distinto de fallo técnico. [Contrato](obligatorios/rf_11/renovado2/plantilla_rf11_renovado2.docx) · [Diagrama](obligatorios/rf_11/renovado2/RF11%20-%20Consultar%20y%20Validar%20Carnet%20Sanitario%20Digital%20-%20renovado2.pdf).
- **RF12 · No obligatorio.** Gestionar animales bajo cuidado. Consulta limitada al refugio y guardado atómico; alta o reutilización mediante DatosMascota de RF06. La descripción sanitaria de seguimiento no reemplaza las prácticas oficiales de RF09. [Contrato](no_obligatorios/rf_12/renovado/plantilla_rf12_renovado.docx) · [Diagrama](no_obligatorios/rf_12/renovado/RF12%20-%20Gestionar%20Animales%20Bajo%20Cuidado%20-%20renovado.pdf).
- **RF13 · Obligatorio.** Gestionar publicaciones. Se incorporó consulta propia y se completó el guardado con foto, condiciones de adopción y alta de mascota cuando falta NRM. El cuidado se articula con RF12; sólo Activa es pública y sólo puede existir una Activa por NRM. [Contrato](obligatorios/rf_13/renovado/plantilla_rf13_renovado.docx) · [Diagrama](obligatorios/rf_13/renovado/RF13%20-%20Gestionar%20Publicaciones%20de%20Adopci%C3%B3n%20-%20renovado.pdf).
- **RF14 · No obligatorio.** Gestionar adopciones. Solicitud ciudadana, consulta y decisión del refugio. APROBADA puede finalizar la interacción sin concretar todavía. CONCRETADA cambia responsable y cierra cuidado/publicación juntos, conservando historial. [Contrato](no_obligatorios/rf_14/renovado/plantilla_rf14_renovado.docx) · [Diagrama](no_obligatorios/rf_14/renovado/RF14%20-%20Gestionar%20Procesos%20de%20Adopci%C3%B3n%20-%20renovado.pdf).
- **RF15 · Obligatorio.** Autenticar usuarios. El backend determina perfil y permisos. AutenticarUsuario y el adaptador lógico de identidad tienen contratos diferenciados; CiDi corresponde al proyecto completo y la autenticación local de refugios al Parcial III. [Contrato](obligatorios/rf_15/renovado/plantilla_rf15_renovado.docx) · [Diagrama](obligatorios/rf_15/renovado/RF15%20-%20Autenticar%20Usuarios%20-%20renovado.pdf).
- **RF16 · Obligatorio.** Acceder al portal. Acceso público sin sesión y personalización con Token válido. MostrarPortalWeb devuelve datos de navegación; Angular construye la interfaz y cada servicio aplica sus permisos. [Contrato](obligatorios/rf_16/renovado2/plantilla_rf16_renovado2.docx) · [Diagrama](obligatorios/rf_16/renovado2/RF16%20-%20Acceder%20al%20Portal%20del%20Programa%20Municipal%20de%20Bienestar%20Animal%20-%20renovado2.pdf).
- **RF17 · Obligatorio.** Consultar veterinarias. Consulta pública con filtros; sólo habilitadas y con información de ubicación, contacto, horarios y servicios. Modelo definido y lista vacía válida. [Contrato](obligatorios/rf_17/renovado/plantilla_rf17_renovado.docx) · [Diagrama](obligatorios/rf_17/renovado/RF17%20-%20Consultar%20Veterinarias%20Adheridas%20-%20renovado.pdf).
- **RF18 · Obligatorio.** Consultar refugios. Consulta pública de habilitados, horarios y publicaciones Activas de RF13. Se corrigió el objeto copiado como Veterinarias; no se exponen datos privados del responsable. [Contrato](obligatorios/rf_18/renovado/plantilla_rf18_renovado.docx) · [Diagrama](obligatorios/rf_18/renovado/RF18%20-%20Consultar%20Refugios%20Adheridos%20-%20renovado.pdf).
- **RF19 · No obligatorio.** Consultar campañas. Vista pública del modelo de RF05, con fecha municipal inclusiva, lugares y participantes. Actualizaciones de administración reflejadas durante vigencia; [] es un resultado válido. Se mantiene el par original. [Contrato](no_obligatorios/rf_19/renovado/plantilla_rf19_renovado.docx) · [Diagrama](no_obligatorios/rf_19/renovado/RF19%20-%20Consultar%20Campa%C3%B1as%20Municipales%20-%20renovado.pdf).
- **RF20 · Obligatorio.** Consultar mis mascotas. Se obtiene el ciudadano de la sesión; se retiró el CUIL arbitrario de entrada. Incluye estado general y acceso por NRM al carnet de RF11, sin duplicar historia clínica. [Contrato](obligatorios/rf_20/renovado/plantilla_rf20_renovado.docx) · [Diagrama](obligatorios/rf_20/renovado/RF20%20-%20Consultar%20Mis%20Mascotas%20-%20renovado.pdf).
- **RF21 · Obligatorio.** Consultar asistente. Se unificó ConsultarAsistente y se incorporó el contrato lógico con IA generativa. Consulta general sin Token; personalización con permisos, contexto limitado e indisponibilidad sin bloquear el portal. [Contrato](obligatorios/rf_21/renovado/plantilla_rf21_renovado.docx) · [Diagrama](obligatorios/rf_21/renovado/RF21%20-%20Consultar%20Asistente%20Virtual%20-%20renovado.pdf).

## Decisiones de diseño y puntos para validar

La especificación fija capacidades y reglas de negocio, pero deja decisiones al diseño. Conviene explicarlas como decisiones propuestas al defender los diagramas:

- **Animales de refugio sin propietario conocido.** RF06 pide identificar al propietario y RF12/RF13 permiten ingresar animales del refugio. Se interpretan en conjunto: propietario obligatorio cuando se conoce; en el otro caso se conserva la asociación al refugio sin inventar un CUIL. Esta excepción debe validarse con la docente o el responsable funcional.
- **Información adicional de adhesión.** OBSERVADA conserva la solicitud y la organización aporta lo pedido por el canal administrativo acordado; la evaluación referencia esa documentación. No se presupone un servicio de correo, notificaciones o respuesta digital. Si se exige responder dentro del portal, habrá que ampliar RF01/RF02 y sus contratos.
- **Carnet.** El mínimo sanitario y las reglas concretas de vigencia pertenecen al programa municipal. Se propone un código asociado al contenido y conservar códigos anteriores para distinguir “auténtico” de “vigente”. No se fija una duración general ni un QR obligatorio.
- **Estados y representación.** Año de nacimiento expresa la edad aproximada admitida por RF06. Las longitudes de campos, las transiciones de publicaciones (sin reabrir Finalizada) y los estados intermedios de solicitudes son decisiones de diseño documentadas, no citas literales de la especificación.
- **Proveedores externos.** CiDi e IA se documentan mediante adaptadores lógicos. Endpoints, protocolo de identidad, credenciales y parámetros reales se precisan con el proveedor al implementar; no se inventa una API oficial.

## Verificación realizada

Se revisaron reglas, datos, permisos, resultados y dependencias contra la especificación completa y el enunciado del parcial. Se comprobó que los 21 pares estén presentes, que las operaciones de los diagramas tengan contrato y que los tipos compartidos no se contradigan.

Se renderizaron los 21 contratos (73 páginas) y los 21 diagramas (22 páginas). Se inspeccionaron las páginas; los contratos ajustados mantienen encabezados, pies, numeración y geometría de sus originales. Sólo se modificó el cuerpo de los DOCX, con adición del estilo de título y actualización de campos donde faltaban. No se modificaron código, base de datos ni especificación.

Para la exposición, cada integración puede explicarse con cinco preguntas: quién solicita, quién provee, qué operación se invoca, qué datos recibe/devuelve y qué ocurre si falla. Las validaciones internas y la base de datos son responsabilidad del proveedor; no se convierten en servicios adicionales por cada paso del diagrama.
