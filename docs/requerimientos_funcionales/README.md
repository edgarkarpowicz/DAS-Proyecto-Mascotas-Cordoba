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
