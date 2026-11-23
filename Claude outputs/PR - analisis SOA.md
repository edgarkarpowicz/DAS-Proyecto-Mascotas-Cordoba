## Análisis SOA de integraciones — Mascotas Córdoba

Documentación del análisis SOA de los requerimientos funcionales (obligatorios y no obligatorios), con las correcciones que hizo la profesora después de la presentación. **No toca código**: todos los cambios están en `docs/requerimientos_funcionales/`.

### Criterio
Una integración es un punto donde Mascotas Córdoba invoca el sistema de **otra organización**. El portal y la base de datos propia no cuentan como integraciones.

### Integraciones identificadas (3)
| # | Sistema externo | RF | Modalidad |
|---|---|---|---|
| 1 | Sistema de Gestión de la Veterinaria | RF06, RF09 (configurada en RF04) | Por lote |
| 2 | CiDi | RF15 | En línea |
| 3 | IA generativa | RF21 | En línea (ReAct + RAG) |

### Correcciones de la profesora (renovado10)
- **Rango de fechas** (`fechaDesde` / `fechaHasta`) en lugar de `idLote` y cursor.
- **Paginación opcional** (`pagina`, `tamanioPagina`, `totalPaginas`).
- **Información sanitaria planchada** en RF09: un registro por práctica (`practicas`), con el profesional concatenado.
- **RF21**: recuperar la información (RAG) y armar el prompt ReAct es **un mismo paso**, con una sola invocación y el campo `contexto`.

### Resto de los RF (16 sin integración)
Cada uno tiene un diagrama del proceso interno y la ficha **"Por qué no es una integración"**.

### Integraciones candidatas (A–G)
Posibles integraciones adicionales (refugios, identificación municipal, terceros que validan el carnet, veterinaria que envía y actualización de mascotas), alineadas a las correcciones de renovado10.

### Organización
- `integraciones/identificadas/`: el informe corto (*Integraciones - Mascotas Cordoba.pdf*), el anexo SOA detallado y una carpeta por integración con su PDF y su plantilla.
- `integraciones/candidatas/`: A–G en docx y pdf, más el PDF consolidado.
- `obligatorios/rf_XX/` y `no_obligatorios/rf_XX/`: una carpeta por versión (renovado3 … renovado10).
- Se conservan **todas las versiones anteriores** (`versiones_anteriores/` y las carpetas por versión) para trazabilidad.
- `README.md` explica qué cambió en cada versión y dónde está lo que se presenta.

🤖 Generated with [Claude Code](https://claude.com/claude-code)

https://claude.ai/code/session_01A8u8bjmfxr6CmUbiTN4Ksh
